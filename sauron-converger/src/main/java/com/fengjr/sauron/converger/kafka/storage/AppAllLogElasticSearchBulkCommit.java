package com.fengjr.sauron.converger.kafka.storage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppAllLogElasticSearchBulkCommit {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private volatile BulkRequestBuilder prepareBulk = AppAllLogElasticSearchManager.getInstance().getClient().prepareBulk();

	private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

	private static class ElasticSearchManagerHolder {
		private static final AppAllLogElasticSearchBulkCommit INSTANCE = new AppAllLogElasticSearchBulkCommit();

	}

	private ReadWriteLock rwl = new ReentrantReadWriteLock();

	public static final AppAllLogElasticSearchBulkCommit getInstance() {
		return ElasticSearchManagerHolder.INSTANCE;
	}

	private AppAllLogElasticSearchBulkCommit() {

		threadPoolExecutor.setRejectedExecutionHandler(new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

				if (!executor.isShutdown()) {
					try {
						executor.getQueue().put(r);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		new TaskSubmitThread().start();
		Runtime.getRuntime().addShutdownHook(new AppHookSubmitThread());
	}

	public void createIndexBulk_bak(String index, String type, String source) {// 为了保护es 限速 4w/s

		IndexRequestBuilder builder = AppAllLogElasticSearchManager.getInstance().getClient().prepareIndex(index, type).setSource(source);

		try {
			rwl.writeLock().lock();
			prepareBulk.add(builder);

			if (prepareBulk.numberOfActions() >= 5000) {

				final BulkRequestBuilder tmpBuilder;

				tmpBuilder = get();

				threadPoolExecutor.submit(new Runnable() {

					@Override
					public void run() {
						execute(tmpBuilder);
					}
				});
			}
		} finally {
			rwl.writeLock().unlock();
		}

	}

	public void createIndexBulk(String index, String type, String source) {// 速度更快 5w+/s

		IndexRequestBuilder builder = AppAllLogElasticSearchManager.getInstance().getClient().prepareIndex(index, type).setSource(source);

		try {
			rwl.writeLock().lock();
			prepareBulk.add(builder);
		} finally {
			rwl.writeLock().unlock();
		}

		if (prepareBulk.numberOfActions() >= 5000) {

			final BulkRequestBuilder tmpBuilder;
			try {
				rwl.writeLock().lock();
				tmpBuilder = get();
			} finally {
				rwl.writeLock().unlock();
			}

			threadPoolExecutor.submit(new Runnable() {

				@Override
				public void run() {
					execute(tmpBuilder);
				}
			});
		}

	}

	private void execute(BulkRequestBuilder prepareBulk) {

		long currentTimeMillis = System.currentTimeMillis();
		try {

			if (prepareBulk == null) {
				return;
			}
			int numberOfActions = prepareBulk.numberOfActions();

			if (numberOfActions > 0) {

				logger.info("开始插入es:{}条...", numberOfActions);

				BulkResponse actionGet = prepareBulk.execute().actionGet();
				boolean hasFailures = actionGet.hasFailures();
				if (hasFailures) {
					long l = System.currentTimeMillis() - currentTimeMillis;
					// String buildFailureMessage = actionGet.buildFailureMessage();
					// System.out.println(buildFailureMessage);
					logger.error("插入es 部分失败...time:" + l);
				} else {
					long l = System.currentTimeMillis() - currentTimeMillis;

					logger.info("{}条全成功...time:{}ms ", numberOfActions, l);
				}
			}
			prepareBulk = null;
		} catch (Exception e) {
			prepareBulk = null;
			logger.error("App.prepareBulk.execute.error...{}", e);
		}
	}

	public BulkRequestBuilder get() {
		BulkRequestBuilder tmpBuilder = prepareBulk;
		prepareBulk = AppAllLogElasticSearchManager.getInstance().getClient().prepareBulk();
		return tmpBuilder;
	}

	class TaskSubmitThread extends Thread {
		@Override
		public void run() {
			this.setName("AppEsBulkCommit_task");

			while (!isInterrupted()) {
				try {
					Thread.sleep(30 * 1000);
					execute(get());
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	class AppHookSubmitThread extends Thread {
		@Override
		public void run() {
			execute(get());
		}
	}
}
