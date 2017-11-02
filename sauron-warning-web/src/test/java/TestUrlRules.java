import com.feng.sauron.warning.domain.UrlRules;
import com.feng.sauron.warning.service.base.UrlRulesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TestUrlRules
 * Created by jianzhang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@ContextConfiguration(locations={"classpath:dao-config.xml","classpath:task-config.xml"})
public class TestUrlRules {

    @Autowired
    private UrlRulesService urlRulesService;

    @Test
    public void addUrlRules(){
        UrlRules urlRules = new UrlRules();
        urlRules.setAppId(1l);
        urlRules.setAppName("111");
        urlRules.setCookies("afafafafalll");
        urlRules.setCreatorId(2l);
        urlRules.setCustomCode(111);
        urlRules.setHostIp("1111");
        urlRules.setCustomCode(809);
        urlRules.setIsContain((byte)1);
        urlRules.setIsConfigHost((byte)1);
        urlRules.setIsDefaultCode((byte)1);
        urlRules.setIsEnabled((byte)1);
        urlRules.setParam("...");
        urlRules.setMonitorKey("kkkkkkkkk");
        urlRules.setRequestInterval(11);
        urlRules.setMonitorUrl("111");
        urlRules.setMatchContent("1111");
        urlRules.setRequestMode((byte)1);
        urlRules.setTimeout(1000);
        urlRules.setTemplate(1l);
        for(int i=0;i<20;i++){
            urlRulesService.addUrlRules(urlRules);
        }

    }

    @Test
    public void update(){
        UrlRules urlRules = urlRulesService.findUrlRulesById(4);
        urlRulesService.updateUrlRules(urlRules);
    }

    @Test
    public void deleteUrlRules(){
        urlRulesService.delUrlRules(4);
    }

    @Test
    public void getUrlRulesTest(){
        List<UrlRules> list= urlRulesService.findByPage(1,10,"111","k","");
        int count = urlRulesService.findCount("1","2","");
        System.out.println(list.size());
        System.out.println(count);
    }

}
