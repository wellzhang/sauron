import com.feng.sauron.warning.domain.UrlTraceFailed;
import com.feng.sauron.warning.service.base.UrlTraceFailedService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TestUrlTraceFailed
 * Created by jianzhang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@ContextConfiguration(locations={"classpath:dao-config.xml","classpath:task-config.xml"})
public class TestUrlTraceFailed {

    @Autowired
    private UrlTraceFailedService urlTraceFailedService;

    @Test
    public void addUrlTraceFailedTest(){
        UrlTraceFailed urlTraceFailed = new UrlTraceFailed();
        urlTraceFailed.setUrlMonitorId(3l);
        urlTraceFailed.setResult("errro  erro");
        urlTraceFailedService.addUrlTraceFailed(urlTraceFailed);
    }


    @Test
    public void getUrlTraceFailedTest(){
        List<UrlTraceFailed> list= urlTraceFailedService.findByPage(1,50,3);
        System.out.println(list.size());
    }


}
