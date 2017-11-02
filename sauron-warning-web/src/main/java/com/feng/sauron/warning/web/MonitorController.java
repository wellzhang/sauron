package com.feng.sauron.warning.web;

import com.feng.sauron.warning.common.Page;
import com.feng.sauron.warning.domain.App;
import com.feng.sauron.warning.domain.UrlMonitor;
import com.feng.sauron.warning.domain.UrlRules;
import com.feng.sauron.warning.domain.UrlTraceFailed;
import com.feng.sauron.warning.monitor.dubbo.StartDam;
import com.feng.sauron.warning.service.ProducerFailedFlowService;
import com.feng.sauron.warning.service.base.*;
import com.feng.sauron.warning.service.hbase.HbaseMetricsCodeAlarmService;
import com.feng.sauron.warning.service.hbase.HbaseMetricsCodeBulkService;
import com.feng.sauron.warning.service.hbase.HbaseMetricsOriDataService;
import com.feng.sauron.warning.util.DateUtils;
import com.feng.sauron.warning.web.base.BaseController;
import com.feng.sauron.warning.web.vo.DubboMonitor;
import com.fengjr.sauron.dao.hbase.vo.Range;
import com.fengjr.sauron.dao.model.MetricsCodeBulkAlarmData;
import com.fengjr.sauron.dao.model.MetricsCodeBulkData;
import com.fengjr.sauron.dao.model.MetricsOriData;
import com.mongodb.BasicDBObject;

