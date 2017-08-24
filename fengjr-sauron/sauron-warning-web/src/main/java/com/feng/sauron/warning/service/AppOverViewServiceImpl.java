package com.feng.sauron.warning.service;

import java.text.DecimalFormat;
import java.util.*;

import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.stereotype.Service;

import com.feng.sauron.warning.util.DateUtils;
import com.feng.sauron.warning.util.InfluxdbUtils;
import com.feng.sauron.warning.web.vo.OverViewLineData;
import com.fengjr.sauron.commons.utils.JsonUtils;

/**
 * @author wei.wang@fengjr.com
 * @version 2016年11月25日 下午2:38:13
 */
@Service("AppOverViewServiceImpl")
public class AppOverViewServiceImpl implements AppOverViewService {

    @Override
    public Map<String, Object> loadOverView(String appName) {

        if (appName == null || appName.length() == 0) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new HashMap<>();

        StringBuilder sb = new StringBuilder();

        sb.append("select  *  from sauron_jvm   where time > (now() - 2m) and appName = '" + appName + "'   group by hostName  order by time desc limit 1");

        // {"10.255.73.161":[["2016-11-25T09:32:20Z","4390912.0","5.0331648E7","2086272.0","0.0","0.0","19.0","2.0","0.0","0.0","0.0","21.0","2.85814784E8","5.41929472E9","6.651E-4","5.626E8","0.0174176","6.134747136E9","6.274670592E9","5.37919488E8","5.37919488E8","4.42022608E8","0.0","0.0","2.058878976E9","2.058878976E9","5.06658976E8","0.0","0.0","33.0","8.0","0.0","1.0","0.0","42.0","4.2139648E7","3.18767104E8","3.9368072E7","1.431830528E9","1.431830528E9","3003040.0","3.7748736E7","2.68435456E8","3.72818E7","8.912896E7","8.912896E7","6.1633328E7","33.0","7.0","10.0","0.0","4.0","12.0","2.0","136.0","sauron","25036","/export/server/sauron-test-web"]]}
        Map<String, List<List<String>>> point = getPoint(sb.toString(), InfluxdbUtils.dbName_jvm);

        for (String host : point.keySet()) {

            List<List<String>> list = point.get(host);

            List<String> ss = list.get(0);// 只取第一个 limit 1

            String cpu = ss.get(16);

            String FreePhysicalMemorySize = ss.get(12);
            String TotalPhysicalMemorySize = ss.get(17);
            Double double1 = new Double(FreePhysicalMemorySize);
            Double double2 = new Double(TotalPhysicalMemorySize);

            String FreeSwapSpaceSize = ss.get(13);
            String TotalSwapSpaceSize = ss.get(18);

            Double double3 = new Double(FreeSwapSpaceSize);
            Double double4 = new Double(TotalSwapSpaceSize);

            Double double5 = (double1 + double3) / (double2 + double4);

            // Double double5 = (double1 + double3) / (double2 + double4) * 100;
            //
            // BigDecimal b = new BigDecimal(double5);
            // Double mem_100 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            String mem = double5.toString();
            String connec = ss.get(34);
            String jvm_thread = ss.get(47);
            String jvm_mem = ss.get(26);
            String jvm_gc = ss.get(53);

            HashMap<String, String> hashMap = new HashMap<>();

            System.err.println(mem);

            hashMap.put("cpu", formatDouble(Double.parseDouble(cpu), "##.##%"));
            hashMap.put("mem", formatDouble(Double.parseDouble(mem), "##.##%"));
            hashMap.put("connec", Double.valueOf(connec).longValue() + "");
            hashMap.put("jvm_thread", Double.valueOf(jvm_thread).longValue() + "");
            hashMap.put("jvm_mem", Double.valueOf(jvm_mem).longValue() / 1024 / 1024 + "MB");
            hashMap.put("jvm_gc", Double.valueOf(jvm_gc).longValue() + "");

            result.put(host, hashMap);

        }

        return result;
    }

    private String formatDouble(double value, String fmt) {
        DecimalFormat format = new DecimalFormat(fmt);
        return format.format(value);
    }

    public static void main(String[] args) {

        AppOverViewServiceImpl appOverViewServiceImpl = new AppOverViewServiceImpl();

        // Map<String, Object> loadOverView = appOverViewServiceImpl.loadOverView("sauron");
        //
        // String longJSon = JsonUtils.toLongJSon(loadOverView);
        //
        // System.out.println(longJSon);

//        List<OverViewLineData> loadMachineCPU = appOverViewServiceImpl.loadProcessConnection("sauron_smsgateway_dubbo", null);
//
//        String longJSon2 = JsonUtils.toLongJSon(loadMachineCPU);
//
//        System.out.println(longJSon2);

        Map<String, LinkedHashMap<String, Object>> loadAppVariables = appOverViewServiceImpl.loadAppVariables("sauron_smsgateway_dubbo", null);

        String longJSon = JsonUtils.toLongJSon(loadAppVariables);

         System.out.println(longJSon);

    }

