package com.lightning.data.indexer.app;

import java.util.ArrayList;
import java.util.List;

import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lightning.data.indexer.RestaurantIndexTask;
import com.lightning.data.indexer.meta.ParseClass;
import com.lightning.data.indexer.orchetration.TaskExecutor;

public class RestaurantIndexExecutor {
	
	private static final int BATCH_COUNT = 10;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RestaurantIndexExecutor.class);
	
	private int counter = 1;
	
	public void index() {
		LOGGER.info("Indexing restaurants started ......");
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.RESTAURANT);
		query.include("image");
		query.orderByAscending("createdAt");
		query.limit(1000);
		boolean loadMore = true;
		Object lastCreationDate = null;
		while(loadMore) {
			if (lastCreationDate != null) {
				query.whereGreaterThan("createdAt", lastCreationDate);
			} 
			List<ParseObject> results = null;
			try {
				results = query.find();
			} catch (ParseException e) {
				LOGGER.error("Error finding restaurants", e);
			}
			if (results == null || results.size() < 1000) {
				loadMore = false;
			} else {
				lastCreationDate = results.get(results.size() - 1).getCreatedAt();
			}
			if (results != null && results.size() > 0) {
				while (results.size() > 0) {
					int end = BATCH_COUNT;
					if (end > results.size()) {
						end = results.size();
					} 
					List<ParseObject> restaurantsToIndex = new ArrayList<ParseObject>();
					for (int i = 0; i < end; i++) {
						restaurantsToIndex.add(results.get(i));
					}
					LOGGER.info("Indexing restaurants " + counter + " - " + (counter + restaurantsToIndex.size() - 1));
					Runnable task = new RestaurantIndexTask(restaurantsToIndex);
					TaskExecutor.submitTask(task);
					counter = counter + restaurantsToIndex.size();
					results.removeAll(restaurantsToIndex);
				}
			}
		}
		
		
	}

}
