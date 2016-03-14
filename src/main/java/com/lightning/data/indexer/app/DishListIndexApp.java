package com.lightning.data.indexer.app;

import java.util.ArrayList;
import java.util.List;

import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lightning.data.indexer.DishListIndexTask;
import com.lightning.data.indexer.meta.ParseClass;
import com.lightning.data.indexer.orchetration.TaskExecutor;

public class DishListIndexApp {
	
private static final int BATCH_COUNT = 100;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DishListIndexApp.class);
	
	private int counter = 1;
	
	public void index() {
		LOGGER.info("Indexing dish lists started ......");
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.DISH_LIST);
		query.orderByAscending("createdAt");
		query.limit(1000);
		query.include("image");
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
				LOGGER.error("Error finding dish lists", e);
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
					List<ParseObject> listsToIndex = new ArrayList<ParseObject>();
					for (int i = 0; i < end; i++) {
						listsToIndex.add(results.get(i));
					}
					LOGGER.info("Indexing dishes " + counter + " - " + (counter + listsToIndex.size() - 1));
					Runnable task = new DishListIndexTask(listsToIndex);
					TaskExecutor.submitTask(task, 8000);
					counter = counter + listsToIndex.size();
					results.removeAll(listsToIndex);
				}
			}
		}
		
		
	}

}
