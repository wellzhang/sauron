package com.feng.sauron.warning.task;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by liuyuebin on 15/9/7.
 */
public class LeaderSelectionClient {
    private LeaderLatch leaderLatch;
    private Logger logger = LoggerFactory.getLogger(LeaderSelectionClient.class);

    public LeaderSelectionClient(CuratorFramework cfClient, String path) {
        leaderLatch = new LeaderLatch(cfClient, path);
        cfClient.start();
        try {
            leaderLatch.start();
        } catch (Exception e) {
            logger.debug("Leader Election Encounter a Problem!");
            e.printStackTrace();
        }
    }

    public boolean hasLeaderShip() {
        return leaderLatch.hasLeadership();
    }

    public void close() throws IOException {
        leaderLatch.close();
    }

}
