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

public class ElasticSearchBulkCommit {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static BulkRequestBuilder prepareBulk = ElasticSearchManager.getInstance().getClient().prepareBulk();

	private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(15, 15, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1));

	private static class ElasticSearchManagerHolder {
		private static final ElasticSearchBulkCommit INSTANCE = new ElasticSearchBulkCommit();
	}

	public static final ElasticSearchBulkCommit getInstance() {
		return ElasticSearchManagerHolder.INSTANCE;
	}

	private ReadWriteLock rwl = new ReentrantReadWriteLock();

	private ElasticSearchBulkCommit() {

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
		Runtime.getRuntime().addShutdownHook(new HookSubmitThread());
	}

	public void createIndexBulk(String index, String type, String id, byte[] source) {
		IndexRequestBuilder builder = ElasticSearchManager.getInstance().getClient().prepareIndex(index, type, id).setSource(source);

		try {
			rwl.writeLock().lock();
			prepareBulk.add(builder);
		} finally {
			rwl.writeLock().unlock();
		}

		if (prepareBulk.numberOfActions() >= 500) {
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
					logger.error("插入es 部分失败...time:" + (System.currentTimeMillis() - currentTimeMillis));
				} else {
					logger.info("{}条全成功...time:{}ms ", numberOfActions, (System.currentTimeMillis() - currentTimeMillis));
				}
			}
			prepareBulk = null;
		} catch (Exception e) {
			prepareBulk = null;
			logger.error("prepareBulk.execute.error...{}", e);
		}
	}

	public BulkRequestBuilder get() {
		BulkRequestBuilder tmpBuilder = prepareBulk;
		prepareBulk = ElasticSearchManager.getInstance().getClient().prepareBulk();
		return tmpBuilder;
	}

	class TaskSubmitThread extends Thread {
		@Override
		public void run() {
			this.setName("EsBulkCommit");

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

	class HookSubmitThread extends Thread {
		@Override
		public void run() {
			execute(get());
		}
	}
}
