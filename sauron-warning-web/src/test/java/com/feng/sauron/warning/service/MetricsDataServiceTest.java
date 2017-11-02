package com.feng.sauron.warning.service;

import com.alibaba.fastjson.JSON;
import com.feng.sauron.warning.service.hbase.HbaseMetricsOriDataService;
import com.feng.sauron.warning.web.vo.MetricsOriDataVO;
import com.fengjr.sauron.dao.hbase.vo.Range;
import com.fengjr.sauron.dao.model.MetricsOriData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * Created by lianbin.wang on 2016/11/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:dao-config.xml", "classpath:task-config.xml", "classpath:hbase-config.xml"})
public class MetricsDataServiceTest {

    @Autowired
    private MetricsDataService service;

    @Test
    public void testLoad() {
        List<MetricsOriDataVO> result = service.loadScatterData(new Date(), new Date(), "sauron", null);
        System.out.println(JSON.toJSONString(result));
    }


    @Autowired
    HbaseMetricsOriDataService hbaseMetricsOriDataService;

    @Test
    public void testGetTrace() {
        String appName = "sauron";
        String traceid = "1479802022027^0f00aff49a1";
        List<MetricsOriData> list = hbaseMetricsOriDataService.getTraceTree(appName, traceid);

        System.out.println(list.size());

        for (MetricsOriData data : list) {
            System.out.println(JSON.toJSONString(data));
        }
    }

    @Test
    public void testScatter(){
        String app = "sauron_fengfd_trade";
        long st = 1486373788085L;
        long end = 1486374088085L;
        Range range = new Range(1486373788085L,1486374088085L);


        List datas = service.loadScatterData(new Date(st),new Date(end),app,"");
        System.out.println(datas);
    }

}
