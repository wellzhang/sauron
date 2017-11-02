package com.feng.sauron.warning.service.mock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.feng.sauron.warning.service.*;
import com.feng.sauron.warning.util.SpanIdComparator;
import com.feng.sauron.warning.web.vo.*;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by lianbin.wang on 2016/11/15.
 */
@Service("mocked")
public class MockedService implements ScatterService, TopologyService, Top10Service, TraceService, CallstackService, AppOverViewService {

    @Override
    public List<MetricsOriDataVO> loadScatterData(Date startTime, Date endTime, String appName, String host) {
        long start = startTime.getTime();
        long end = endTime.getTime();

        List<MetricsOriDataVO> list = new ArrayList<>();

        for (long i = start; i < end; i += 100) {
            list.add(new MetricsOriDataVO(i, RandomUtils.nextLong(10, 1000 * 60),
                    "" + System.currentTimeMillis(), RandomUtils.nextInt(0, 10) <= 8 ? "success" : "fail"));
        }

        System.err.println("list.size:" + list.size());

        return list;
    }

    @Override
    public TopologyData load(String appName, String host, Date startTime, Date endTime) {
        List<TopologyNode> nodeList = new ArrayList<>();
        List<TopologyLink> linkList = new ArrayList<>();


        String[] categorys = {"ACTIVE_MQ_CLIENT", "APACHE", "DUBBO", "MONGODB", "MYSQL", "REDIS", "SPRING_BOOT", "NGINX", "UNKNOWN"};

        List<TopologyNodeURL> urlList = Collections.emptyList();
        TopologyNode currentNode = new TopologyNode("STAND_ALONE", appName, appName, urlList);


        List<String> keys = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TopologyNode node = new TopologyNode();
            String category = categorys[i % categorys.length];
            node.setCategory(category);
            node.setNodeName(category);
            String key = category + i;
            node.setKey(key);
            keys.add(key);

            node.setUrlList(createUrlList(node));

            nodeList.add(node);


            TopologyLink link = new TopologyLink(currentNode.getKey(), node.getKey(),
                    RandomUtils.nextInt(0, 100000), RandomUtils.nextInt(0, 100), RandomUtils.nextLong(0, 1000 * 10), "ok");
            if (i > 3) {
                String from = link.getFrom();
                String to = link.getTo();
                link.setTo(from);
                link.setFrom(to);
            }

            linkList.add(link);
        }


        nodeList.add(currentNode);


