package com.fengjr.sauron.converger.kafka.decoder;


import com.fengjr.sauron.converger.BaseTest;
import org.junit.Test;


import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by xubiao.fan on 2016/5/18.
 */
public class TestLogDecoder extends BaseTest {

    @Resource
    LogMsgDecoder logMsgDecoder;

    @Test
    public void testAppMsg(){

       // String line = "host1|2016-05-04 15:33:25|{\"Sauron\":{\"Type\":\"App\",\"Version\":\"2\",\"Traceid\":\"7bd37363-11ca-11e6-b3ff-1002b5da01b4\",\"Spanid\":\"0.1.1\",\"AppName\":\"sauron\",\"MethodName\":\"com.feng.sauron.test.domain.ExampleClass.doSomething3(int)\",\"InvokeResult\":\"0\",\"Tracer\":{\"duration\":\"468ms\"},\"Params\":{\"int\":101}}}";
        //String line = "{\"Sauron\":{\"Type\":\"App\",\"Version\":\"2\",\"Traceid\":\"6e4ae6f0-2094-11e6-9833-f0000aff35a1\",\"Spanid\":\"0.1.1\",\"AppName\":\"sauron_warning\",\"MethodName\":\"dubbo_provider|com.feng.ipcenter.service.IPDataFileServiceTest.find\",\"InvokeResult\":\"0\",\"Tracer\":{\"duration\":\"0ms\"},\"Params\":{\"String\":\"127.0.0.1\"}}}";
        //String line = "10.255.53.161 |2016-05-23 14:34:36|{\"Sauron\":{\"Type\":\"App\",\"Version\":\"2\",\"Traceid\":\"6b1fd500-20b0-11e6-a178-f0000aff35a1\",\"Spanid\":\"0.1.1.1\",\"AppName\":\"sauron_warning\",\"MethodName\":\"com.feng.ipcenter.service.impl.IPDataFileServiceImplTest.find(java.lang.String)\",\"InvokeResult\":\"0\",\"Tracer\":{\"duration\":\"0ms\"},\"Params\":{\"String\":\"127.0.0.1\"}}}";

        String line = "10.255.53.161|2016-11-09 19:44:26|app|v3|{\"sauron\":{\"traceId\":\"1478691865367^0989096d96e48\",\"spanId\":\"0\",\"appName\":\"sauron\",\"source\":\"WebTracer\",\"type\":\"WebTracer\",\"detail\":\"http:localhost:8080\",\"methodName\":\"WebTracer\",\"result\":\"0\",\"tracer\":{\"time\":\"638\"},\"params\":{\"URI_0\":\"http://localhost:8080/\",\"String_1\":{}}}}";

        try {
            logMsgDecoder.decodeMsg(line);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testCodeBulkAlarmMsg(){
        String line = "host1|2016-05-19 14:50:25|alarm|v3|{\"sauron\":{\"appName\":\"sauron\",\"traceId\":\"1478691865367^0989096d96e48\",\"key\":\"key2\",\"methodName\":\"com.feng.sauron.impl.SauronTracerByMap.main\",\"time\":\"1463539002486\",\"lineNumber\":\"188\",\"Result\":\"ecception:error\"}}";
        try {
            logMsgDecoder.decodeMsg(line);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void testCodeBulkMsg(){
        String line = "host1|2016-05-19 10:33:25|codeBulk|v3|{\"sauron\":{\"appName\":\"sauron\",\"traceId\":\"1478691865367^0989096d96e48\",\"key\":\"key1\",\"methodName\":\"com.feng.sauron.impl.SauronTracer.main\",\"tracer\":{\"duration\":\"3174ms\"},\"lineNumber\":\"200\",\"result\":\"{errorCode:1}\",\"isSuccess\":\"0\"}}";
        try {
            logMsgDecoder.decodeMsg(line);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




}
