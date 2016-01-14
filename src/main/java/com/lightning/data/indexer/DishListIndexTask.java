package com.lightning.data.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.parse4j.ParseException;
import org.parse4j.ParseGeoPoint;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lightning.data.indexer.basic.ElasticsearchRestClientFactory;
import com.lightning.data.indexer.document.DishListDocument;
import com.lightning.data.indexer.document.GeoPoint;
import com.lightning.data.indexer.meta.Indices;
import com.lightning.data.indexer.meta.ParseClass;
import com.lightning.data.indexer.meta.Types;
import com.lightning.data.indexer.util.DateConverter;

import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;

public class DishListIndexTask implements Runnable {

	private List<ParseObject> dishLists;

	private final static Logger LOGGER = LoggerFactory.getLogger(RestaurantIndexTask.class);

	public DishListIndexTask(List<ParseObject> dishLists) {
		this.dishLists = dishLists;
	}

	@Override
	public void run() {
		if (dishLists != null && dishLists.size() > 0) {
			JestResult indexResult = indexDishLists();
			if (indexResult.isSucceeded()) {
				LOGGER.info("Success");
			} else {
				LOGGER.error(indexResult.getErrorMessage());
			}
		}
	}

	private JestResult indexDishLists() {
		if (dishLists == null || dishLists.size() == 0) {
			return null;
		} else {
			List<DishListDocument> documents = convert(dishLists);
			expandDocuments(documents);
			if (documents == null || documents.size() == 0) {
				return null;
			} else {
				return bulkIndexDishListDocument(documents);
			}
		}
	}

	private void expandDocuments(List<DishListDocument> documents) {
		if (documents == null || documents.isEmpty()) {
			return;
		} else {
			Map<String, DishListDocument> documentDictionary = new HashMap<String, DishListDocument>();
			for (DishListDocument document : documents) {
				if (document == null || document.getObjectId() == null || document.getObjectId().isEmpty()) {
					continue;
				}
				documentDictionary.put(document.getObjectId(), document);
			}
			List<ParseObject> members = getListMembers(documentDictionary.keySet());
			assembleDishListDocuments(members, documentDictionary);
		}
	}

	private void assembleDishListDocuments(List<ParseObject> members,
			Map<String, DishListDocument> documentDictionary) {
		if (members == null || members.size() <= 0 || documentDictionary == null || documentDictionary.size() <= 0)
			return;
		for (ParseObject member : members) {
			ParseObject dish = member.getParseObject("dish");
			ParseObject list = member.getParseObject("list");
			ParseObject fromRestaurant = null;
			if (dish != null) {
				fromRestaurant = dish.getParseObject("from_restaurant");
			}
			if (dish == null || list == null) {
				continue;
			} else {
				DishListDocument document = documentDictionary.get(list.getObjectId());
				fillDishNames(dish, document);
				if (fromRestaurant != null) {
					fillRestaurantNames(fromRestaurant, document);
					fillLocations(fromRestaurant, document);
				}
			}
		}
	}

	private void fillLocations(ParseObject fromRestaurant, DishListDocument document) {
		if (fromRestaurant == null || document == null) {
			return;
		}
		List<GeoPoint> locations = document.getLocations();
		if (locations == null) {
			locations = new ArrayList<GeoPoint>();
			document.setLocations(locations);
		}
		if (fromRestaurant.getParseGeoPoint("coordinates") != null) {
			ParseGeoPoint coordinates = fromRestaurant.getParseGeoPoint("coordinates");
			GeoPoint location = new GeoPoint();
			location.setLat(coordinates.getLatitude());
			location.setLon(coordinates.getLongitude());
			locations.add(location);
		}

	}

	private void fillRestaurantNames(ParseObject fromRestaurant, DishListDocument document) {
		if (fromRestaurant == null || document == null) {
			return;
		}
		List<String> restaurants = document.getRestaurants();
		if (restaurants == null) {
			restaurants = new ArrayList<String>();
			document.setRestaurants(restaurants);
		}
		String name = fromRestaurant.getString("name");
		String englishName = fromRestaurant.getString("english_name");
		restaurants.add(name);
		restaurants.add(englishName);
	}

