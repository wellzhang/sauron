package com.feng.sauron.warning.service;

import com.feng.sauron.warning.util.DateUtils;
import com.feng.sauron.warning.util.InfluxdbUtils;
import com.feng.sauron.warning.util.JsonUtils;
import com.feng.sauron.warning.web.vo.Top10Iterm;
import com.feng.sauron.warning.web.vo.Top10LineItem;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年11月16日 下午6:17:53
 */
@Service("Top10ServiceImpl")
public class Top10ServiceImpl implements Top10Service {

    public static void main(String[] args) {

        Top10ServiceImpl top10ServiceImpl = new Top10ServiceImpl();

        List<Top10Iterm> loadTotal3 = top10ServiceImpl.loadTotal("sauron", null, null);

        System.out.println(JsonUtils.toLongJSon(loadTotal3));

        List<Top10LineItem> loadLineTotal = top10ServiceImpl.loadLineTotal("sauron", null, "sauron", "c.f.s.t.w.RocketMQServlet.doPost(HttpServletRequest,HttpServletResponse)");

        System.out.println(JsonUtils.toLongJSon(loadLineTotal));

    }

    @Override
    public List<Top10Iterm> loadTotal(String appName, String host, String type) {

        if (appName == null || appName.length() == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("select top(tp90,10)  as tp90, method ,type   from sauron  where appName = '" + appName + "' ");

        if (host != null && host.length() > 0) {
            sb.append(" and hostName = '" + host + "' ");
        }

        if (type != null && type.length() > 0) {
            sb.append(" and type = '" + type + "' ");
        }

        String format = DateUtils.format(new Date((System.currentTimeMillis() - 30 * 60 * 1000) - 8 * 60 * 60 * 1000), "yyyy-MM-dd HH:mm");

        format += ":00";

        String now = DateUtils.format(new Date(System.currentTimeMillis() - 8 * 60 * 60 * 1000), "yyyy-MM-dd HH:mm");

        now += ":00";

        // test
//		format = "2016-11-15 16:20:00";
//		now = "2016-11-15 16:50:00";

        sb.append(" and time >= '").append(format).append("'");

        sb.append(" and time <= '").append(now).append("'");

        List<List<String>> value = getValue(sb.toString(), InfluxdbUtils.dbName);

        List<Top10Iterm> arrayList = new ArrayList<>();

        for (List<String> list2 : value) {
            if (list2.size() < 4) {
                continue;
            }
            String time = list2.get(0);
            String method = list2.get(2);
            String type_now = list2.get(3);

            Top10Iterm top10Iterm = new Top10Iterm();

            top10Iterm.setAppName(appName);
            top10Iterm.setHost(host);
            top10Iterm.setType(type_now);
            top10Iterm.setUrl(method);
            top10Iterm.setTime(time);

            arrayList.add(top10Iterm);
        }

        return arrayList;
    }

    private static List<List<String>> getValue(String sql, String dbName) {

        List<List<String>> tagDeatilValues = new ArrayList<>();

        QueryResult queryDetailResult = InfluxdbUtils.getInfluxDB().query(new Query(sql, dbName));

        if (!queryDetailResult.hasError() && queryDetailResult.getResults() != null) {

            for (QueryResult.Result result : queryDetailResult.getResults()) {

                if (result != null && !result.hasError() && result.getSeries() != null) {

                    for (QueryResult.Series series : result.getSeries()) {

                        if (series != null && series.getValues() != null) {

                            for (List<Object> valusList : series.getValues()) {

                                if (valusList != null) {
                                    List<String> tagValues = new ArrayList<>();

                                    for (Object tagValue : valusList) {

                                        if (tagValue != null) {

                                            tagValues.add(tagValue.toString());
                                        }
                                    }
                                    tagDeatilValues.add(tagValues);
                                }
                            }

                        }
                    }
                }
            }
        }

        return tagDeatilValues;
    }

    @Override
    public List<Top10LineItem> loadLineTotal(String appName, String host, String type, String url) {

        if (appName == null || appName.length() == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("select top(tp90,1),traceId from sauron  where appName = '" + appName + "' ");

        if (host != null && host.length() > 0) {
            sb.append(" and hostName = '" + host + "' ");
        }

        if (type != null && type.length() > 0) {
            sb.append(" and type = '" + type + "' ");
        }

        if (url != null && url.length() > 0) {
            sb.append(" and method = '" + url + "' ");
        }

        String format = DateUtils.format(new Date((System.currentTimeMillis() - 30 * 60 * 1000) - 8 * 60 * 60 * 1000), "yyyy-MM-dd HH:mm");

        format += ":00";

        String now = DateUtils.format(new Date(System.currentTimeMillis() - 8 * 60 * 60 * 1000), "yyyy-MM-dd HH:mm");

        now += ":00";


        // test
//		format = "2016-11-15 16:20:00";
//		now = "2016-11-15 16:50:00";

        sb.append(" and time >= '").append(format).append("'");

        sb.append(" and time <= '").append(now).append("'");

        sb.append(" GROUP BY time(1m) ");

        List<List<String>> point = getPoint(sb.toString(), InfluxdbUtils.dbName);

        ArrayList<Top10LineItem> arrayList = new ArrayList<>();

        for (List<String> list : point) {

            if (list.size() < 3) {
                continue;
            }

            String time = list.get(0);
            String costTime = list.get(1);
            String traceid = list.get(2);

            time = time.replace("T", " ").replace("Z", "");

            Long parseTime = DateUtils.parseDate(time, "yyyy-MM-dd HH:mm:ss").getTime() + 8 * 60 * 60 * 1000;

            long longValueCostTime = new Double(costTime).longValue();

            Top10LineItem top10LineItem = new Top10LineItem(traceid, parseTime, longValueCostTime);

            arrayList.add(top10LineItem);

        }

        return arrayList;
    }

    private static List<List<String>> getPoint(String sql, String dbName) {

        List<List<String>> tagDeatilValues = new ArrayList<>();

        QueryResult queryDetailResult = InfluxdbUtils.getInfluxDB().query(new Query(sql, dbName));

        if (!queryDetailResult.hasError() && queryDetailResult.getResults() != null) {

            for (QueryResult.Result result : queryDetailResult.getResults()) {

                if (result != null && !result.hasError() && result.getSeries() != null) {

                    for (QueryResult.Series series : result.getSeries()) {

                        if (series != null && series.getValues() != null) {

                            for (List<Object> valusList : series.getValues()) {

                                if (valusList != null) {
                                    List<String> tagValues = new ArrayList<>();

                                    for (Object tagValue : valusList) {

                                        if (tagValue != null) {

                                            tagValues.add(tagValue.toString());
                                        }
                                    }
                                    tagDeatilValues.add(tagValues);
                                }
                            }

                        }
                    }
                }
            }
        }

        return tagDeatilValues;
    }

    @Override
    public List<Top10Iterm> loadError(String appName, String host, String type) {

        if (appName == null || appName.length() == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("select top(exception,10)  as tp90, method ,type   from sauron  where appName = '" + appName + "' ");

        if (host != null && host.length() > 0) {
            sb.append(" and hostName = '" + host + "' ");
        }

        if (type != null && type.length() > 0) {
            sb.append(" and type = '" + type + "' ");
        }

        String format = DateUtils.format(new Date((System.currentTimeMillis() - 30 * 60 * 1000) - 8 * 60 * 60 * 1000), "yyyy-MM-dd HH:mm");

        format += ":00";

        String now = DateUtils.format(new Date(System.currentTimeMillis() - 8 * 60 * 60 * 1000), "yyyy-MM-dd HH:mm");

        now += ":00";


        // test
//		format = "2016-11-15 11:00:00";
//		now = "2016-11-15 11:30:00";

        sb.append(" and time >= '").append(format).append("'");

        sb.append(" and time <= '").append(now).append("'");

        List<List<String>> value = getValue(sb.toString(), InfluxdbUtils.dbName);

        List<Top10Iterm> arrayList = new ArrayList<>();

        for (List<String> list2 : value) {
            if (list2.size() < 4) {
                continue;
            }
            String time = list2.get(0);
            String method = list2.get(2);
            String type_now = list2.get(3);

            Top10Iterm top10Iterm = new Top10Iterm();

            top10Iterm.setAppName(appName);
            top10Iterm.setHost(host);
            top10Iterm.setType(type_now);
            top10Iterm.setUrl(method);
            top10Iterm.setTime(time);

            arrayList.add(top10Iterm);

        }

        return arrayList;
    }

    @Override
    public List<Top10LineItem> loadLineError(String appName, String host, String type, String url) {

        if (appName == null || appName.length() == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("select top(exception,1),traceId from sauron  where appName = '" + appName + "' ");

        if (host != null && host.length() > 0) {
            sb.append(" and hostName = '" + host + "' ");
        }

        if (type != null && type.length() > 0) {
            sb.append(" and type = '" + type + "' ");
        }

        if (url != null && url.length() > 0) {
            sb.append(" and method = '" + url + "' ");
        }

        String format = DateUtils.format(new Date((System.currentTimeMillis() - 30 * 60 * 1000) - 8 * 60 * 60 * 1000), "yyyy-MM-dd HH:mm");

        format += ":00";

        String now = DateUtils.format(new Date(System.currentTimeMillis() - 8 * 60 * 60 * 1000), "yyyy-MM-dd HH:mm");

        now += ":00";


        // test
//		format = "2016-11-15 11:00:00";
//		now = "2016-11-15 11:30:00";

        sb.append(" and time >= '").append(format).append("'");

        sb.append(" and time <= '").append(now).append("'");

        sb.append(" GROUP BY time(1m) ");

        List<List<String>> point = getPoint(sb.toString(), InfluxdbUtils.dbName);

        ArrayList<Top10LineItem> arrayList = new ArrayList<>();

        for (List<String> list : point) {
            if (list.size() < 3) {
                continue;
            }

            String time = list.get(0);
            String costTime = list.get(1);
            String traceid = list.get(2);

            time = time.replace("T", " ").replace("Z", "");

            Long parseTime = DateUtils.parseDate(time, "yyyy-MM-dd HH:mm:ss").getTime() + 8 * 60 * 60 * 1000;

            long longValueCostTime = new Double(costTime).longValue();

            Top10LineItem top10LineItem = new Top10LineItem(traceid, parseTime, longValueCostTime);

            arrayList.add(top10LineItem);

        }

        return arrayList;
    }

}
