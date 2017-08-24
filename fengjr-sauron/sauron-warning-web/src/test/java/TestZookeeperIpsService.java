import com.feng.sauron.warning.domain.ZookeeperIps;
import com.feng.sauron.warning.service.base.ZookeeperIpsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * TestZookeeperIpsService
 * Created by jianzhang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@ContextConfiguration(locations={"classpath:dao-config.xml","classpath:task-config.xml"})
public class TestZookeeperIpsService {

    @Autowired
    private ZookeeperIpsService zookeeperIpsService;

    @Test
    public void addZookeeperIpsTest(){
        ZookeeperIps zookeeperIps = new ZookeeperIps();
        zookeeperIps.setName("111");
        zookeeperIps.setZkIp("192.168.1.1:6181");
        zookeeperIps.setDescribes("22");
        zookeeperIpsService.addZookeeperIps(zookeeperIps);
    }

    @Test
    public void updateZookeeperIpsTest(){
        ZookeeperIps zookeeperIps = zookeeperIpsService.findZookeeperIpsById(1);
        zookeeperIpsService.updateZookeeperIps(zookeeperIps);
    }

    @Test
    public void delZookeeperIpsTest(){
        zookeeperIpsService.delZookeeperIps("1");
    }

    @Test
    public void getZookeeperIpsTest(){
        zookeeperIpsService.findByPage(0,100);
    }



}
