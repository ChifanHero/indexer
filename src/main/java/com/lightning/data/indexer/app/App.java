package com.lightning.data.indexer.app;

import java.util.Timer;

import org.parse4j.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lightning.data.indexer.notification.db.Notification;
import com.lightning.data.indexer.orchetration.TaskExecutor;

public class App {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(App.class);
	private final static String APP_ID = "Z6ND8ho1yR4aY3NSq1zNNU0kPc0GDOD1UZJ5rgxM";
	private final static String REST_API_ID = "2mf4ECBjkrhFaB9PVwCN0iTDUYUTO1hKKqW7MyQc";
	
	private static final long ONE_HOUR = 60 * 60 * 1000;
	
	public static void main(String[] args) {
		initialize();
//		scheduleJob();
		createIndices();
		performOneTimeJob();
	}

	private static void createIndices() {
		IndexManager.getInstance().createIndex();
	}

	private static void performOneTimeJob() {
		Notification notification = new Notification();
		notification.setTriggerTime(System.currentTimeMillis());
		new RestaurantIndexExecutor().index();
//		new DishIndexApp().index();
//		new DishListIndexApp().index();
		TaskExecutor.shutDownWhenComplete();
		IndexManager.getInstance().setAliasToNewIndex();
		IndexManager.getInstance().deleteOldIndex();
		notification.setFinishTime(System.currentTimeMillis());
		notification.send();
	}

	private static void initialize() {
		LOGGER.info("Initializing....");
		Parse.initialize(APP_ID, REST_API_ID);
	}
	
	private static long getPeriod() {
		return 4 * ONE_HOUR;
	}
	
	@SuppressWarnings("unused")
	private static void scheduleJob() {
		Timer timer = new Timer();
		ScheduledIndexingJob job = new ScheduledIndexingJob();
		timer.schedule(job, 0, getPeriod());
	}
}
