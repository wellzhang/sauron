import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.feng.sauron.warning.service.mock.TraceLog;
import com.feng.sauron.warning.util.SpanIdComparator;
import javassist.NotFoundException;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.htrace.Span;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by lianbin.wang on 2016/11/16.
 */
public class LocalTest {

    @Test
    public void testSort() {
        String[] spanids = {
                "0",
                "0.1",
                "0.1.1",
                "0.2",
                "0.2.1",
                "0.12.1",
                "0.12"
        };

        List<String> list = Arrays.asList(spanids);
        Collections.sort(list, SpanIdComparator.INSTANCE);

        for (String s : list) {
            System.out.println(s);
        }


        Collections.sort(list);
        for (String s : list) {
            System.err.println(s);
        }
    }


    @Test
    public void testResolve() {
        String data = "[{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.1\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"Redis\",\"detail\":\"Redis\",\"methodName\":\"r.c.j.BinaryClient.set(byte[],byte[])\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"0\"},\"params\":{\"String_0\":\"wangweitest\"}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.2\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"Redis\",\"detail\":\"Redis\",\"methodName\":\"r.c.j.BinaryClient.del(byte[][])\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"0\"},\"params\":{\"String_0\":\"wangweitest\"}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.3\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"sauron\",\"detail\":\"sauron\",\"methodName\":\"c.f.s.t.w.s.CommonService.print(String)\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"0\"},\"params\":{\"String_0\":\"1\"}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.4\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"sauron\",\"detail\":\"sauron\",\"methodName\":\"c.f.s.t.w.s.CommonService.printError(String)\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"0\"},\"params\":{\"String_0\":\"test\"}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.5.1\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"MybatisInterceptor\",\"detail\":\"MybatisInterceptor\",\"methodName\":\"select id, user_name, user_id, create_time from tb_user\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"1\"},\"params\":{\"String_0\":\"com.fengjr.sauron.test.web.dao.UserMapper.selectAll\"}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.5\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"Mybatis\",\"detail\":\"Mybatis\",\"methodName\":\"o.a.i.s.d.DefaultSqlSession.selectList(String,Object,RowBounds)\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"1\"},\"params\":{\"String_0\":\"com.fengjr.sauron.test.web.dao.UserMapper.selectAll\",\"Object_1\":null,\"RowBounds_2\":{\"limit\":2147483647,\"offset\":0}}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.6.1\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"MybatisInterceptor\",\"detail\":\"MybatisInterceptor\",\"methodName\":\"SELECT id, user_name, user_id, create_time FROM tb_user ORDER BY id LIMIT 0,3\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"1\"},\"params\":{\"String_0\":\"com.fengjr.sauron.test.web.dao.UserMapper.selectByPage\"}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.6\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"Mybatis\",\"detail\":\"Mybatis\",\"methodName\":\"o.a.i.s.d.DefaultSqlSession.selectList(String,Object,RowBounds)\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"1\"},\"params\":{\"String_0\":\"com.fengjr.sauron.test.web.dao.UserMapper.selectByPage\",\"Object_1\":{\"pageSize\":3,\"param1\":0,\"param2\":3,\"startIndx\":0},\"RowBounds_2\":{\"limit\":2147483647,\"offset\":0}}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.7.1.1.1.1\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"RocketMQProducer\",\"detail\":\"fengmq-test\",\"methodName\":\"send\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"2\"},\"params\":{\"String_0\":\"topic:fengmq-test\",\"String_1\":\"keys:null\",\"String_2\":\"msgId:0AFF341000002A9F00000003CFB61B08\"}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.7.1.1.1.2\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"dubbo_consumer\",\"detail\":\"10.255.53.162\",\"methodName\":\"dubbo_consumer#com.feng.ipcenter.service.IPDataFileService.find\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"0\"},\"params\":{\"String_0\":\"220.181.111.188\"}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.7.1.1.1\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"sauron\",\"detail\":\"sauron\",\"methodName\":\"c.f.s.t.w.RocketMQServlet.doGet(HttpServletRequest,HttpServletResponse)\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"2\"},\"params\":{\"HttpServletRequest_0\":\"Could not resolve!\",\"HttpServletResponse_1\":\"Could not resolve!\"}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.7.1.1\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"sauron\",\"detail\":\"sauron\",\"methodName\":\"c.f.s.t.w.RocketMQServlet.doPost(HttpServletRequest,HttpServletResponse)\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"2\"},\"params\":{\"HttpServletRequest_0\":\"Could not resolve!\",\"HttpServletResponse_1\":\"Could not resolve!\"}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.7.1\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"Web\",\"detail\":\"http://127.0.0.1:8080\",\"methodName\":\"Web\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"2\"},\"params\":{\"URI_0\":\"http://127.0.0.1:8080/rocketmq\",\"String_1\":{}}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1.7\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"HttpClient4\",\"detail\":\"127.0.0.1\",\"methodName\":\"o.a.h.i.c.CloseableHttpClient.execute(HttpUriRequest,HttpContext)\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"4\"},\"params\":{\"String_0\":\"http://127.0.0.1:8080/rocketmq\"}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0.1\",\"appName\":\"sauron\",\"source\":\"sauron\",\"type\":\"Controller\",\"detail\":\"Controller\",\"methodName\":\"c.f.s.t.w.c.CommonInfoController.test()\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"9\"},\"params\":{}}\n" +
                ",{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0\",\"appName\":\"sauron\",\"source\":\"Web\",\"type\":\"Web\",\"detail\":\"http://10.255.53.162:8080\",\"methodName\":\"Web\",\"result\":\"0\",\"exception\":\"\",\"tracer\":{\"time\":\"9\"},\"params\":{\"URI_0\":\"http://10.255.53.162:8080/common/test\",\"String_1\":{}}}\n" +
                "]";

        List<TraceLog> list = JSON.parseObject(data, new TypeReference<List<TraceLog>>() {
        });

        //根据SPANID排序
        Collections.sort(list, new Comparator<TraceLog>() {

            @Override
            public int compare(TraceLog o1, TraceLog o2) {
                return SpanIdComparator.INSTANCE.compare(o1.getSpanId(), o2.getSpanId());
            }

        });

//
//        Deque<TraceLog> parents = new ArrayDeque<>();
//        for (int i = 0; i < list.size(); i++) {
//            TraceLog current = list.get(i);
//            int indent = current.getSpanId().split("\\.").length;
//            TraceLog parent = null;
//            if (i > 0) {
//                TraceLog last = list.get(i - 1);
//                int lastIndent = last.getSpanId().split("\\.").length;
//                if (indent > lastIndent) {
//                    parents.push(last);
//                } else if (indent < lastIndent) {
//                    parents.pop();
//                }
//                parent = parents.peek();
//            }
//            current.setParent(parent != null ? parent.getSpanId() : null);
//            current.setIndent(indent);
//        }


        for (TraceLog log : list) {
            String[] span = log.getSpanId().split("\\.");
            log.setIndent(span.length >= 1 ? span.length - 1 : 0);
            log.setParent(log.getIndent() > 0 ? log.getSpanId().substring(0, log.getSpanId().lastIndexOf(".")) : null);
        }

        for (TraceLog log : list) {
            System.err.println("spanid:" + log.getSpanId() + ",pid:" + log.getParent() + ",indent:" + log.getIndent());
        }
    }


    @Test
    public void testNull() {
        System.out.println(null instanceof NotFoundException);
        System.out.println(null instanceof Object);
//        System.out.println(null instanceof );

    }

    @Test
    public void testData() {
        List<Number> list = new ArrayList<>();
        list.add(1L);
        list.add(5);

        System.out.println(JSON.toJSONString(list));


        long l = Double.valueOf("9.46828544E8").longValue();
        System.out.println(l);

        DecimalFormat format = new DecimalFormat("##.##%");
        System.out.println(format.format(0.955));
    }

}
