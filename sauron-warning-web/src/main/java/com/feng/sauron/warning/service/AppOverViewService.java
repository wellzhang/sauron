package com.feng.sauron.warning.service;

import com.feng.sauron.warning.web.vo.OverViewLineData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lianbin.wang on 2016/11/24.
 */
public interface AppOverViewService {

    Map<String, Object> loadOverView(String appName);

    //机器CPU使用率
    List<OverViewLineData> loadMachineCPU(String appName, String host);

    //机器内存占用率
    List<OverViewLineData> loadMachineMemory(String appName, String host);

    //机器连接数
    List<OverViewLineData> loadMachineConnection(String appName, String host);

    //机器剩余物理内存
    List<OverViewLineData> loadMachineFreePhysicalMemory(String appName, String host);

    //进程JVM线程占用情况
    List<OverViewLineData> loadProcessJVMThreads(String appName, String host);

    //进程JVMGC情况
    List<OverViewLineData> loadProcessJVMGC(String appName, String host);

    //进程占用连接情况
    List<OverViewLineData> loadProcessConnection(String appName, String host);

    //进程JVM堆内存使用量
    List<OverViewLineData> loadProcessJVMHeapMemory(String appName, String host);

    //进程JVM 非堆内存使用量
    List<OverViewLineData> loadProcessJVMNonHeapMemory(String appName, String host);

    Map<String, LinkedHashMap<String, Object>> loadAppVariables(String appName, String host);
}
