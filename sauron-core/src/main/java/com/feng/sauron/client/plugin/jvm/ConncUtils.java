package com.feng.sauron.client.plugin.jvm;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.feng.sauron.utils.SauronUtils;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年11月9日 下午3:02:01
 */
public class ConncUtils {

    private static boolean isWindows = false;

    private static String encode = "utf-8";

    static {

        String property = System.getProperties().getProperty("os.name", "linux");

        if (property.indexOf("Windows") != -1) {
            isWindows = true;
        }

    }

    public static Map<String, Integer> getAll() {

        if (isWindows) {

            Map<String, Integer> hashMap = new HashMap<>();

            hashMap.put("Connec.TOTLE", 0);
            hashMap.put("Connec.SYN-SENT", 0);
            hashMap.put("Connec.SYN-RECV", 0);
            hashMap.put("Connec.ESTAB", 0);
            hashMap.put("Connec.LISTEN", 0);
            hashMap.put("Connec.TIME-WAIT", 0);
            hashMap.put("Connec.CLOSE-WAIT", 0);
            hashMap.put("Connec.CLOSING", 0);

            hashMap.put("Machine.Connec.TOTLE", 0);
            hashMap.put("Machine.Connec.SYN-SENT", 0);
            hashMap.put("Machine.Connec.SYN-RECV", 0);
            hashMap.put("Machine.Connec.ESTAB", 0);
            hashMap.put("Machine.Connec.LISTEN", 0);
            hashMap.put("Machine.Connec.TIME-WAIT", 0);
            hashMap.put("Machine.Connec.CLOSE-WAIT", 0);
            hashMap.put("Machine.Connec.CLOSING", 0);

            return hashMap;
        }

        Map<String, Integer> byPid = getByPid(SauronUtils.getPid());

        Map<String, Integer> machine = getMachine();

        byPid.putAll(machine);

        return byPid;

    }

    // 获取当前进程
    private static Map<String, Integer> getByPid(String pid) {

        Map<String, Integer> hashMap = new HashMap<>();

        hashMap.put("Connec.TOTLE", 0);
        hashMap.put("Connec.SYN-SENT", 0);
        hashMap.put("Connec.SYN-RECV", 0);
        hashMap.put("Connec.ESTAB", 0);
        hashMap.put("Connec.LISTEN", 0);
        hashMap.put("Connec.TIME-WAIT", 0);
        hashMap.put("Connec.CLOSE-WAIT", 0);
        hashMap.put("Connec.CLOSING", 0);

        try {
            Map<String, Map<String, String>> detailInfo = start(pid);

            for (String ipPort : detailInfo.keySet()) {

                Map<String, String> map = detailInfo.get(ipPort);

                for (String ss : map.keySet()) {

                    String realSs = "Connec." + ss;

                    if (hashMap.containsKey(realSs)) {

                        int parseInt = Integer.parseInt(map.get(ss));

                        Integer integer = hashMap.get(realSs);

                        hashMap.put(realSs, integer + parseInt);

                        Integer integer2 = hashMap.get("Connec.TOTLE");

                        hashMap.put("Connec.TOTLE", integer2 + parseInt);

                    }
                }
            }
        } catch (Exception e) {
        }

        return hashMap;

    }

    // 获取当前机器连接状态
    private static Map<String, Integer> getMachine() {

        Map<String, Integer> hashMap = new HashMap<>();

        hashMap.put("Machine.Connec.TOTLE", 0);
        hashMap.put("Machine.Connec.SYN-SENT", 0);
        hashMap.put("Machine.Connec.SYN-RECV", 0);
        hashMap.put("Machine.Connec.ESTAB", 0);
        hashMap.put("Machine.Connec.LISTEN", 0);
        hashMap.put("Machine.Connec.TIME-WAIT", 0);
        hashMap.put("Machine.Connec.CLOSE-WAIT", 0);
        hashMap.put("Machine.Connec.CLOSING", 0);

        try {
            Map<String, String> map = get(null);

            for (String ss : map.keySet()) {

                String realSs = "Machine.Connec." + ss;

                if (hashMap.containsKey(realSs)) {

                    int parseInt = Integer.parseInt(map.get(ss));

                    Integer integer = hashMap.get(realSs);

                    hashMap.put(realSs, integer + parseInt);

                    Integer integer2 = hashMap.get("Machine.Connec.TOTLE");

                    hashMap.put("Machine.Connec.TOTLE", integer2 + parseInt);

                }
            }
        } catch (Exception e) {

        }

        return hashMap;

    }

    private static Map<String, Map<String, String>> start(String pid) {

        Map<String, Map<String, String>> detailInfo = null;

        try {

            detailInfo = getDetailInfo(getTcp(pid));

            detailInfo.putAll(getDetailInfo(getListenTcp(pid)));

        } catch (Exception e) {
        }

        return detailInfo;
    }

    private static Map<String, Map<String, String>> getDetailInfo(String[] ipPorts) {

        Map<String, Map<String, String>> resultMap = new HashMap<>();

        for (String string : ipPorts) {

            resultMap.put(string, get(string));
        }

        return resultMap;
    }

    private static Map<String, String> get(String ipPort) {

        Map<String, String> hashMap = new HashMap<>();

        try {

            String cmd = "ss -a | awk '{++S[$1]} END {for(a in S) print a, S[a]}'";

            if (ipPort != null && ipPort.length() > 0) {

                ipPort = ipPort.replace("*", "");

                cmd = "ss -a  |grep '" + ipPort.trim() + "'| awk '{++S[$1]} END {for(a in S) print a, S[a]}'";
            }

            byte[] b = execute(cmd);

            if (b != null && b.length > 0) {

                String result = new String(b, encode);

                String[] split = result.split("\n");

                for (String string2 : split) {

                    String[] split2 = string2.split(" ");

                    if (split2 != null && split2.length == 2) {

                        hashMap.put(split2[0], split2[1]);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hashMap;
    }

    private static String[] getTcp(String pid) {

        String[] ipPorts = null;

        try {

            // ss -p |grep ',10757,' | awk '{print $5}'

            String cmd = "ss -p |grep '," + pid + ",'|awk '{print $5}'";

            byte[] b = execute(cmd);

            if (b != null && b.length > 0) {

                String result = new String(b, encode);

                ipPorts = result.split("\n");

            }
        } catch (Exception e) {

        }
        return ipPorts;
    }

    private static String[] getListenTcp(String pid) {

        String[] ipPorts = null;

        try {

            // ss -lp |grep ',10757,' | awk '{print $4}'

            String cmd = "ss -lp |grep '," + pid + ",'|awk '{print $4}'";

            byte[] b = execute(cmd);

            if (b != null && b.length > 0) {

                String result = new String(b, encode);

                ipPorts = result.split("\n");
            }
        } catch (Exception e) {

        }

        return ipPorts;

    }

    private static byte[] execute(String cmd) {

        byte[] b = null;

        InputStream inputStream = null;

        try {

            String[] cmdA = {"/bin/sh", "-c", cmd};

            Process exec = Runtime.getRuntime().exec(cmdA);

            exec.waitFor();

            inputStream = exec.getInputStream();

            b = new byte[inputStream.available()];

            inputStream.read(b);

        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return b;
    }

}
