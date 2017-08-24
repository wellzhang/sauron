package com.feng.sauron.warning.monitor.dubbo;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年5月11日 下午5:53:34
 * 
 */
public interface ChildrenChangeWatcher {
	void onChildrenChanged(String path);

	Boolean isAlive();
}
