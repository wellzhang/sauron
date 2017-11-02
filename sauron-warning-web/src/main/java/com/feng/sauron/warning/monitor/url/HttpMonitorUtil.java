package com.feng.sauron.warning.monitor.url;

import com.feng.sauron.warning.domain.UrlRules;
import com.feng.tsc.xhttpclient.sync.syncImpl.DefaultBXHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xubiao.fan on 2016/5/11.
 * Http 监控工具类，提供GET,POST,HEAD
 */
public class HttpMonitorUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpMonitorUtil.class);

    public HttpResult executeByHttp(UrlRules urlRules){

        if(urlRules.getMonitorUrl() == null || urlRules.getMonitorUrl().isEmpty())
            return new HttpResult(-1,"request url is empty!");
        try {
            if (urlRules.getRequestMode() == UrlRules.RequestMode.GET.val())
                return excueteByGet(urlRules);
            else if (urlRules.getRequestMode() == UrlRules.RequestMode.HEAD.val())
                return excueteByHead(urlRules);
            else if (urlRules.getRequestMode() == UrlRules.RequestMode.POST.val())
                return excueteByPost(urlRules);
            else
                return new HttpResult(-1,"unsupport request method");

        } catch (IOException e) {
                //e.printStackTrace();
                logger.error(urlRules.getId()+":"+ urlRules.getMonitorUrl(),e);
            return new HttpResult(500,e.getMessage());
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error(urlRules.getId()+":"+ urlRules.getMonitorUrl(),e);
            return new HttpResult(500,e.getMessage());
        }

    }


    private HttpResult excueteByGet(UrlRules urlRules) throws Exception {

        DefaultBXHttpClient client = new DefaultBXHttpClient();
        //CloseableHttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
        String requestUrl = urlRules.getMonitorUrl();
        try{
            //replace host  to ip
            if (urlRules.getIsConfigHost() == UrlRules.IsConfigHost.ConfigHost.val() && !urlRules.getHostIp().isEmpty())
                requestUrl = this.converDodainToIp(requestUrl,urlRules.getHostIp());
            HttpGet httpget = new HttpGet(requestUrl);
            if(urlRules.getTimeout() != null && urlRules.getTimeout()>0 &&  urlRules.getTimeout()< 30 * 1000){ // time out
                RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(urlRules.getTimeout()).build();
                httpget.setConfig(requestConfig);
            }
            //cookie
            if(urlRules.getCookies()!=null && urlRules.getCookies().trim().length()>0){
                httpget.setHeader("Cookie",urlRules.getCookies());
            }

            HttpResponse response =  client.execute(httpget);
            HttpResult result ;
            if(urlRules.getIsDefaultCode() == UrlRules.IsDefaultCode.DefaultCode.val())
                result = new HttpResult(response.getStatusLine().getStatusCode(),null);
            else
                result = new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
            return result;
        }finally {
            client.close();
        }

    }

    private HttpResult excueteByHead(UrlRules urlRules) throws Exception {

        DefaultBXHttpClient client = new DefaultBXHttpClient();
        //CloseableHttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
        String requestUrl = urlRules.getMonitorUrl();
        try{
            //replace host  to ip
            if (urlRules.getIsConfigHost() == UrlRules.IsConfigHost.ConfigHost.val() && !urlRules.getHostIp().isEmpty())
                requestUrl = this.converDodainToIp(requestUrl,urlRules.getHostIp());
            HttpHead httpHead = new HttpHead(requestUrl);
            if(urlRules.getTimeout() != null && urlRules.getTimeout()>0 &&  urlRules.getTimeout()< 30 * 1000){ // time out
                RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(urlRules.getTimeout()).build();
                httpHead.setConfig(requestConfig);
            }
            //cookie
            if(urlRules.getCookies()!=null && urlRules.getCookies().trim().length()>0){
                httpHead.setHeader("Cookie",urlRules.getCookies());
            }
            HttpResponse response =  client.execute(httpHead);
            HttpResult result =   new HttpResult(response.getStatusLine().getStatusCode(),null);

            return result;
        }finally {
            client.close();
        }

    }


    private HttpResult excueteByPost(UrlRules urlRules) throws Exception {

        DefaultBXHttpClient client = new DefaultBXHttpClient();
        //CloseableHttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
        String requestUrl = urlRules.getMonitorUrl();
        try{
            //replace host  to ip
            if (urlRules.getIsConfigHost() == UrlRules.IsConfigHost.ConfigHost.val() && !urlRules.getHostIp().isEmpty())
                requestUrl = this.converDodainToIp(requestUrl,urlRules.getHostIp());
            HttpPost httpPost = new HttpPost(requestUrl);
            if(urlRules.getTimeout() != null && urlRules.getTimeout()>0 &&  urlRules.getTimeout()< 30 * 1000){ // time out
                RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(urlRules.getTimeout()).build();
                httpPost.setConfig(requestConfig);
            }
            //cookie
            if(urlRules.getCookies()!=null && urlRules.getCookies().trim().length()>0){
                httpPost.setHeader("Cookie",urlRules.getCookies());
            }
            //set param
            String param = urlRules.getParam();
            if(param!=null && param.trim().length()>0){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                String[] params = param.split(",");
                for(String pm :params){
                    String[] kvs = pm.split("=");
                    if(kvs!=null && kvs.length == 2)
                        nvps.add(new BasicNameValuePair(kvs[0], kvs[1]));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            }
            HttpResponse response =  client.execute(httpPost);
            HttpResult result = null;
            if(urlRules.getIsDefaultCode() == UrlRules.IsDefaultCode.DefaultCode.val())
                result = new HttpResult(response.getStatusLine().getStatusCode(),null);
            else
                result = new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
            return result;
        }finally {
            client.close();
        }

    }
    private String converDodainToIp(String requestUrl,String hostIp){
        int pos = requestUrl.indexOf("/", 7);
        if(pos>0)
          return  requestUrl = "http://" + hostIp +  requestUrl.substring(pos);

        else
            return requestUrl = "http://" + hostIp;
    }




}
