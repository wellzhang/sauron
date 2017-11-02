import com.feng.sauron.warning.dao.DubboRulesMapper;
import com.feng.sauron.warning.domain.DubboRules;
import com.feng.sauron.warning.service.base.DubboRulesService;
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
 * TestDubboRulesService
 * Created by jianzhang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@ContextConfiguration(locations={"classpath:dao-config.xml","classpath:task-config.xml"})
public class TestDubboRulesService {

    @Autowired
    private DubboRulesService dubboRulesService;

    @Test
    public void addDubboRulesTest(){
        DubboRules dubboRules = new DubboRules();
        dubboRules.setIsEnabled((byte)0);
        dubboRules.setAppId(2l);
        dubboRules.setApplicationName("afafaf");
        dubboRules.setCreatorId(1l);
        dubboRules.setTemplate(1l);
        dubboRules.setZookeeperIpsId(9l);
        dubboRulesService.addDubboRules(dubboRules);
    }

    @Test
    public void update(){
        DubboRules dubboRules = dubboRulesService.findDubboRulesById(1);
        dubboRules.setApplicationName("xxxxxxx");
        dubboRulesService.updateDubboRules(dubboRules);
    }

    @Test
    public void deleteDubboRules(){
        dubboRulesService.delDubboRules("1");
    }

    @Test
    public void getAppTest(){
        List<Map<String,Object>> list= dubboRulesService.findByPage(1,10,"t","xxx",null);
        int count = dubboRulesService.findCount(null,null,null);
        System.out.println(list.size());
        System.out.println(count);
    }
}