import org.apache.commons.lang3.StringUtils;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * MonitorController
 * Created by jianzhang
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController extends BaseController {

    public static final String COLLECTION_NAME = "metrics_ori_data";

    public static final String CODE_BULK_NAME = "metrics_ori_data_codebulk";

    public static final String CODE_BULK_NAME_ALARM = "metrics_ori_data_codebulk_alarm";

    @Autowired
    private UrlRulesService urlRulesService;

    @Autowired
    private UrlMonitorService urlMonitorService;

    @Autowired
    private UrlTraceFailedService urlTraceFailedService;

    @Autowired
    private DubboRulesService dubboRulesService;

    @Autowired
    private StartDam startDam;

    //@Resource
    //MongoService mongoService;
    @Resource
    ProducerFailedFlowService producerFailedFlowService;

    @Resource
    private AppService appService;

    @Autowired
    private UserService userService;

    /**
     * 避免hbase返回数据量太大
     */
    private final int MAX_LIST_SIZE = 50;


    @RequestMapping(value = "/urlList/{pageNo}/{pageSize}")
    public String urlList(@PathVariable(value = "pageNo") int pageNo, @PathVariable(value = "pageSize") int pageSize,
                          String appName, String monitorKey, Model model) {
        Page<Map<String, Object>> pageData = new Page<Map<String, Object>>();
        pageData.setDataList(urlMonitorService.findByPage(pageNo, pageSize, appName, monitorKey, getCreatorId()));
        pageData.setPageSize(pageSize);
        pageData.setPageNO(pageNo);
//        UserUtils.getUser().getId();
        pageData.setTotal(urlMonitorService.findCount(appName, monitorKey, getCreatorId()));
        List<App> appNameList = appService.findByPage(1, 1000, null, getCreatorId());
        model.addAttribute("appNameList", appNameList);
        model.addAttribute("pageData", pageData);
        model.addAttribute("appName", appName);
        model.addAttribute("monitorKey", monitorKey);
        return "monitor/urlMonitorList";
    }


//    @RequestMapping(value = "/urlQuery")
//    public String urlQueryRule(HttpServletRequest request, Model model) {
//        String appName = request.getParameter("appName");
//        String monitorKey = request.getParameter("monitorKey");
//
//        Page<Map<String,Object>> pageData = new Page<Map<String,Object>>();
//        pageData.setDataList(urlMonitorService.findByPage(1, 10, appName, monitorKey,getCreatorId()));
//        pageData.setPageSize(10);
//        pageData.setPageNO(1);
//        pageData.setTotal(urlMonitorService.findCount(appName, monitorKey,getCreatorId()));
//        List<App> appNameList = appService.findByPage(1,1000,null,getCreatorId());
//        model.addAttribute("appNameList", appNameList);
//        model.addAttribute("appName",appName);
//        model.addAttribute("monitorKey",monitorKey);
//        model.addAttribute("pageData", pageData);
//        return "monitor/urlMonitorList";
//    }

    @RequestMapping(value = "/urlTraceFailList/{urlRuleId}/{urlMonitorId}")
    public String traceFailList(@PathVariable(value = "urlRuleId") long urlRuleId, @PathVariable(value = "urlMonitorId") long urlMonitorId, Model model) {
        UrlRules urlRules = urlRulesService.findUrlRulesById(urlRuleId);
        UrlMonitor urlMonitor = urlMonitorService.findUrlMonitorById(urlMonitorId);
        Page<UrlTraceFailed> pageData = new Page<UrlTraceFailed>();
        pageData.setDataList(urlTraceFailedService.findByPage(1, 50, urlMonitorId));
        model.addAttribute("pageData", pageData);
        model.addAttribute("urlRules", urlRules);
        model.addAttribute("urlMonitor", urlMonitor);
        return "monitor/traceFailedList";
    }

    @RequestMapping(value = "/dubboList/{pageNo}/{pageSize}")
    public String dubboList(@PathVariable(value = "pageNo") int pageNo, @PathVariable(value = "pageSize") int pageSize,
                            String appName, String applicationName, Model model) {
        Page<DubboMonitor> pageData = new Page<DubboMonitor>();
        try {
            List<Map<String, Object>> list = dubboRulesService.findByPage(pageNo, pageSize, appName, applicationName, getCreatorId());
            List<DubboMonitor> dubboMonitors = getMonitorList(list);
            pageData.setDataList(dubboMonitors);
            pageData.setPageSize(pageSize);
            pageData.setPageNO(pageNo);
            pageData.setTotal(dubboRulesService.findCount(appName, applicationName, getCreatorId()));
            List<App> appNameList = appService.findByPage(1, 1000, null, getCreatorId());
            model.addAttribute("appNameList", appNameList);
            model.addAttribute("pageData", pageData);
            model.addAttribute("appName", appName);
            model.addAttribute("applicationName", applicationName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "monitor/dubboMonitorList";
    }

//    @RequestMapping(value = "/dubboQuery")
//    public String dubboQueryRule(HttpServletRequest request, Model model) {
//        String appName = request.getParameter("appName");
//        String applicationName = request.getParameter("applicationName");
//
//        try{
//            Page<DubboMonitor> pageData = new Page<DubboMonitor>();
//            List<Map<String,Object>> list = dubboRulesService.findByPage(1, 10, appName, applicationName,getCreatorId());
//            List<DubboMonitor> dubboMonitors = getMonitorList(list);
//            pageData.setDataList(dubboMonitors);
//            pageData.setPageSize(10);
//            pageData.setPageNO(1);
//            pageData.setTotal(dubboRulesService.findCount(appName, applicationName,getCreatorId()));
//            List<App> appNameList = appService.findByPage(1,1000,null,getCreatorId());
//            model.addAttribute("appNameList", appNameList);
//            model.addAttribute("appName",appName);
//            model.addAttribute("applicationName",applicationName);
//            model.addAttribute("pageData", pageData);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return "monitor/dubboMonitorList";
//    }

    @RequestMapping(value = "/methodList")
    public String methodList(Model model) {
        List<App> appNameList = appService.findByPage(1, 1000, null, getCreatorId());
        model.addAttribute("appNameList", appNameList);
        return "monitor/methodMonitorList";
    }

    private List<DubboMonitor> getMonitorList(List<Map<String, Object>> list) {
        List<DubboMonitor> dubboMonitors = new ArrayList<>();
        DubboMonitor dubboMonitor = null;
        String zkIp = "";
        String applicationName = "";
        for (Map<String, Object> map : list) {
            applicationName = String.valueOf(map.get("applicationName"));
            zkIp = String.valueOf(map.get("zkIp"));
            dubboMonitor = new DubboMonitor();
            dubboMonitor.setAppName(String.valueOf(map.get("name")));
            dubboMonitor.setApplicationName(applicationName);
            boolean isAlive = startDam.getZkAppisAlive(zkIp, applicationName);
            if (isAlive) {
                dubboMonitor.setIsAlive(DubboMonitor.IsAlive.alived.getVal());
            } else {
                dubboMonitor.setIsAlive(DubboMonitor.IsAlive.died.getVal());
            }
            dubboMonitors.add(dubboMonitor);
        }
        return dubboMonitors;
    }

    @Autowired
    private HbaseMetricsOriDataService hbaseMetricsOriDataService;

    @RequestMapping(value = "/getTracers", method = {RequestMethod.GET, RequestMethod.POST})
    public String getInfoByTraceid1(String traceId, String appName, String hostName, String dTime, String methodName, Model model) {
//        BasicDBObject basicDBObject = new BasicDBObject();
        String startTime, endTime;
        String timeArr[] = null;
        List<App> appNameList = appService.findByPage(1, 1000, null, getCreatorId());
        model.addAttribute("appNameList", appNameList);
        model.addAttribute("traceId", traceId);
        model.addAttribute("appName", appName);
        model.addAttribute("hostName", hostName);
        model.addAttribute("methodName", methodName);
        try {
            if (StringUtils.isBlank(appName)) {
                return "monitor/methodMonitorList";
            }

            Range timeRange = null;

            if (StringUtils.isNotBlank(dTime)) {
                timeArr = dTime.split(" - ");
                if (timeArr.length != 2)
                    return "monitor/methodMonitorList";
                else {
                    startTime = timeArr[0].trim();
                    endTime = timeArr[1].trim();
//                    basicDBObject.put("logtime", new BasicBSONObject("$gt",
//                            DateUtils.getGMTDate(startTime)).append("$lt", DateUtils.getGMTDate(endTime)));// 大于 start 小于 end
                    timeRange = new Range(DateUtils.parseDate(startTime, "yyyy-MM-dd HH:mm:ss").getTime(),
                            DateUtils.parseDate(endTime, "yyyy-MM-dd HH:mm:ss").getTime());
                }
            }


//            if (StringUtils.isBlank(hostName)) {
//                hostName = "all";
//            }

//            basicDBObject.put("AppName", appName);
//
//            if (StringUtils.isNotBlank(methodName)) {
//                basicDBObject.put("methodName", methodName);
//            }
//
//            if (!"all".equalsIgnoreCase(hostName)) {
//                basicDBObject.put("hostName", hostName);
//            }
//
//            if (StringUtils.isNotBlank(traceId)) {
//                basicDBObject.put("Traceid", traceId);
//            } else {
//                basicDBObject.put("Spanid", "0");
//            }


//            List<Document> findMsg = mongoService.findMsg(COLLECTION_NAME, basicDBObject, 50);
//            List<Map<String, String>> arrayList = transformDocument(findMsg);

            List<MetricsOriData> arrayList = new ArrayList<>();
            if (StringUtils.isNotBlank(traceId)) {
                MetricsOriData data = hbaseMetricsOriDataService.getMetricsForMethodMonitor(appName, traceId);
                if (data != null) {
                    arrayList.add(data);
                }
            } else if (timeRange != null) {
                List<MetricsOriData> list = hbaseMetricsOriDataService.getMetricsForMethodMonitor(appName, timeRange);
                if (list != null) {
                    arrayList = list;
                }
            }

            if (StringUtils.isNotBlank(hostName) || StringUtils.isNotBlank(methodName)) {
                Iterator<MetricsOriData> iterator = arrayList.iterator();
                while (iterator.hasNext()) {
                    MetricsOriData data = iterator.next();
                    if (StringUtils.isNotBlank(hostName) && !hostName.equals(data.getHostName())) {
                        iterator.remove();
                        continue;
                    }
                    if (StringUtils.isNotBlank(methodName) && !methodName.equals(data.getMethodName())) {
                        iterator.remove();
                        continue;
                    }
                }
            }

            if (arrayList.size() > MAX_LIST_SIZE) {
                arrayList = arrayList.subList(0, MAX_LIST_SIZE);
            }

            model.addAttribute("methodMonitorList", arrayList);
            model.addAttribute("dTime", dTime);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "monitor/methodMonitorList";
    }


    private List<Map<String, String>> transformDocument(List<Document> findMsg) {
        List<Map<String, String>> arrayList = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<>();
        for (Document document : findMsg) {
            String Traceid = document.get("Traceid").toString();

            if (!hashSet.contains(Traceid)) {
                hashSet.add(Traceid);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Traceid", document.get("Traceid").toString());
                hashMap.put("AppName", document.get("AppName").toString());
                hashMap.put("hostName", document.get("hostName").toString());
                hashMap.put("duration", document.get("duration").toString());
                hashMap.put("InvokeResult", document.get("InvokeResult").toString());
                hashMap.put("logtime", DateUtils.format((Date) document.get("logtime")));
                hashMap.put("Spanid", document.get("Spanid").toString());
                hashMap.put("methodName", document.get("methodName").toString());
                arrayList.add(hashMap);
            }
        }
        return arrayList;
    }


//    @ResponseBody
//    @RequestMapping(value = "/getInfoByTraceid/{traceId}/{logTime}", method = {RequestMethod.GET, RequestMethod.POST})
//    public TraceResponseDTO<TreeNode> getInfoByTraceid(@PathVariable(value = "traceId") String traceId, @PathVariable(value = "logTime") String logTime, HttpServletRequest request) {
//
//
//        TraceResponseDTO<TreeNode> responseDTO = new TraceResponseDTO<>();
//        responseDTO.setCode(ReturnCode.ACTIVE_FAILURE.code());
//
//
//        if (StringUtils.isBlank(traceId) || StringUtils.isBlank(logTime)) {
//            return responseDTO;
//        }
//
////        BasicDBObject basicDBObject = new BasicDBObject("Traceid", traceId);
////
////        List<Document> findMsg = mongoService.findMsg(COLLECTION_NAME, basicDBObject, 1000);
////
////
////        List<Map<String, String>> arrayList = transformDocument(findMsg);
////
////
//////        TreeNode dataToJson = TreeNode.dataToJson(findMsg);
////
////
////        if (arrayList != null) {
////
////            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
////
////            responseDTO.setAttach(arrayList);
////        }
//
//        BasicDBObject basicDBObject = new BasicDBObject("Traceid", traceId);
//
//        basicDBObject.put("logtime",
//                new BasicBSONObject("$gt", DateUtils.getGMTDate(DateUtils.getTranformTime(logTime, -5))).append("$lt",
//                        DateUtils.getGMTDate(DateUtils.getTranformTime(logTime, 5))));
//
//
//        List<Document> findMsg = mongoService.findMsg(COLLECTION_NAME, basicDBObject, 100);
//
//        Collections.sort(findMsg, new Comparator<Document>() {
//            @Override
//            public int compare(Document o1, Document o2) {
//                return o1.get("Spanid").toString().compareTo(o2.get("Spanid").toString());
//            }
//        });
//
//        try {
//
//            String spanId;
//            Document document;
//            Document lastDocument;
//            int offset = 0;
//            int documentSize = findMsg.size();
//            if (documentSize >= 2) {
//                document = findMsg.get(0);
//                document.append("offset", "0");
//                document.append("elapse", "0");
//                document = findMsg.get(1);
//                document.append("offset", "0");
//                document.append("elapse", "0");
//            }
//            for (int loop = 2; loop < findMsg.size(); loop++) {
//                document = findMsg.get(loop);
//                lastDocument = findMsg.get(loop - 1);
//                spanId = document.get("Spanid").toString();
//                if ((lastDocument.get("Spanid").toString() + ".1").equals(spanId)) {
////                Document thirdDocument = findMsg.get(loop - 2);
////                offset  =  Integer.valueOf(thirdDocument.get("offset").toString())+
////                        Integer.valueOf(thirdDocument.get("duration").toString());
//
//                    //寻找根节点偏移量
//
//                    for (int i = 0; i < spanId.split(".").length; i++) {
//                        Document document1 = findMsg.get(loop - i);
//                        Document document2 = findMsg.get(loop - (i + 1));
//                        if (!(document1.get("Spanid").toString().equals(document2.get("Spanid").toString() + ".1"))) {
//                            break;
//                        }
//                        offset = Integer.valueOf(document2.get("offset").toString()) +
//                                Integer.valueOf(document2.get("duration").toString());
//                    }
//
//
//                } else if (lastDocument.get("Spanid").toString().trim().length() == spanId.trim().length()) {
//                    offset = Integer.valueOf(lastDocument.get("offset").toString())
//                            + Integer.valueOf(lastDocument.get("duration").toString());
//                } else {
//                    String[] tempSpanIds = spanId.split("\\.");
//                    int index = Integer.valueOf(tempSpanIds[tempSpanIds.length - 1]);
//                    tempSpanIds[tempSpanIds.length - 1] = String.valueOf(index - 1);
//                    StringBuffer stringBuffer = new StringBuffer();
//                    for (int j = 0; j < tempSpanIds.length; j++) {
//                        if (j > 0) stringBuffer.append(".");
//                        stringBuffer.append(tempSpanIds[j]);
//                    }
//
//                    for (Document d : findMsg) {
//                        if (d.get("Spanid").toString().equals(stringBuffer.toString()) && d.get("offset") != null) {
//                            offset = Integer.valueOf(d.get("offset").toString()) +
//                                    Integer.valueOf(d.get("duration").toString());
//                            break;
//                        }
//                    }
//
//                }
//                document.append("offset", offset);
//
//
//                if (document.get("methodName").toString().indexOf("dubbo_") >= 0 && lastDocument.get("methodName").toString().indexOf("dubbo_") >= 0) {
//                    lastDocument.append("elapse", Integer.valueOf(lastDocument.get("duration").toString())
//                            - Integer.valueOf(document.get("duration").toString()));
//
//                    document.append("elapse", "0");
//                } else {
//                    document.append("elapse", "0");
//                }
//
//            }
//
//
//            responseDTO.setTreeData(JSONArray.toJSONString(findMsg));
//            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
////		TreeNode dataToJson = TreeNode.dataToJson(findMsg);
//
//
////		if (dataToJson != null) {
////			responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
////			responseDTO.setAttach(dataToJson);
////		}
//
//        return responseDTO;
//    }


    @RequestMapping(value = "/getTracersDetail/{tid}/{time}")
    public String methodDetail(@PathVariable(value = "tid") String tid, @PathVariable(value = "time") String time, Model model) {
        model.addAttribute("tid", tid);
        model.addAttribute("time", time);
        return "monitor/methodMonitorDetail";
    }


    @RequestMapping(value = "/getMetricsOriDataCodeBulk", method = {RequestMethod.GET, RequestMethod.POST})
    public String getMetricsOriDataCodeBulk(String Key, String appName, String dTime, Model model) {
//        BasicDBObject basicDBObject = new BasicDBObject();
        String startTime, endTime;
        String timeArr[] = null;
        List<App> appNameList = appService.findByPage(1, 1000, null, getCreatorId());
        model.addAttribute("appNameList", appNameList);
        model.addAttribute("appName", appName);
        model.addAttribute("dTime", dTime);
        model.addAttribute("Key", Key);
        if (StringUtils.isBlank(appName)) {
            return "monitor/customMonitorList";
        }

        Range timeRange = null;
        if (StringUtils.isNotBlank(dTime)) {
            timeArr = dTime.split(" - ");
            if (timeArr.length != 2)
                return "monitor/customMonitorList";
            else {
                startTime = timeArr[0].trim();
                endTime = timeArr[1].trim();
//                basicDBObject.put("logtime", new BasicBSONObject("$gt", DateUtils.getGMTDate(startTime)).append("$lt", DateUtils.getGMTDate(endTime)));// 大于 start 小于 end
                timeRange = new Range(DateUtils.parseDate(startTime, "yyyy-MM-dd HH:mm:ss").getTime(),
                        DateUtils.parseDate(endTime, "yyyy-MM-dd HH:mm:ss").getTime());
            }
        }
//        if (StringUtils.isNotBlank(Key)) {
//            basicDBObject.put("Key", Key);
//        }
//        basicDBObject.put("AppName", appName);
//        List<Map<String, String>> arrayList = new ArrayList<>();
//        try {
//            List<Document> findMsg = mongoService.findMsg(CODE_BULK_NAME, basicDBObject, 50);
//            HashSet<String> hashSet = new HashSet<>();
//            for (Document document : findMsg) {
//
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("IsSuccess", document.get("IsSuccess").toString());
//                hashMap.put("AppName", document.get("AppName").toString());
//                hashMap.put("hostName", document.get("hostName").toString());
//                hashMap.put("duration", document.get("duration").toString());
//                hashMap.put("Key", document.get("Key").toString());
//                hashMap.put("logtime", DateUtils.format((Date) document.get("logtime"), "yyyy-MM-dd HH:mm:ss"));
//                hashMap.put("Traceid", document.get("Traceid").toString());
//                hashMap.put("Result", document.get("Result").toString());
//                hashMap.put("methodName", document.get("methodName").toString());
//                arrayList.add(hashMap);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        List<MetricsCodeBulkData> list = new ArrayList<>();
        if (timeRange != null) {
            List<MetricsCodeBulkData> rawList = hbaseMetricsCodeBulkService.getMetricsCodeBulkDataRange(appName, timeRange);
            if (StringUtils.isNotBlank(Key)) {
                for (MetricsCodeBulkData data : rawList) {
                    if (Key.equals(data.getKey())) {
                        list.add(data);
                        if (list.size() >= MAX_LIST_SIZE) {
                            break;
                        }
                    }
                }
            } else {
                list = rawList;
            }
        }

        if (list.size() > MAX_LIST_SIZE) {
            list = list.subList(0, MAX_LIST_SIZE);
        }

        model.addAttribute("methodMonitorList", list);


        return "monitor/customMonitorList";
    }


    @Autowired
    private HbaseMetricsCodeBulkService hbaseMetricsCodeBulkService;

    @Autowired
    private HbaseMetricsCodeAlarmService hbaseMetricsCodeAlarmService;

    @RequestMapping(value = "/getMetricsOriDataCodebulkAlarm", method = {RequestMethod.GET, RequestMethod.POST})
    public String getMetricsOriDataCodebulkAlarm(String traceId, String appName, String Key, String dTime, String methodName, Model model) {
//        BasicDBObject basicDBObject = new BasicDBObject();
        String startTime, endTime;
        String timeArr[] = null;
        List<App> appNameList = appService.findByPage(1, 1000, null, getCreatorId());
        model.addAttribute("appNameList", appNameList);
        model.addAttribute("traceId", traceId);
        model.addAttribute("appName", appName);
        model.addAttribute("Key", Key);
        model.addAttribute("methodName", methodName);
        try {
            if (StringUtils.isBlank(appName)) {
                return "monitor/customAlarmMonitorList";
            }

            Range timeRange = null;

            if (StringUtils.isNotBlank(dTime)) {
                timeArr = dTime.split(" - ");
                if (timeArr.length != 2)
                    return "monitor/methodMonitorList";
                else {
                    startTime = timeArr[0].trim();
                    endTime = timeArr[1].trim();
//                    basicDBObject.put("logtime", new BasicBSONObject("$gt", DateUtils.getGMTDate(startTime)).append("$lt", DateUtils.getGMTDate(endTime)));// 大于 start 小于 end
                    timeRange = new Range(DateUtils.parseDate(startTime, "yyyy-MM-dd HH:mm:ss").getTime(),
                            DateUtils.parseDate(endTime, "yyyy-MM-dd HH:mm:ss").getTime());
                }
            }

//            if (StringUtils.isNotBlank(Key)) {
//                basicDBObject.put("Key", Key);
//            }
//
//            basicDBObject.put("AppName", appName);
//
//            if (StringUtils.isNotBlank(methodName)) {
//                basicDBObject.put("methodName", methodName);
//            }

//            if (!"all".equalsIgnoreCase(hostName)) {
//            basicDBObject.put("hostName", "all");
//            }

//            if(StringUtils.isNotBlank(traceId)){
//                basicDBObject.put("Traceid", traceId);
//            }
//
//
//            List<Map<String, String>> arrayList = new ArrayList<>();
//            try{
//                List<Document> findMsg = mongoService.findMsg(CODE_BULK_NAME_ALARM, basicDBObject, 50);
//                for (Document document : findMsg) {
//
//                    HashMap<String, String> hashMap = new HashMap<>();
//                    hashMap.put("logtime", DateUtils.format((Date) document.get("logtime"),"yyyy-MM-dd HH:mm:ss"));
//                    hashMap.put("LineNumber", document.get("LineNumber").toString());
//                    hashMap.put("Key", document.get("Key").toString());
//                    hashMap.put("Traceid", document.get("Traceid").toString());
//                    hashMap.put("Type", document.get("Type").toString());
//                    hashMap.put("hostName", document.get("hostName").toString());
//                    hashMap.put("AppName", document.get("AppName").toString());
//                    hashMap.put("methodName", document.get("methodName").toString());
//                    hashMap.put("Result", document.get("Result").toString());
//                    arrayList.add(hashMap);
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//

            List<MetricsCodeBulkAlarmData> arrayList = new ArrayList<>();
            if (StringUtils.isNotBlank(traceId)) {
                MetricsCodeBulkAlarmData data = hbaseMetricsCodeAlarmService.getMetricsCodeBulkAlarm(appName, traceId);
                if (data != null) {
                    arrayList.add(data);
                }
            } else if (timeRange != null) {
                arrayList = hbaseMetricsCodeAlarmService.getMetricsCodeBulkAlarmRange(appName, timeRange);
            }


            if (StringUtils.isNotBlank(Key) || StringUtils.isNotBlank(methodName)) {
                Iterator<MetricsCodeBulkAlarmData> iterator = arrayList.iterator();
                while (iterator.hasNext()) {
                    MetricsCodeBulkAlarmData data = iterator.next();
                    if (StringUtils.isNotBlank(Key) && !Key.equals(data)) {
                        iterator.remove();
                        continue;
                    }
                    if (StringUtils.isNotBlank(methodName) && !methodName.equals(data.getMethodName())) {
                        iterator.remove();
                        continue;
                    }
                }
            }

            if (arrayList.size() > MAX_LIST_SIZE) {
                arrayList = arrayList.subList(0, MAX_LIST_SIZE);
            }

            model.addAttribute("methodMonitorList", arrayList);
            model.addAttribute("dTime", dTime);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "monitor/customAlarmMonitorList";
    }


    @RequestMapping(value = "/getCustomAlarmMonitorDetail/{AppName}/{Traceid}")
    public String getCustomAlarmMonitorDetail(@PathVariable(value = "AppName") String AppName, @PathVariable(value = "Traceid") String Traceid, Model model) {
        model.addAttribute("Traceid", Traceid);
        model.addAttribute("AppName", AppName);
        return "monitor/customAlarmMonitorDetail";
    }


//    @ResponseBody
//    @RequestMapping(value = "/getCustomAlarmMonitorDetailDto/{AppName}/{Traceid}", method = {RequestMethod.GET, RequestMethod.POST})
//    public TraceResponseDTO<TreeNode> getCustomAlarmMonitorDetailDto(@PathVariable(value = "AppName") String AppName, @PathVariable(value = "Traceid") String Traceid, HttpServletRequest request) {
//        return getCustomMonitorDetailInfo(Traceid, AppName, CODE_BULK_NAME_ALARM);
//    }


    @RequestMapping(value = "/getCustomMonitorDetail/{AppName}/{Traceid}")
    public String getCustomMonitorDetail(@PathVariable(value = "AppName") String AppName, @PathVariable(value = "Traceid") String Traceid, Model model) {
        model.addAttribute("Traceid", Traceid);
        model.addAttribute("AppName", AppName);
        return "monitor/customMonitorDetail";
    }

    @Override
    protected UserService getUserService() {
        return userService;
    }


//    @ResponseBody
//    @RequestMapping(value = "/getCustomMonitorDetailDto/{AppName}/{Traceid}", method = {RequestMethod.GET, RequestMethod.POST})
//    public TraceResponseDTO<TreeNode> getCustomMonitorDetailDto(@PathVariable(value = "AppName") String AppName, @PathVariable(value = "Traceid") String Traceid, HttpServletRequest request) {
//
//        return getCustomMonitorDetailInfo(Traceid, AppName, CODE_BULK_NAME);
//    }
//
//
//    private TraceResponseDTO<TreeNode> getCustomMonitorDetailInfo(String Traceid, String AppName, String tableNmae) {
//        BasicDBObject basicDBObject = new BasicDBObject();
//
//        if (StringUtils.isNotBlank(Traceid)) {
//            basicDBObject.put("Traceid", Traceid);
//        }
//        if (StringUtils.isNotBlank(AppName)) {
//            basicDBObject.put("AppName", AppName);
//        }
//        TraceResponseDTO<TreeNode> responseDTO = new TraceResponseDTO<>();
//        responseDTO.setCode(ReturnCode.ACTIVE_FAILURE.code());
//        List<Document> findMsg = mongoService.findMsg(tableNmae, basicDBObject, 1);
//
//        responseDTO.setTreeData(JSONArray.toJSONString(findMsg));
//        return responseDTO;
//    }


}
