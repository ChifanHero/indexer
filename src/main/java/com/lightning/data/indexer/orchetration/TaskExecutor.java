package com.lightning.data.indexer.orchetration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskExecutor {
	
	private static ExecutorService executorService = Executors.newFixedThreadPool(10);
	public static volatile Semaphore semaphore = new Semaphore(20);
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TaskExecutor.class);
	
	private static final long DELAY = 10000;
	
	public static void submitTask(Runnable task) {
		submitTask(task, DELAY);
	}
	
	public static void submitTask(Runnable task, long delay) {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			LOGGER.error("Error acquiring semaphore", e);
		}
		Runnable wrappedTask = new WrappedTask(task, DELAY);
		try {
			executorService.submit(wrappedTask);
		} catch (Exception e) {
			LOGGER.error("Not able to submit task", e);
		}
		
	}
	
	public static void shutDownWhenComplete() {
		LOGGER.info("Executor shutting down");
		executorService.shutdown();
		try {
			executorService.awaitTermination(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			LOGGER.error("Error shutting down executorService", e);
		}
	}
	

}
