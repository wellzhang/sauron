import com.feng.sauron.warning.domain.App;
import com.feng.sauron.warning.service.base.AppService;
import com.feng.sauron.warning.util.InfluxdbUtils;
import com.feng.sauron.warning.util.WatchableConfigClient;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TestAppService
 * Created by jianzhang
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@ContextConfiguration(locations={"classpath:dao-config.xml","classpath:task-config.xml"})
public class TestAppService {

    @Autowired
    private AppService appService;

    @Test
    public void addAppTest(){
        App app = new App();
        app.setName("ccccccccccccccccccccccccc");
        app.setDescribes("xxxxxxxxxxxxxxxxxxxxxxxxxx");
        app.setUserId("");
        appService.addApp(app);
    }

    @Test
    public void update(){
        App app = appService.findAppById(1);
        app.setName("test");
        appService.updateApp(app);
    }

    @Test
    public void deleteApp(){
        appService.delApp(1);
    }

    @Test
    public void getAppTest(){
        List<App> list= appService.findByPage(2,10,null,null);
        int count = appService.findCount("");
        System.out.println(list.size());
        System.out.println(count);
    }

    @Test
    public void getInfluxDbDate(){

        BatchPoints batchPoints =  BatchPoints.database("sauron_metrics_h5").retentionPolicy("sauron_h5").consistency(InfluxDB.ConsistencyLevel.ALL).tag("appName", "t_t").tag("hostName", "t_t").tag("method", "com.com").build();
        Point point = Point.measurement("sauron_h5").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS).field("duration", Double.parseDouble("111")).field("result", Integer.parseInt("11")).build();

        batchPoints.point(point);
        InfluxDB influxDB = InfluxdbUtils.getInfluxDB();

        influxDB.write(batchPoints);
    }


    @Test
    public void zkPathTest(){
//        WatchableConfigClient.getInstance()
    }



}
