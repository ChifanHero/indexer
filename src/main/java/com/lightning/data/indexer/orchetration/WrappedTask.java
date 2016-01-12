package com.lightning.data.indexer.orchetration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrappedTask implements Runnable{

	private long delay = 0;
	private Runnable task;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(WrappedTask.class);
	
	public WrappedTask(Runnable task, long delay) {
		this.delay = delay;
		this.task = task;
	}
	
	@Override
	public void run() {
		try {
			LOGGER.info("Sleeping for " + this.delay/1000 + " second(s)");
			Thread.sleep(this.delay);
		} catch (InterruptedException e) {
			LOGGER.error("Sleep interrupted", e);
		}
		this.task.run();
		TaskExecutor.semaphore.release();
	}
}
