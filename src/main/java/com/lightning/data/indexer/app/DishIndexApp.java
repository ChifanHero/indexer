package com.lightning.data.indexer.app;

import java.util.ArrayList;
import java.util.List;

import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lightning.data.indexer.DishIndexTask;
import com.lightning.data.indexer.meta.ParseClass;
import com.lightning.data.indexer.orchetration.TaskExecutor;

public class DishIndexApp {
	
private static final int BATCH_COUNT = 100;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DishIndexApp.class);
	
	private int counter = 1;
	
	public void index() {
		LOGGER.info("Indexing dishes started ......");
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClass.DISH);
		query.include("image");
		query.include("menu");
		query.include("from_restaurant");
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
					List<ParseObject> dishesToIndex = new ArrayList<ParseObject>();
					for (int i = 0; i < end; i++) {
						dishesToIndex.add(results.get(i));
					}
					LOGGER.info("Indexing dishes " + counter + " - " + (counter + dishesToIndex.size() - 1));
					Runnable task = new DishIndexTask(dishesToIndex);
					TaskExecutor.submitTask(task, 8000);
					counter = counter + dishesToIndex.size();
					results.removeAll(dishesToIndex);
				}
			}
		}
		
		
	}

}
