package com.feng.sauron.warning.service;

import com.feng.sauron.warning.domain.*;
import com.feng.sauron.warning.monitor.url.HttpMonitorUtil;
import com.feng.sauron.warning.monitor.url.HttpResult;
import com.feng.sauron.warning.service.base.*;
import com.feng.sauron.warning.task.RunHttpMonitorTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by xubiao.fan on 2016/5/12.
 */
@Service
public class HttpMonitorService {

    private static final Logger logger = LoggerFactory.getLogger(HttpMonitorService.class);

    @Autowired
    UrlRulesService urlRulesService;
    @Autowired
    UrlMonitorService urlMonitorService;
    @Autowired
    UrlTraceFailedService urlTraceFailedService;
    @Autowired
    AlarmService alarmService;
    @Autowired
    RuleReceiverService ruleReceiverService;
    @Autowired
    ContactsService contactsService;



    public Map<Long, Integer> getAUrlRulesIds() {

        Map<Long, Integer> ids = new HashMap<>();
        int page = 1, pageNum = 100;
        List<UrlRules> urlRules = urlRulesService.findByPage(page, pageNum, null, null,null);

        while (urlRules.size() > 0) {
            for (UrlRules urlRule : urlRules) {
                if(urlRule.getIsEnabled() == UrlRules.IsEnabled.Enabled.val())//
                    ids.put(urlRule.getId(), urlRule.getRequestInterval());
            }
            page++;
            urlRules = urlRulesService.findByPage(page, pageNum, null, null,null);

        }
        return ids;
    }


    public void setUrlRulesService(UrlRulesService urlRulesService) {
        this.urlRulesService = urlRulesService;
    }
    @Transactional
    public void runUrlMonitorById(long rulesById) {

        UrlRules urlRule = urlRulesService.findUrlRulesById(rulesById);
        if (urlRule == null)
            return;
        HttpMonitorUtil httpMonitorUtil = new HttpMonitorUtil();
        //get urlmonitor
        UrlMonitor urlMonitor = urlMonitorService.findByUrlRuleId(urlRule.getId());
        if(urlMonitor == null){
            urlMonitor = new UrlMonitor();
            urlMonitor.setTotalTimes(0);
            urlMonitor.setFailTimes(0);
            urlMonitor.setCreatedTime(new Date());
            urlMonitor.setUrlRulesId(urlRule.getId());
        }

        urlMonitor.setUpdataTime(new Date());
        HttpResult result = httpMonitorUtil.executeByHttp(urlRule);
        boolean failed = false;
        if (UrlRules.IsDefaultCode.DefaultCode.val() == urlRule.getIsDefaultCode())
            if (!(result.getStatusCode() == 200 || result.getStatusCode() == 301 || result.getStatusCode() == 302))
                failed = true;
        if (UrlRules.IsDefaultCode.NoneDefaultCode.val() == urlRule.getIsDefaultCode())
            if (!(result.getStatusCode() == urlRule.getCustomCode()))
                failed = true;
        if (UrlRules.IsContain.Contain.val() == urlRule.getIsContain())
            if (!result.getResult().equals(urlRule.getMatchContent()))
                failed = true;


        if ( failed ) {
            urlMonitor.setFailTimes(urlMonitor.getFailTimes() + 1);
            //send alarm
            List<RuleReceiversKey> ruleReceiversKeys = ruleReceiverService.findReceiversByRule(urlRule.getId(),RuleReceiversKey.Type.Url.val()); // type
            String msg = urlRule.getMonitorUrl() + " return:"+ result.getStatusCode() +
                    ( result.getResult() == null ? "" : ",body:"+
                            ( result.getResult().length()>10 ? result.getResult().substring(0,10) :result) + "...");
            if(ruleReceiversKeys.size()>0){
                List<String> mobileNums = new ArrayList<String>();
                for(RuleReceiversKey ruleReceiversKey:ruleReceiversKeys )
                {
                    Contact contact =  contactsService.findContactById(ruleReceiversKey.getContactId());
                    mobileNums.add(contact.getMobile());
                }
                alarmService.sendSms(mobileNums,msg,urlRule.getTemplate());
            }else
            {
                logger.info(  "none of contact : " + msg);
            }

        }
        urlMonitor.setTotalTimes(urlMonitor.getTotalTimes() + 1);
        long monitorId = 0;
        if(urlMonitor.getId()!=null )
             urlMonitorService.updateUrlMonitor(urlMonitor);
        else
            monitorId= urlMonitorService.addUrlMonitor(urlMonitor);

        if(failed){
            UrlTraceFailed urlTraceFailed = new UrlTraceFailed();
            urlTraceFailed.setCreatedTime(
                    new Date());
            urlTraceFailed.setResult(result.toString());
            urlTraceFailed.setUrlMonitorId(urlMonitor.getId()!=null ?urlMonitor.getId():monitorId);
            //save data;
            urlTraceFailedService.addUrlTraceFailed(urlTraceFailed);
        }
    }


    @Transactional
    public void addOrUpdateUrlRules(UrlRules urlRules,String contacts,String contactType,boolean isExist){
        if(isExist){
            urlRulesService.updateUrlRules(urlRules);
            ruleReceiverService.delReceiversByRuleId(urlRules.getId(),RuleReceiversKey.Type.Url.val());
        }else{
            urlRulesService.addSelectiveRules(urlRules);
        }
        List<RuleReceiversKey> ruleReceiversList = new ArrayList<RuleReceiversKey>();
        if(StringUtils.isNotBlank(contacts)){
            if(StringUtils.isBlank(contactType)){
                contactType = "0";
            }
            String[] contactArr = contacts.split(";");
            RuleReceiversKey ruleReceiversKey = null;
            long urlRuleId = urlRules.getId();
            for(String contact:contactArr){
                ruleReceiversKey = new RuleReceiversKey();
                ruleReceiversKey.setRuleId(urlRuleId);
                ruleReceiversKey.setType(RuleReceiversKey.Type.Url.val());
                ruleReceiversKey.setContactId(Long.parseLong(contact));
                ruleReceiversKey.setNotifyMode(contactType);
                ruleReceiversList.add(ruleReceiversKey);
            }
            ruleReceiverService.addReceiversToRule(ruleReceiversList);
            RunHttpMonitorTask.watch();
        }
    }

    public void enableOrDisableUrlRule(String ids,int status){
        String ruleIds[] = ids.split(";");
        UrlRules urlRules = null;
        for(String ruleId:ruleIds){
            urlRules = new UrlRules();
            urlRules.setId(Long.valueOf(ruleId));
            urlRules.setIsEnabled((byte)status);
            urlRulesService.updateUrlRules(urlRules);
        }
        RunHttpMonitorTask.watch();
    }

    public void delUrlRules(String ids){
        String ruleIds[] = ids.split(";");
        for(String ruleId:ruleIds){
            urlRulesService.delUrlRules(Long.valueOf(ruleId));
        }
        RunHttpMonitorTask.watch();
    }


}
