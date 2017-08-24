package com.feng.sauron.warning.monitor.url;

import com.feng.sauron.warning.domain.UrlRules;
import org.junit.Test;

/**
 * Created by xubiao.fan on 2016/5/11.
 */
public class HttpMonitorUtilTest {


    @Test
    public void testExec(){


        for(int m=0 ;m<10 ;m++){
            UrlRules   urlRules = new UrlRules();

            urlRules.setMonitorUrl("http://sdk.entinfo.cn:801/mdsmssend.ashx");
            urlRules.setRequestMode((byte)UrlRules.RequestMode.GET.val());
            //urlRules.setRequestMode((byte)UrlRules.RequestMode.HEAD.val());
            //urlRules.setRequestMode((byte)UrlRules.RequestMode.POST.val());
            //urlRules.setParam("user=kkinfo,password=1233445633,keycode=dd12");
            urlRules.setIsConfigHost((byte)UrlRules.IsConfigHost.NoneConfigHost.val());
            //urlRules.setHostIp("220.181.112.244");
            urlRules.setIsDefaultCode((byte)UrlRules.IsDefaultCode.NoneDefaultCode.val());
            //urlRules.setCookies("hello=word");
            urlRules.setTimeout(1);
            HttpMonitorUtil util = new HttpMonitorUtil();
            HttpResult result = util.executeByHttp(urlRules);

            System.out.println("------------------------------------------------");

            System.out.println("code:" + result.getStatusCode());
            System.out.println("body:" + result.getResult());
        }







    }
}
