package com.lightning.data.indexer.app;

import java.util.TimerTask;

//import com.lightning.data.indexer.orchetration.TaskExecutor;

public class ScheduledIndexingJob extends TimerTask{

	@Override
	public void run() {
		new RestaurantIndexExecutor().index();
		new DishIndexExecutor().index();
		new DishListIndexExecutor().index();
//		TaskExecutor.shutDownWhenComplete();
	}

}