        return new TopologyData(linkList, nodeList);
    }

    private List<TopologyNodeURL> createUrlList(TopologyNode node) {
        return Arrays.asList(
                new TopologyNodeURL("APP_" + node.getCategory() + "_1", 10, "HELLO_URL_Detail"),
                new TopologyNodeURL("APP_" + node.getCategory() + "_2", 10, "HELLO_URL_Detail"),
                new TopologyNodeURL("APP_" + node.getCategory() + "_3", 10, "HELLO_URL_Detail"),
                new TopologyNodeURL("APP_" + node.getCategory() + "_4", 10, "HELLO_URL_Detail")
        );
    }

    private List<Top10LineItem> createLine() {
        List<Top10LineItem> list = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            list.add(new Top10LineItem("trace_" + i, now - i * 1000, RandomUtils.nextLong(10, 50 * 1000)));
        }

        Collections.sort(list, new Comparator<Top10LineItem>() {
            @Override
            public int compare(Top10LineItem o1, Top10LineItem o2) {
                return Long.valueOf(o1.getTimestamp()).compareTo(Long.valueOf(o2.getTimestamp()));
            }
        });
        return list;
    }


    @Override
    public List<TraceItem> loadTrace(String[] appName, String[] traceID) {
        if (traceID == null || traceID.length == 0) {
            return Collections.emptyList();
        }

        List<TraceItem> list = new ArrayList<>();

        String json = "{\"traceId\":\"1479203906841^0f00aff35a2\",\"spanId\":\"0\",\"appName\":\"sauron\",\"source\":\"Web\",\"type\":\"Web\",\"detail\":\"http://10.255.53.162:8080\",\"methodName\":\"Web\",\"result\":\"0\",\"exception\":\"\",\"paramStr\":\"sdfsdfssdfsdf\"}";

        for (int i = 0; i < 20; i++) {
            TraceItem item = JSON.parseObject(json, TraceItem.class);
            list.add(item);
        }

        return list;
    }


    @Override
    public List<CallstackItem> loadCallStack(String traceId, String appName) {
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

//        Deque<TraceLog> parents = new ArrayDeque<>();
//        for (int i = 0; i < list.size(); i++) {
//            TraceLog current = list.get(i);
//
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
//
//        for (TraceLog log : list) {
//            System.err.println(JSON.toJSONString(log));
//        }

        List<CallstackItem> result = new ArrayList<>();

        for (TraceLog log : list) {
            String[] span = log.getSpanId().split("\\.");
            log.setIndent(span.length >= 1 ? span.length - 1 : 0);
//            log.setParent(log.getIndent() > 0 ? log.getSpanId().substring(0, log.getSpanId().lastIndexOf(".")) : null);


            CallstackItem item = new CallstackItem();
            BeanUtils.copyProperties(log, item);
            item.setParamStr(JSON.toJSONString(log.getParams()));
            item.setDuration(Integer.parseInt(log.getTracer().getTime()));
            item.setIndent(log.getIndent());
            result.add(item);
        }

        return result;

    }

    @Override
    public List<Top10Iterm> loadTotal(String appName, String host, String type) {
        List<Top10Iterm> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Top10Iterm item = new Top10Iterm(appName, host, type, "url" + i);
            list.add(item);
        }
        return list;
    }

    @Override
    public List<Top10LineItem> loadLineTotal(String appName, String host, String type, String url) {
        return createLine();
    }

    @Override
    public List<Top10Iterm> loadError(String appName, String host, String type) {
        return loadTotal(appName, host, type);
    }

    @Override
    public List<Top10LineItem> loadLineError(String appName, String host, String type, String url) {
        return loadLineTotal(appName, host, type, url);
    }

    @Override
    public Map<String, Object> loadOverView(String appName) {
        int ipCount = RandomUtils.nextInt(1, 20);

        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < ipCount; i++) {
            result.put("255.255.255.2" + i, new Snapshot(i + "", i + "", i + "", i + "", i + "", i + ""));
        }

        return result;
    }


    static class Snapshot {

        /**
         * cpu : cpu
         * mem : mem
         * con : con
         * vmThread : vmThread
         * vmGC : vmGC
         * vmMem : vmMem
         */

        private String cpu;
        private String mem;
        private String con;
        private String vmThread;
        private String vmGC;
        private String vmMem;

        public Snapshot(String cpu, String mem, String con, String vmThread, String vmGC, String vmMem) {
            this.cpu = cpu;
            this.mem = mem;
            this.con = con;
            this.vmThread = vmThread;
            this.vmGC = vmGC;
            this.vmMem = vmMem;
        }

        public String getCpu() {
            return cpu;
        }

        public void setCpu(String cpu) {
            this.cpu = cpu;
        }

        public String getMem() {
            return mem;
        }

        public void setMem(String mem) {
            this.mem = mem;
        }

        public String getCon() {
            return con;
        }

        public void setCon(String con) {
            this.con = con;
        }

        public String getVmThread() {
            return vmThread;
        }

        public void setVmThread(String vmThread) {
            this.vmThread = vmThread;
        }

        public String getVmGC() {
            return vmGC;
        }

        public void setVmGC(String vmGC) {
            this.vmGC = vmGC;
        }

        public String getVmMem() {
            return vmMem;
        }

        public void setVmMem(String vmMem) {
            this.vmMem = vmMem;
        }
    }


    @Override
    public List<OverViewLineData> loadMachineCPU(String appName, String host) {
        return null;
    }

    @Override
    public List<OverViewLineData> loadMachineMemory(String appName, String host) {
        return null;
    }

    @Override
    public List<OverViewLineData> loadMachineConnection(String appName, String host) {
        return null;
    }

    @Override
    public List<OverViewLineData> loadMachineFreePhysicalMemory(String appName, String host) {
        return null;
    }

    @Override
    public List<OverViewLineData> loadProcessJVMThreads(String appName, String host) {
        return null;
    }

    @Override
    public List<OverViewLineData> loadProcessJVMGC(String appName, String host) {
        return null;
    }

    @Override
    public List<OverViewLineData> loadProcessConnection(String appName, String host) {
        return null;
    }

    @Override
    public List<OverViewLineData> loadProcessJVMHeapMemory(String appName, String host) {
        return null;
    }

    @Override
    public List<OverViewLineData> loadProcessJVMNonHeapMemory(String appName, String host) {
        return null;
    }

    @Override
    public Map<String, LinkedHashMap<String, Object>> loadAppVariables(String appName, String host) {
        return null;
    }
}