    public List<OverViewLineData> loadMachineCPU(String appName, String host) {

        String[] keys = {"Cpu.ProcessCpuLoad", "Cpu.SystemCpuLoad"};
        String[] alias = {"Process", "System"};

        return load(appName, host, keys, alias);
    }

    @Override
    public List<OverViewLineData> loadMachineMemory(String appName, String host) {

        String[] keys = {"Cpu.TotalPhysicalMemorySize", "Cpu.TotalSwapSpaceSize", "Cpu.FreePhysicalMemorySize", "Cpu.FreeSwapSpaceSize"};
        String[] alias = {"TotalPhysical", "TotalSwap", "FreePhysical", "FreeSwap"};

        return load(appName, host, keys, alias);
    }

    @Override
    public List<OverViewLineData> loadMachineConnection(String appName, String host) {

        String[] keys = {"Machine.Connec.CLOSE-WAIT", "Machine.Connec.ESTAB", "Machine.Connec.LISTEN", "Machine.Connec.TIME-WAIT", "Machine.Connec.TOTLE"};
        String[] alias = {"CLOSE-WAIT", "ESTAB", "LISTEN", "TIME-WAIT", "TOTAL"};

        return load(appName, host, keys, alias);
    }

    @Override
    public List<OverViewLineData> loadMachineFreePhysicalMemory(String appName, String host) {

        String[] keys = {"Cpu.FreePhysicalMemorySize", "Cpu.FreeSwapSpaceSize"};
        String[] alias = {"FreePhysical", "FreeSwap"};

        return load(appName, host, keys, alias);
    }

    @Override
    public List<OverViewLineData> loadProcessJVMThreads(String appName, String host) {

        String[] keys = {"Thread.Total", "Thread.blocked", "Thread.runnable", "Thread.terminated", "Thread.timed_waiting", "Thread.waiting"};
        String[] alias = {"TOTAL", "BLOCKED", "RUNNABLE", "TERMINATED", "TIMED_WAITING", "WAITING"};

        return load(appName, host, keys, alias);
    }

    @Override
    public List<OverViewLineData> loadProcessJVMGC(String appName, String host) {

        String[] keys = {"YoungGc.count"};
        String[] alias = {"YoungGc"};

        return load(appName, host, keys, alias);
    }

    @Override
    public List<OverViewLineData> loadProcessConnection(String appName, String host) {

        String[] keys = {"Connec.CLOSE-WAIT", "Connec.ESTAB", "Connec.LISTEN", "Connec.TIME-WAIT", "Connec.TOTLE"};
        String[] alias = {"CLOSE-WAIT", "ESTAB", "LISTEN", "TIME-WAIT", "TOTAL"};
        return load(appName, host, keys, alias);
    }

    @Override
    public List<OverViewLineData> loadProcessJVMHeapMemory(String appName, String host) {

        String[] keys = {"OldGen.max", "OldGen.used", "EdenSpace.max", "EdenSpace.used"};
        String[] alias = {"OldGen.max", "OldGen.used", "Eden.max", "Eden.used"};

        return load(appName, host, keys, alias);
    }

    @Override
    public List<OverViewLineData> loadProcessJVMNonHeapMemory(String appName, String host) {

        String[] keys = {"NonHeapMemoryUsage.max", "NonHeapMemoryUsage.used"};
        String[] alias = {"max", "used"};

        return load(appName, host, keys, alias);
    }

    @Override
    public Map<String, LinkedHashMap<String, Object>> loadAppVariables(String appName, String host) {

        // select * from sauron_sys order by time desc limit 1s

        if (appName == null || appName.length() == 0) {
            return Collections.emptyMap();
        }

        StringBuilder sb = new StringBuilder();

        sb.append("select *  from sauron_sys   where appName = '" + appName + "' ");

        if (host != null && host.length() > 0) {
            sb.append(" and hostName = '").append(host).append("' ");
        }

        sb.append("   order by time desc limit 1 ");

        List<List<String>> sys = getSys(sb.toString(), InfluxdbUtils.dbName_sys);

        Map<String, LinkedHashMap<String, Object>> result = new HashMap<>();

        if (sys.size() > 0) {

            for (List<String> list : sys) {

                LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
                hashMap.put("pid", list.get(17));
                hashMap.put("appName", list.get(2));
                hashMap.put("hostName", list.get(6));

                hashMap.put("mainClass", list.get(13));
                hashMap.put("jvmArgs", list.get(12));
                hashMap.put("agentPath", list.get(1));

                hashMap.put("file.encoding", list.get(5));
                hashMap.put("java.endorsed.dirs", list.get(7));
                hashMap.put("java.home", list.get(8));
                hashMap.put("java.runtime.name", list.get(9));
                hashMap.put("java.runtime.version", list.get(10));
                hashMap.put("java.vm.name", list.get(11));

                hashMap.put("catalina.base", list.get(3));
                hashMap.put("catalina.home", list.get(4));

                hashMap.put("os.arch", list.get(14));
                hashMap.put("os.name", list.get(15));
                hashMap.put("os.version", list.get(16));
                hashMap.put("sun.boot.library.path", list.get(18));
                hashMap.put("sun.desktop", list.get(19));
                hashMap.put("sun.java.command", list.get(20));
                hashMap.put("user.name", list.get(21));
                hashMap.put("userDir", list.get(22));

                String pid = list.get(17);

                result.put(pid, hashMap);
            }

        }

        return result;
    }

