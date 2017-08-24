package com.feng.sauron.warning.web.vo;

import java.io.Serializable;

/**
 * DubboMonitor
 * Created by jianzhang
 */
public class DubboMonitor implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String appName;
    private String applicationName;
    private int isAlive; // 0 活着   1 死了


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public int getIsAlive() {
        return isAlive;
    }

    public void setIsAlive(int isAlive) {
        this.isAlive = isAlive;
    }

    public enum  IsAlive{
        died(1),alived(0);
        private int val;
        IsAlive(int val){
            this.val = val;
        }
        public int getVal(){
            return  val;
        }
    }

}
