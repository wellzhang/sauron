package com.feng.sauron.warning.web;

import com.feng.sauron.warning.common.Page;
import com.feng.sauron.warning.common.ResponseDTO;
import com.feng.sauron.warning.common.ReturnCode;
import com.feng.sauron.warning.domain.Metrics;
import com.feng.sauron.warning.domain.WarningEvent;
import com.feng.sauron.warning.service.MetricOptInfoService;
import com.feng.sauron.warning.service.MetricsService;
import com.feng.sauron.warning.service.WarningEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Liuyb on 2015/12/24.
 */
@RequestMapping("/event")
public class WarningEventController {
    @Autowired
    WarningEventService eventService;
    @Autowired
    MetricsService metricsService;
    @Autowired
    MetricOptInfoService metricOptInfoService;

    @RequestMapping(value = "/list/{pageNo}/{pageSize}")
    public String listContact(@PathVariable("pageNo") int pageNo,
                              @PathVariable("pageSize") int pageSize,
                              Model model) {


        Page<WarningEvent> pageData = new Page<WarningEvent>();
        pageData.setDataList(eventService.selectByPager(pageNo, pageSize, null, null, null, null));
        pageData.setPageSize(pageSize);
        pageData.setPageNO(pageNo);
        pageData.setTotal(eventService.selectCount(null, null, null, null));
        model.addAttribute("pageData", pageData);
        model.addAttribute("metricOptList", metricOptInfoService.selectAllMetricsOpt(0).getAttach());
        return "event/eventList";
    }

    @RequestMapping(value = "/detail/{eventId}")
    @ResponseBody
    public ResponseDTO<List<Metrics>> getMetricsDetail(@PathVariable("eventId") long eventId) {
        ResponseDTO<List<Metrics>> responseDTO = new ResponseDTO<>(ReturnCode.ACTIVE_EXCEPTION);
        try {
            responseDTO.setAttach(metricsService.getMetricsByEvent(eventId));
            responseDTO.setReturnCode(ReturnCode.ACTIVE_SUCCESS);
        } catch (Exception ex) {
            responseDTO.setMsg(ex.getMessage());
        }
        return responseDTO;
    }

    @RequestMapping(value = "/query")
    public String queryEvent(HttpServletRequest request, Model model) {
        String appName = request.getParameter("appName");
        String hostName = request.getParameter("hostName");
        String methodName = request.getParameter("methodName");
        String instantName = request.getParameter("instantName");
        Page<WarningEvent> pageData = new Page<>();
        pageData.setDataList(eventService.selectByPager(1, 10, appName, methodName, hostName, instantName));
        pageData.setPageSize(10);
        pageData.setPageNO(1);
        pageData.setTotal(eventService.selectCount(appName, methodName, hostName, instantName));
        model.addAttribute("pageData", pageData);
        model.addAttribute("metricOptList", metricOptInfoService.selectAllMetricsOpt(0).getAttach());
        return "event/eventList";
    }
}