    private static Map<String, List<List<String>>> getPoint(String sql, String dbName) {

        Map<String, List<List<String>>> hashMap = new HashMap<>();

        QueryResult queryDetailResult = InfluxdbUtils.getInfluxDB().query(new Query(sql, dbName));

        if (!queryDetailResult.hasError() && queryDetailResult.getResults() != null) {

            for (QueryResult.Result result : queryDetailResult.getResults()) {

                if (result != null && !result.hasError() && result.getSeries() != null) {

                    for (QueryResult.Series series : result.getSeries()) {

                        Map<String, String> tags = series.getTags();

                        String hostName = tags.get("hostName");

                        if (series != null && series.getValues() != null) {

                            List<List<String>> tagValuesList = new ArrayList<>();

                            for (List<Object> valusList : series.getValues()) {

                                if (valusList != null) {
                                    List<String> tagValues = new ArrayList<>();

                                    for (Object tagValue : valusList) {

                                        if (tagValue != null) {

                                            tagValues.add(tagValue.toString());
                                        }
                                    }
                                    tagValuesList.add(tagValues);
                                }
                            }
                            hashMap.put(hostName, tagValuesList);
                        }

                    }
                }
            }
        }
        return hashMap;
    }

    private static Map<String, List<List<String>>> getLine(String sql, String dbName) {

        Map<String, List<List<String>>> hashMap = new HashMap<>();

        QueryResult queryDetailResult = InfluxdbUtils.getInfluxDB().query(new Query(sql, dbName));

        if (!queryDetailResult.hasError() && queryDetailResult.getResults() != null) {

            for (QueryResult.Result result : queryDetailResult.getResults()) {

                if (result != null && !result.hasError() && result.getSeries() != null) {

                    for (QueryResult.Series series : result.getSeries()) {

                        Map<String, String> tags = series.getTags();

                        String hostName = tags.get("pid");

                        if (series != null && series.getValues() != null) {

                            List<List<String>> tagValuesList = new ArrayList<>();

                            for (List<Object> valusList : series.getValues()) {

                                if (valusList != null) {
                                    List<String> tagValues = new ArrayList<>();

                                    for (Object tagValue : valusList) {

                                        if (tagValue != null) {

                                            tagValues.add(tagValue.toString());
                                        }
                                    }
                                    tagValuesList.add(tagValues);
                                }
                            }
                            hashMap.put(hostName, tagValuesList);
                        }

                    }
                }
            }
        }
        return hashMap;
    }

    private static List<List<String>> getSys(String sql, String dbName) {

        List<List<String>> tagValuesList = new ArrayList<>();

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
                                    tagValuesList.add(tagValues);
                                }
                            }
                        }

                    }
                }
            }
        }
        return tagValuesList;
    }

    public List<OverViewLineData> load(String appName, String host, String[] keys, String[] alias) {

        if (appName == null || appName.length() == 0) {
            return Collections.emptyList();
        }

        StringBuilder sb = new StringBuilder("select ");

        for (int i = 0; i < keys.length; i++) {
            sb.append("\"").append(keys[i]).append("\"");
            if (i < keys.length - 1) {
                sb.append(", ");
            }
        }

        sb.append("  from sauron_jvm   where time > (now() - 30m) and appName = '" + appName + "' ");

        if (host != null && host.length() > 0) {
            sb.append(" and hostName = '").append(host).append("' ");
        }

        sb.append(" group by pid ");

        sb.append(" order by time asc limit 30 ");

        Map<String, List<List<String>>> line = getLine(sb.toString(), InfluxdbUtils.dbName_jvm);

        List<OverViewLineData> overViewLineDatas = new ArrayList<>();

        for (String pid : line.keySet()) {

            OverViewLineData overViewLineData = new OverViewLineData();

            overViewLineData.setpID(pid);

//            overViewLineData.setLineNames(keys);
            overViewLineData.setLineNames(alias);

            LinkedHashMap<Long, List<Number>> hashMap = new LinkedHashMap<>();

            List<List<String>> list = line.get(pid);

            for (List<String> linePiont : list) {

                if (linePiont.size() != keys.length + 1) {
                    continue;
                }

                String time = linePiont.get(0);

                time = time.replace("T", " ").replace("Z", "");

                Long parseTime = DateUtils.parseDate(time, "yyyy-MM-dd HH:mm:ss").getTime() + 8 * 60 * 60 * 1000;

                List<Number> arrayList = new ArrayList<>();

                for (int i = 1; i < linePiont.size(); i++) {

                    Double double2 = new Double(linePiont.get(i));
                    arrayList.add(double2);
                }

                hashMap.put(parseTime, arrayList);

            }

            overViewLineData.setLineDatas(hashMap);

            overViewLineDatas.add(overViewLineData);

        }

        return overViewLineDatas;
    }
}
