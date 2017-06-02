package com.zhaiugo.photopicker.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ImagePoolExecutor {

	private static ImagePoolExecutor instance;
	private ThreadPoolExecutor executor;

	public static ImagePoolExecutor getInstance() {
		if (instance == null)
			instance = new ImagePoolExecutor();
		return instance;
	}

	private ImagePoolExecutor() {
		int cpuQuantity = Runtime.getRuntime().availableProcessors();
		if (cpuQuantity > 3)
			cpuQuantity = 3;
		BlockingQueue<Runnable> blockingDeque = new LinkedBlockingQueue<Runnable>();
		executor = new ThreadPoolExecutor(cpuQuantity, Thread.MIN_PRIORITY + 2, 40000, TimeUnit.MILLISECONDS, blockingDeque, new ThreadPoolExecutor.DiscardOldestPolicy());

	}

	public boolean isQueueEmpty() {
		return executor.getQueue().isEmpty();
	}

	public int getQueueSize() {
		return executor.getQueue().size();
	}

	public Executor getExecutor() {
		return executor;
	}
}