	private void fillDishNames(ParseObject dish, DishListDocument document) {
		List<String> dishes = document.getDishes();
		if (dishes == null) {
			dishes = new ArrayList<String>();
			document.setDishes(dishes);
		}
		String dishName = dish.getString("name");
		String englishName = dish.getString("english_name");
		dishes.add(dishName);
		dishes.add(englishName);
	}

	private List<ParseObject> getListMembers(Set<String> keySet) {
		if (keySet == null || keySet.isEmpty()) {
			return Collections.emptyList();
		}
		List<ParseObject> listMembers = new ArrayList<ParseObject>();
		Object lastCreationDate = null;
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.LIST_MEMBER);
		query.include("dish.restaurant");
		ParseQuery<?> innerQuery = ParseQuery.getQuery(ParseClass.DISH_LIST);
		innerQuery.whereContainedIn("objectId", keySet);
		query.whereMatchesQuery("list", innerQuery);
		query.limit(1000);
		query.orderByAscending("createdAt");
		boolean loadMore = true;
		while (loadMore) {
			if (lastCreationDate != null) {
				query.whereGreaterThan("createdAt", lastCreationDate);
			}
			List<ParseObject> results = null;
			try {
				results = query.find();
			} catch (ParseException e) {
				LOGGER.error("Error getting members for lists", e);
			}
			if (results == null || results.size() < 1000) {
				loadMore = false;
			} else {
				lastCreationDate = results.get(results.size() - 1).getCreatedAt();
			}
			if (results != null && results.size() > 0) {
				listMembers.addAll(results);
			}
		}
		return listMembers;
	}

	private List<DishListDocument> convert(List<ParseObject> dishes) {
		if (dishes == null || dishes.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<DishListDocument> documents = new ArrayList<DishListDocument>();
			for (ParseObject dish : dishes) {
				if (dish == null || dish.getObjectId() == null || dish.getObjectId().isEmpty()) {
					continue;
				}
				DishListDocument document = new DishListDocument();
				document.setObjectId(dish.getObjectId());
				document.setName(dish.getString("name"));
				document.setCreatedAt(DateConverter.convert(dish.getCreatedAt()));
				document.setUpdatedAt(DateConverter.convert(dish.getUpdatedAt()));
				document.setLikeCount(dish.getLong("like_count"));
				document.setFavoriteCount(dish.getLong("favorite_count"));
				document.setMemberCount(dish.getLong("member_count"));
				ParseGeoPoint startingLocation = dish.getParseGeoPoint("starting_location");
				if (startingLocation != null) {
					List<GeoPoint> locations = new ArrayList<GeoPoint>();
					GeoPoint location = new GeoPoint();
					location.setLat(startingLocation.getLatitude());
					location.setLon(startingLocation.getLongitude());
					locations.add(location);
					document.setLocations(locations);
				}
				documents.add(document);
			}
			return documents;
		}
	}

	private JestResult bulkIndexDishListDocument(List<DishListDocument> documents) {
		if (documents == null || documents.size() <= 0) {
			return null;
		}
		List<Index> actions = new ArrayList<Index>();
		for (DishListDocument document : documents) {
			String doc = document.getJSONRepresentation().toString();
			if (doc != null) {
				Index index = new Index.Builder(doc).build();
				actions.add(index);
			}
		}
		if (actions.size() > 0) {
			Bulk bulk = new Bulk.Builder().defaultIndex(Indices.FOOD).defaultType(Types.DISHLIST).addAction(actions)
					.build();
			try {
				return ElasticsearchRestClientFactory.getRestClient().execute(bulk);
			} catch (IOException e) {
				LOGGER.error("Error indexing dishes in bulk", e);
			}
		}
		return null;
	}

}