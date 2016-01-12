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
import com.lightning.data.indexer.document.DishDocument;
import com.lightning.data.indexer.document.GeoPoint;
import com.lightning.data.indexer.document.NestedDishList;
import com.lightning.data.indexer.document.NestedMenu;
import com.lightning.data.indexer.document.NestedRestaurant;
import com.lightning.data.indexer.document.Picture;
import com.lightning.data.indexer.meta.Indices;
import com.lightning.data.indexer.meta.ParseClass;
import com.lightning.data.indexer.meta.Types;
import com.lightning.data.indexer.util.DateConverter;

import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;

public class DishIndexTask implements Runnable{
	
	List<ParseObject> dishes;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DishIndexTask.class);
	
	public DishIndexTask(List<ParseObject> dishes) {
		this.dishes = dishes;
	}
	
	@Override
	public void run() {
		if (dishes != null && dishes.size() > 0) {
			JestResult indexResult = indexDishes();
			if (indexResult.isSucceeded()) {
				LOGGER.info("Success");
			} else {
				LOGGER.error(indexResult.getErrorMessage());
			}
		}
	}
	
	private JestResult indexDishes() {
		if (dishes == null || dishes.size() == 0) {
			return null;
		} else {
			List<DishDocument> documents = convert(dishes);
			expandDocuments(documents);
			if (documents == null || documents.size() == 0) {
				return null;
			} else {
				return bulkIndexDishDocument(documents);
			}
		}
	}
	
	private void expandDocuments(List<DishDocument> documents) {
		if (documents == null || documents.isEmpty()) {
			return;
		} else {
			Map<String, DishDocument> documentDictionary = new HashMap<String, DishDocument>();
			for (DishDocument document : documents) {
				if (document == null || document.getObjectId() == null || document.getObjectId().isEmpty()) {
					continue;
				}
				documentDictionary.put(document.getObjectId(), document);
			}
			List<ParseObject> listMembers = getListMembers (documentDictionary.keySet());
			assembleDishDocuments(listMembers, documentDictionary);
		}
	}

	private void assembleDishDocuments(List<ParseObject> listMembers, Map<String, DishDocument> documentDictionary) {
		if (listMembers == null || listMembers.size() <= 0 || documentDictionary == null || documentDictionary.size() <= 0) return;
		for (ParseObject listMember : listMembers) {
			ParseObject dish = listMember.getParseObject("dish");
			ParseObject list = listMember.getParseObject("list");
			if (dish == null || list == null) {
				continue;
			} else {
				DishDocument document = documentDictionary.get(dish.getObjectId());
				List<NestedDishList> lists = document.getLists();
				if (lists == null) {
					lists = new ArrayList<NestedDishList>();
					document.setLists(lists);
				}
				NestedDishList dishList = new NestedDishList();
				dishList.setObjectId(list.getObjectId());
				dishList.setName(list.getString("name"));
				lists.add(dishList);
			}
		}
	}

	private List<ParseObject> getListMembers (Set<String> keySet) {
		if (keySet == null || keySet.isEmpty()) {
			return Collections.emptyList();
		}
		List<ParseObject> listMembers = new ArrayList<ParseObject>();	
		Object lastCreationDate = null;
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.LIST_MEMBER);
		query.include("list");
		ParseQuery<?> innerQuery = ParseQuery.getQuery(ParseClass.DISH);
		innerQuery.whereContainedIn("objectId", keySet);
		query.whereMatchesQuery("dish", innerQuery);
		query.limit(1000);
		query.orderByAscending("createdAt");
		boolean loadMore = true;
		while(loadMore) {
			if (lastCreationDate != null) {
				query.whereGreaterThan("createdAt", lastCreationDate);
			}
			List<ParseObject> results = null;
			try {
				results = query.find();
			} catch (ParseException e) {
				LOGGER.error("Error getting dishes for restaurants", e);
			}
			if (results == null || results.size() < 1000 ) {
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

	private List<DishDocument> convert(List<ParseObject> dishes) {
		if (dishes == null || dishes.size() == 0) {
			return Collections.emptyList();
		}
		List<DishDocument> documents = new ArrayList<DishDocument>();
		for (ParseObject dish : dishes) {
			DishDocument document = new DishDocument();
			document.setCreatedAt(DateConverter.convert(dish.getCreatedAt()));
			document.setDislikeCount(dish.getLong("dislike_count"));
			document.setEnglishName(dish.getString("english_name"));
			document.setFavoriteCount(dish.getLong("favorite_count"));
			ParseObject restaurant = dish.getParseObject("from_restaurant");
			if (restaurant != null) {
				NestedRestaurant fromRestaurant = new NestedRestaurant();
				ParseGeoPoint pg = restaurant.getParseGeoPoint("coordinates");
				if (pg != null) {
					GeoPoint coordinates = new GeoPoint();
					coordinates.setLat(pg.getLatitude());
					coordinates.setLon(pg.getLongitude());
					fromRestaurant.setCoordinates(coordinates);
					document.setFromRestaurant(fromRestaurant);
				}
				fromRestaurant.setEnglishName(restaurant.getString("english_name"));
				fromRestaurant.setName(restaurant.getString("name"));
				fromRestaurant.setObjectId(restaurant.getObjectId());
				document.setFromRestaurant(fromRestaurant);
			}
			document.setLikeCount(dish.getLong("like_count"));
			ParseObject pmenu = dish.getParseObject("menu");
			if (pmenu != null) {
				NestedMenu menu = new NestedMenu();
				menu.setEnglishName(pmenu.getString("english_name"));
				menu.setName(pmenu.getString("name"));
				menu.setObjectId(pmenu.getObjectId());
				document.setMenu(menu);
			}
			document.setName(dish.getString("name"));
			document.setNeutralCount(dish.getLong("neutral_count"));
			document.setObjectId(dish.getObjectId());
			ParseObject image = dish.getParseObject("image");
			if (image != null) {
				Picture picture = new Picture();
				picture.setOriginal(image.getString("original"));
				picture.setThumbnail(image.getString("thumbnail"));
				document.setPicture(picture);
			}
			document.setUpdatedAt(DateConverter.convert(dish.getUpdatedAt()));
			documents.add(document);
		}
		return documents;
	}
	

	private JestResult bulkIndexDishDocument(List<DishDocument> documents) {
		if (documents == null || documents.size() <= 0) {
			return null;
		} 
		List<Index> actions = new ArrayList<Index>();
		for (DishDocument document : documents) {
			String doc = null;
			doc = document.getJSONRepresentation().toString();
			if (doc != null) {
				Index index = new Index.Builder(doc).build();
				actions.add(index);
			}
		}
		if (actions.size() > 0) {
			Bulk bulk = new Bulk.Builder().defaultIndex(Indices.FOOD).defaultType(Types.DISH).addAction(actions).build();
			try {
				return ElasticsearchRestClientFactory.getRestClient().execute(bulk);
			} catch (IOException e) {
				LOGGER.error("Error indexing dishes in bulk", e);
				e.printStackTrace();
			}
		} 
		return null;
	}

	

}