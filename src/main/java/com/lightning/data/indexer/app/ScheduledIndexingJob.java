package com.lightning.data.indexer.app;

import java.util.TimerTask;

//import com.lightning.data.indexer.orchetration.TaskExecutor;

public class ScheduledIndexingJob extends TimerTask{

	@Override
	public void run() {
		new RestaurantIndexApp().index();
		new DishIndexApp().index();
		new DishListIndexApp().index();
//		TaskExecutor.shutDownWhenComplete();
	}

}
