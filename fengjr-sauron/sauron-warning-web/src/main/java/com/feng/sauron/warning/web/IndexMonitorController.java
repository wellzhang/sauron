package com.feng.sauron.warning.web;

import com.alibaba.fastjson.JSON;
import com.feng.sauron.warning.domain.App;
import com.feng.sauron.warning.service.*;
import com.feng.sauron.warning.service.base.AppService;
import com.feng.sauron.warning.service.base.UserService;
import com.feng.sauron.warning.web.base.BaseController;
import com.feng.sauron.warning.web.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by lianbin.wang on 2016/11/14.
 */
@Controller
@RequestMapping("/index")
public class IndexMonitorController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AppService appService;

    private static final String implName = "MetricsDataService"; //"MetricsDataService"

    @Autowired
    @Qualifier(implName)
    private TraceService traceService;

    @Autowired
    @Qualifier("CachedScatterService")
    private ScatterService scatterService;

    @Autowired
    @Qualifier(implName)
    private CallstackService callstackService;

    @Autowired
    @Qualifier("Top10ServiceImpl")
    private Top10Service top10Service;

    @Autowired
    @Qualifier("CachedIndexMonitorService")
    private TopologyService topologyService;


    @RequestMapping("/applist")
    @ResponseBody
    public Map<String, Object> appList() {
        Map<String, Object> map = new HashMap<>();
        List<App> list = appService.findByPage(1, 1000, null, getCreatorId());
        for (App app : list) {
            map.put(app.getName(), app.getName());
        }
        return map;
    }


    @RequestMapping("/scatterData")
    @ResponseBody
    public List<MetricsOriDataVO> scatterData(IndexFormVO vo) {
        IndexFormVO.ParsedFormVO formVO = vo.parse();

        if (logger.isDebugEnabled()) {
            logger.debug("scatterData, param:{}", JSON.toJSONString(formVO));
        }

        Date startTime = formVO.getStartTime();
        Date endTime = formVO.getEndTime();
        String appName = formVO.getAppName();
        String host = formVO.getHost();

        if (endTime.getTime() - startTime.getTime() > 3600 * 1000) {
            logger.error("timeSpan is longer than 1H");
            return Collections.emptyList();
        }

        try {
            List<MetricsOriDataVO> list = scatterService.loadScatterData(startTime, endTime, appName, host);

            /**
             * order
             */
            Collections.sort(list, new Comparator<MetricsOriDataVO>() {
                @Override
                public int compare(MetricsOriDataVO o1, MetricsOriDataVO o2) {
                    return Long.valueOf(o1.getTime()).compareTo(Long.valueOf(o2.getTime()));
                }
            });


            /**
             * 散点图过滤
             */
            boolean status = StringUtils.isNotBlank(vo.getStatus());
            boolean type = StringUtils.isNotBlank(vo.getType());

            if (status || type) {
                Iterator<MetricsOriDataVO> iterator = list.iterator();
                while (iterator.hasNext()) {
                    MetricsOriDataVO m = iterator.next();

                    //暂时不过滤type
//                    if (type && !vo.getType().equals(m.getAppType())) {
//                        iterator.remove();
//                        continue;
//                    }

                    //
                    if (status && !vo.getStatus().equals(m.getStatus())) {
                        iterator.remove();
                        continue;
                    }
                }
            }


            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();

    }


    @RequestMapping("/top10/total")
    @ResponseBody
    public List<Top10Iterm> totalTop10(@RequestParam String appName,
                                       @RequestParam(required = false) String host,
                                       @RequestParam(required = false) String type) {
        List<Top10Iterm> list = top10Service.loadTotal(appName, host, type);
        return list;
    }

    @RequestMapping("/top10/linetotal")
    @ResponseBody
    public List<Top10LineItem> top10LineTotal(
            @RequestParam String appName,
            @RequestParam String url,
            @RequestParam(required = false) String host,
            @RequestParam(required = false) String type) throws UnsupportedEncodingException {
        logger.info("url:" + url);
        url = URLDecoder.decode(url, "utf-8");
        logger.info("url after decode:" + url);
        List<Top10LineItem> list = top10Service.loadLineTotal(appName, host, type, url);
        return list;
    }


    @RequestMapping("/top10/error")
    @ResponseBody
    public List<Top10Iterm> errorTop10(@RequestParam String appName,
                                       @RequestParam(required = false) String host,
                                       @RequestParam(required = false) String type) {
        List<Top10Iterm> list = top10Service.loadError(appName, host, type);
        return list;
    }

    @RequestMapping("/top10/lineerror")
    @ResponseBody
    public List<Top10LineItem> top10LineError(
            @RequestParam String appName,
            @RequestParam String url,
            @RequestParam(required = false) String host,
            @RequestParam(required = false) String type) throws UnsupportedEncodingException {
        logger.info("url:" + url);
        url = URLDecoder.decode(url, "utf-8");
        logger.info("url after decode:" + url);
        List<Top10LineItem> list = top10Service.loadLineError(appName, host, type, url);
        return list;
    }


    //默认查30分钟
    @Value("${monitor.topology.minute:#{30}}")
    private int topologyMinute;

    //拓扑图
    @RequestMapping("/topology")
    @ResponseBody
    public TopologyData topology(@RequestParam String appName,
                                 @RequestParam(required = false) String host,
                                 @RequestParam(required = false, defaultValue = "-1") int minute) {

        if (minute <= 0) {
            minute = this.topologyMinute;
        }

        Date endTime = new Date();
        Date startTime = new Date(endTime.getTime() - (60 * 1000) * minute);

        logger.debug("topology minute:{}", minute);

        TopologyData data = topologyService.load(appName, host, startTime, endTime);
        data.setStartTime(startTime.getTime());
        data.setEndTime(endTime.getTime());

        return data;
    }


    @RequestMapping("/trace")
    public ModelAndView trace(@RequestParam String appName[], @RequestParam String[] traceID) {
        if (logger.isDebugEnabled()) {
            logger.debug("traceID:{}", JSON.toJSONString(traceID));
            logger.debug("appName:{}", JSON.toJSONString(appName));
        }

        ModelAndView view = new ModelAndView("callstack");

        List<TraceItem> list = traceService.loadTrace(appName, traceID);
        view.addObject("list", list);

        return view;
    }


    @RequestMapping("/callstack")
    @ResponseBody
    public List<CallstackItem> callstack(@RequestParam String traceID, @RequestParam String appName) {
        return callstackService.loadCallStack(traceID, appName);
    }

    @Autowired
    @Qualifier("AppOverViewServiceImpl")
    private AppOverViewService overViewService;

    @RequestMapping("/overview")
    @ResponseBody
    public Map<String, Object> overView(@RequestParam String appName) {
        return overViewService.loadOverView(appName);
    }


    @RequestMapping("/apphost")
    public ModelAndView appHost(@RequestParam String appName, @RequestParam String host) {
        Map<String, LinkedHashMap<String, Object>> variables = overViewService.loadAppVariables(appName, host);
        return new ModelAndView("app_host_detail").addObject("appName", appName).addObject("host", host).addObject("variables", variables);
    }

    @RequestMapping("/apphost/chart")
    @ResponseBody
    public List<OverViewLineData> charData(@RequestParam String appName, @RequestParam String host, @RequestParam String type) {
        switch (type) {
            case "cpu":
                return formatNumbers(overViewService.loadMachineCPU(appName, host), PercentVisitor);
            case "mem":
                return formatNumbers(overViewService.loadMachineMemory(appName, host), MBVisitor);
            case "con":
                return overViewService.loadMachineConnection(appName, host);
//            case "free":
//                return formatNumbers(overViewService.loadMachineFreePhysicalMemory(appName, host), MBVisitor);
            case "vmcon":
                return overViewService.loadProcessConnection(appName, host);
            case "vmheap":
                return formatNumbers(overViewService.loadProcessJVMHeapMemory(appName, host), MBVisitor);
            case "vmgc":
                return overViewService.loadProcessJVMGC(appName, host);
            case "vmthread":
                return overViewService.loadProcessJVMThreads(appName, host);
            case "vmnonheap":
                return formatNumbers(overViewService.loadProcessJVMNonHeapMemory(appName, host), MBVisitor);
            default:
                return Collections.emptyList();
        }
    }

    static NumberVisitor MBVisitor = new NumberVisitor() {
        @Override
        public Number visit(Number number) {
            Double value = (Double) number;
            double mb = value.longValue() / 1024 / 1024.0;
            int tmp = (int) (mb * 100);
            double v = tmp / 100.0;
            return v;
        }
    };

    static NumberVisitor PercentVisitor = new NumberVisitor() {
        @Override
        public Number visit(Number number) {
            Double value = (Double) number;
            int tmp = (int) (value.doubleValue() * 10000);
            double v = tmp / 100.0;
            return v;
        }
    };


    private List<OverViewLineData> formatNumbers(List<OverViewLineData> list, NumberVisitor visitor) {
        for (OverViewLineData lineData : list) {
            LinkedHashMap<Long, List<Number>> lineDatas = lineData.getLineDatas();
            for (List<Number> numbers : lineDatas.values()) {
                for (int i = 0; i < numbers.size(); i++) {
                    Number number = numbers.get(i);
                    numbers.set(i, visitor.visit(number));
                }
            }
        }
        return list;
    }

    @Override
    protected UserService getUserService() {
        return null;
    }

    interface NumberVisitor {
        Number visit(Number number);
    }
}