package com.lightning.data.indexer.app;

import org.parse4j.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lightning.data.indexer.orchetration.TaskExecutor;

public class App {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(App.class);
	private final static String APP_ID = "Z6ND8ho1yR4aY3NSq1zNNU0kPc0GDOD1UZJ5rgxM";
	private final static String REST_API_ID = "2mf4ECBjkrhFaB9PVwCN0iTDUYUTO1hKKqW7MyQc";
	
	public static void main(String[] args) {
		initialize();
//		new RestaurantIndexApp().index();
//		new DishIndexApp().index();
		new DishListIndexApp().index();
		TaskExecutor.shutDownWhenComplete();
	}

	private static void initialize() {
		LOGGER.info("Initializing....");
		Parse.initialize(APP_ID, REST_API_ID);
	}
}
