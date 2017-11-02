import com.feng.sauron.warning.dao.UrlMonitorMapper;
import com.feng.sauron.warning.domain.App;
import com.feng.sauron.warning.domain.UrlMonitor;
import com.feng.sauron.warning.service.base.UrlMonitorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * TestUrlMonitorService
 * Created by jianzhang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@ContextConfiguration(locations={"classpath:dao-config.xml","classpath:task-config.xml"})
public class TestUrlMonitorService {

    @Autowired
    private UrlMonitorService urlMonitorService;

    @Test
    public void addUrlMonitor(){
        UrlMonitor urlMonitor = new UrlMonitor();
        urlMonitor.setFailTimes(10);
        urlMonitor.setTotalTimes(1);
        urlMonitor.setUrlRulesId(2l);
        urlMonitorService.addUrlMonitor(urlMonitor);
    }

    @Test
    public void update(){
        UrlMonitor urlMonitor = urlMonitorService.findUrlMonitorById(1);
        urlMonitorService.updateUrlMonitor(urlMonitor);
    }

    @Test
    public void updateByUrlRuleId(){
        urlMonitorService.updateByUrlRuleId(2l,20,null);
    }

    @Test
    public void deleteUrlMonitor(){
        urlMonitorService.delUrlMonitor(1);
    }

    @Test
    public void deleteUrlMonitorByUrlRuleId(){
        urlMonitorService.delUrlMonitorByUrlRuleId(2);
    }

    @Test
    public void getUrlMonitorTest(){
        List<Map<String,Object>> list= urlMonitorService.findByPage(1,10,"2","","");
        int count = urlMonitorService.findCount(null,"","");
        for(Map<String,Object> map : list){
            System.out.println(map.get("appName"));
            System.out.println(map.get("failTimes"));
        }
        System.out.println(list.size());
        System.out.println(count);
    }

}
