package com.feng.sauron.warning.service;

import com.feng.sauron.warning.BaseTest;
import com.fengjr.sauron.dao.hbase.impl.HbaseMetricsOriData;
import com.fengjr.sauron.dao.hbase.vo.Range;
import com.fengjr.sauron.dao.model.MetricsOriData;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/2.
 */
public class TestHbaseDao extends BaseTest {

    @Resource
    HbaseMetricsOriData hbaseMetricsOriData;

//    @Autowired
//    private HbaseOperations2 hbaseTemplate;

    @Test
    public void createTable(){

       // metrics_ori_data
    }


    @Test
    public void testInsert(){

       // org.apache.log4j.Log4jLoggerFactory
        System.out.println("abddee");
        MetricsOriData metricsOriData = new MetricsOriData();
        metricsOriData.setTraceId("1478244366394^sdfds011");
        metricsOriData.setAppName("Locksmith");
        metricsOriData.setMethodName("com.test.apc");
        metricsOriData.setSpanId("0.2");
        metricsOriData.setVersion("v3");
        hbaseMetricsOriData.insert(metricsOriData);
        //org.apache.log4j.Log4jLoggerFactory
        System.out.println("absdfsdf");

    }
    @Test
    public void testQuery(){
        String appName = "sauron_usercenter";
        String traceId = "1484797180528^014187760df07";
        List<MetricsOriData> datas =  hbaseMetricsOriData.getMetricsOriData(appName,traceId);
        System.err.println("data:----------- "+datas);
        for(MetricsOriData data:datas){
            System.err.println("appName:"+ data.getAppName()+",traceId:"+data.getTraceId() + ",method:"+ data.getMethodName() + ",spangId:"+data.getSpanId());
        }
    }

    @Test
    public void testRange(){
        String app = "sauron_fengfd_trade";
        Range range = new Range(1484701875539L,1484705475539L);


        Object ob = hbaseMetricsOriData.getMetricsOriDataList(app,range);
        System.err.println("resut:"+ob);
        System.err.println("resut:"+ob);



    }




}
