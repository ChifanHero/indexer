package com.lightning.data.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.parse4j.ParseException;
import org.parse4j.ParseFile;
import org.parse4j.ParseGeoPoint;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lightning.data.indexer.app.IndexManager;
import com.lightning.data.indexer.basic.ElasticsearchRestClientFactory;
import com.lightning.data.indexer.document.GeoPoint;
import com.lightning.data.indexer.document.Picture;
import com.lightning.data.indexer.document.RestaurantDocument;
import com.lightning.data.indexer.meta.ParseClass;
import com.lightning.data.indexer.meta.Types;
import com.lightning.data.indexer.util.DateConverter;
import com.lightning.data.indexer.util.ParseValidator;

import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;

public class RestaurantIndexTask implements Runnable{
	
	List<ParseObject> restaurants;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RestaurantIndexTask.class);
	
	public RestaurantIndexTask(List<ParseObject> restaurants) {
		this.restaurants = restaurants;
	}
	
	@Override
	public void run() {
		if (restaurants != null && restaurants.size() > 0) {
			JestResult indexResult = indexRestaurants();
			if (indexResult.isSucceeded()) {
				LOGGER.info("Success");
			} else {
				LOGGER.error(indexResult.getErrorMessage());
			}
		}
	}
	
	private JestResult indexRestaurants() {
		if (restaurants == null || restaurants.size() == 0) {
			return null;
		} else {
			List<RestaurantDocument> documents = convert(restaurants);
			expandDocuments(documents);
			if (documents == null || documents.size() == 0) {
				return null;
			} else {
				return bulkIndexRestaurantDocument(documents);
			}
		}
	}
			
	private void expandDocuments(List<RestaurantDocument> documents) {
		if (documents == null || documents.isEmpty()) {
			return;
		} else {
			Map<String, RestaurantDocument> documentDictionary = new HashMap<String, RestaurantDocument>();
			for (RestaurantDocument document : documents) {
				if (document == null || document.getObjectId() == null || document.getObjectId().isEmpty()) {
					continue;
				}
				documentDictionary.put(document.getObjectId(), document);
			}
			List<ParseObject> dishes = getAllDishesForRestaurnts(documentDictionary.keySet());
			assembleRestaurantDocuments(dishes, documentDictionary);
		}
	}
	
	private List<ParseObject> getAllDishesForRestaurnts(Set<String> keySet) {
		if (keySet == null || keySet.isEmpty()) {
			return Collections.emptyList();
		}
		List<ParseObject> dishes = new ArrayList<ParseObject>();
		List<ParseObject> results = null;
		Object lastCreationDate = null;
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.DISH);
		ParseQuery<?> innerQuery = ParseQuery.getQuery(ParseClass.RESTAURANT);
		innerQuery.whereContainedIn("objectId", keySet);
		query.whereMatchesQuery("from_restaurant", innerQuery);
		query.limit(1000);
		query.orderByAscending("createdAt");
		do {
			if (lastCreationDate != null) {
				query.whereGreaterThan("createdAt", lastCreationDate);
			}
			try {
				results = query.find();
			} catch (ParseException e) {
				LOGGER.error("Error getting dishes for restaurants", e);
			}
			if (results != null && results.size() > 0) {
				dishes.addAll(results);
				lastCreationDate = results.get(results.size() - 1).getCreatedAt();
			}
			
		} while (results != null && results.size() >= 1000);
		return dishes;
	}

	private void assembleRestaurantDocuments(List<ParseObject> dishes, Map<String, RestaurantDocument> restaurantToDoc) {
		if (dishes == null || dishes.isEmpty() || restaurantToDoc == null || restaurantToDoc.isEmpty()) {
			return;
		}
		for (ParseObject dish : dishes) {
			ParseObject fromRestaurant = dish.getParseObject("from_restaurant");
			if (ParseValidator.isValidObject(fromRestaurant)) {
				RestaurantDocument document = restaurantToDoc.get(fromRestaurant.getObjectId());
				List<String> dishNames = document.getDishes();
				if (dishNames == null) {
					dishNames = new ArrayList<String>();
					document.setDishes(dishNames);
				}
				if (dish.getString("name") != null) {
					dishNames.add(dish.getString("name"));
				}
				if (dish.getString("english_name") != null) {
					dishNames.add(dish.getString("english_name"));
				}
			}
		}
		
	}
	
	private List<RestaurantDocument> convert(List<ParseObject> restaurants) {
		if (restaurants == null || restaurants.size() == 0) {
			return Collections.emptyList();
		}
		List<RestaurantDocument> documents = new ArrayList<RestaurantDocument>();
		for (ParseObject restaurant : restaurants) {
			RestaurantDocument document = new RestaurantDocument();
			document.setAddress(restaurant.getString("address"));
			ParseGeoPoint pg = restaurant.getParseGeoPoint("coordinates");
			if (pg != null) {
				GeoPoint coordinates = new GeoPoint();
				coordinates.setLat(pg.getLatitude());
				coordinates.setLon(pg.getLongitude());
				document.setCoordinates(coordinates);
			}
			document.setCreatedAt(DateConverter.convert(restaurant.getCreatedAt()));
			document.setDislikeCount(restaurant.getLong("dislike_count"));
			document.setEnglishName(restaurant.getString("english_name"));
			document.setFavoriteCount(restaurant.getLong("favorite_count"));
			document.setLikeCount(restaurant.getLong("like_count"));
			document.setName(restaurant.getString("name"));
			document.setNeutralCount(restaurant.getLong("neutral_count"));
			document.setObjectId(restaurant.getObjectId());
			document.setPhone(restaurant.getString("phone"));
			ParseObject image = restaurant.getParseObject("image");
			if (image != null) {
				Picture picture = new Picture();
				ParseFile original = image.getParseFile("original");
				ParseFile thumbnail = image.getParseFile("thumbnail");
				if (original != null) {
					picture.setOriginal(original.getUrl());
				}
				if (thumbnail != null) {
					picture.setThumbnail(thumbnail.getUrl());
				}
				document.setPicture(picture);
			}
			document.setUpdatedAt(DateConverter.convert(restaurant.getUpdatedAt()));
			documents.add(document);
		}
		return documents;
	}
	
	private JestResult bulkIndexRestaurantDocument(List<RestaurantDocument> documents) {
		if (documents == null || documents.size() <= 0) {
			return null;
		} 
		List<Index> actions = new ArrayList<Index>();
		for (RestaurantDocument document : documents) {
			String doc = document.getJSONRepresentation().toString();
			Index index = new Index.Builder(doc).build();
			actions.add(index);
		}
		if (actions.size() > 0) {
			Bulk bulk = new Bulk.Builder().defaultIndex(IndexManager.getInstance().getIndexName()).defaultType(Types.RESTAURANT).addAction(actions).build();
			try {
				JestResult result =  ElasticsearchRestClientFactory.getRestClient().execute(bulk);
				return result;
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error("Error indexing restaurants in bulk", e);
			}
		} 
		return null;
	}


	
}