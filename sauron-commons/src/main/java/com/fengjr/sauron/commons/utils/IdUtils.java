package com.fengjr.sauron.commons.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by jian.zhang on 2016/11/2.
 */
public class IdUtils {

    /**
     * 上次生成时间
     */
    private long lastTimestamp = -1L;

    /**
     * 序列
     */
    private long sequence = 0L;

    /**
     * 默认序列14位
     */
    private long sequenceBits = 14;

    private long sequenceMask = -1 ^ (-1 << sequenceBits);

    /**
     * mac地址
     */
    private static String mac;

    private static IdUtils idUtils;

    private static long initialTime = 1479112463872l;


    static {
        mac = getLocalMac();
    }

    public static IdUtils getInstance(){
        if(idUtils == null){
            synchronized (IdUtils.class){
                if(idUtils == null){
                    idUtils = new IdUtils();
                }
            }
            return idUtils;
        }else {
            return idUtils;
        }
    }

    public synchronized String nextId(){
        long timestamp = timeNow();
        if(timestamp < lastTimestamp){
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if(timestamp == lastTimestamp){
            sequence = (sequence + 1) & sequenceMask;
            if(sequence == 0){
                timestamp = nextMillis();
                sequence = 0L;
            }
        }else{
            sequence = 0L;
        }

        lastTimestamp = timestamp;
        return timestamp + "^" + String.valueOf(sequence) + mac;
    }


    private long nextMillis(){
        long timestamp = timeNow();
        if(timestamp <= lastTimestamp){
            timestamp = timeNow();
        }
        return timestamp;
    }

    private long timeNow(){
        return System.currentTimeMillis();
    }

    private static String getLocalMac(){
        String result;
        try {
            StringBuffer sb = null;
            byte [] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
            sb = new StringBuffer();
            for(int i=0; i<mac.length; i++) {
                int temp = mac[i]&0xff;
                String str = Integer.toHexString(temp);
                if(str.length()==1){
                    sb.append("0"+str);
                }else{
                    sb.append(str);
                }
            }
            String aa = null;
            aa.toString();
            result = sb.toString();
        } catch (SocketException e) {
//            e.printStackTrace();
            result = defaultMac();
        } catch (UnknownHostException e) {
//            e.printStackTrace();
            result = defaultMac();
        } catch (Exception e){
//            e.printStackTrace();
            result = defaultMac();
        }
        return result;
    }


    /**
     * 获取当前程序进程号
     * @return
     */
    private static final String getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getName().split("@")[0];
    }

    private static long getRandom(long largest){
        return ThreadLocalRandom.current().nextLong(largest);
    }

    public static String defaultMac(){
        long lag = System.currentTimeMillis()-initialTime;
//        String lag = getProcessID();
        String lagStr = String.valueOf(lag);
        return createString(lagStr);

    }
    private static  String createString(String str){
        int size = 12 - str.length();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(9);
        for(int loop = 1; loop<size; loop++){
            stringBuffer.append(0);
        }
        long rd = getRandom(Long.valueOf(stringBuffer.toString()));
        String mac = str + rd;
        if(mac.length() < 12){
            int ph = 12 - mac.length();
            for(int loop$ = 0; loop$ < ph;loop$++){
                mac += "0";
            }
        }
        return mac;
    }

}
