package com.fengjr.sauron.converger.kafka.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.fengjr.sauron.converger.util.UrlPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fengjr.sauron.commons.utils.JsonUtils;
import com.fengjr.sauron.converger.kafka.storage.ElasticSearchBulkCommit;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/8.
 */
public class SaveLogForEsHandler implements BaseHandler {


    private static Logger logger = LoggerFactory.getLogger(SaveLogForEsHandler.class);
    private static Properties pro = new Properties();
    private static final String ES_IP_URL = "es.httpurl";
    private static String[] urls = null;

    static{

        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream("elasticsearch.properties");
            pro.load(in);
            if(pro.get(ES_IP_URL) ==null || pro.get(ES_IP_URL).toString().length()<1){
                logger.error("elasticsearch http url is null!");
                new NullPointerException("error : elasticsearch http url is null!");
            }

            String httpUrl  = pro.get(ES_IP_URL).toString();
            if(httpUrl.contains(",")){
                urls = httpUrl.split(",");
            }else {
                urls = new String[]{httpUrl};
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("conf.properties not found.");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("io error:{}",e);
        }

    }


    @Override
    public void handle(String line, String hostName, String logTime,String version) {

        try {
            //logger.info("enter tracelog  data:{}",line);
        	Map<String, Object> mapData = JsonUtils.getObject(line, Map.class);
            mapData.put("hostName",hostName);
            mapData.put("logTime",logTime);
            mapData.put("version",version);
            saveLogForEs(mapData);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error:",e);

        }


    }

    @SuppressWarnings("unchecked")
    private void saveLogForEs(Map<String, Object> logData) {

        try {
            Map<String, Object> sauronMap = (Map<String, Object>) logData.get("Sauron");
            Object Key = sauronMap.get("Key");
            Object EsType = sauronMap.get("EsType");
            // if (object == null || !"financial_support_log".equals(sauronMap.get("Key"))) {
            if (Key == null || EsType == null) {
                return;
            }
            String index = String.valueOf(Key);
            String type = String.valueOf(EsType);
            String id = String.valueOf(sauronMap.get("Traceid"));
            //byte[] source = ObjectToJson(sauronMap.get("Result")).getBytes();
            //System.err.println(ObjectToJson(sauronMap.get("Result")));
            //ElasticSearchBulkCommit.getInstance().createIndexBulk(index, type, id, source);
            String source = ObjectToJson(sauronMap.get("Result"));

            //String url = pro.get(ES_IP_URL)+index+"/"+type+"/"+id;
            //String ret = UrlPost.sendPost(url,source);
            //logger.info("sava traceLog success ret:{}",ret);
            String suffix = index+"/"+type+"/"+id;
            saveEs( suffix, source );
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("insert es error...");
        }catch (Throwable throwable){
            throwable.printStackTrace();

        }
    }

    public static String ObjectToJson(Object object) {

        String json = null;
        try {
            ObjectMapper om = new ObjectMapper();
            om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            json = om.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    private boolean saveEs(String suffix,String source ){

        for(String url :urls){
            if(url == null || url.trim().length()<1)
                continue;
            String ret = UrlPost.sendPost(url.trim()+suffix,source);
            if(ret.length() >1)
                return true;
        }
        logger.error("insert es  error url:{} , data:{}",suffix,source);
        return false;

    }




    public static void main(String [] args){

        SaveLogForEsHandler handler = new SaveLogForEsHandler();
        String line = "{\"Sauron\":{\"Type\":\"traceLog\",\"Version\":\"1\",\"Key\":\"pay_log.201701\",\"EsType\":\"pay_log_type\",\"Traceid\":\"68faf840-d983-11e6-92b2-2047478df540\",\"Result\":{\"agentCode\":\"ump\",\"amount\":\"4002.88\",\"bankCode\":\"PSBC\",\"cardType\":\"B2CDEBITBANK\",\"details\":[{\"endTime\":1484306910660,\"retCode\":\"1000\",\"startTime\":1484306910655,\"status\":\"true\",\"step\":\"个人充值\"}],\"endTime\":1484306910660,\"function\":\"RECHAGE_PERSONRECHARGE\",\"orderId\":\"1701131100039968539\",\"rechargeType\":\"common\",\"rechargeUserType\":\"person\",\"retCode\":\"1000\",\"retMsg\":\"成功\",\"source\":\"WEB\",\"startTime\":1484306910617,\"system\":\"biz-pay\",\"time\":43,\"userId\":\"CDEC5F07-836C-42E7-A632-E7BD9CBD6CFB\"}}}";
        handler.handle(line,"127.0.0.1","2017-01-13 19:28:30","v1");
    }




}
