package com.fengjr.sauron.dao.hbase;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by xubiao.fan@fengjr.com on 2016/11/1.
 */
public class HbaseTables {

    public static int APP_NAME_MAX_LEN = 24;  //MAX app name

    public final static String METRICS_ORI_DATA = "sauron_metrics_ori_data"; //METRICS_ORI_DATA taible

    public final static byte[] METRICS_ORI_DATA_CF_TRACE = Bytes.toBytes("T"); //METRICS_ORI_DATA taible CF

    public final static String METRICS_CODE_BULK_DATA = "sauron_metrics_code_bulk_data"; //code bulk tables

    public final static byte[] METRICS_CODE_BULK_DATA_CF = Bytes.toBytes("CT");  //code bulk column family

    public final static byte[] METRICS_CODE_BULK_DATA_CF_QUALIFIER = Bytes.toBytes("DA");


    public final static String METRICS_CODE_BULK_ALARM = "sauron_metrics_code_bulk_alarm"; //metrics_code_bulk_alarm taible

    public final static byte[] METRICS_CODE_BULK_ALARM_CF = Bytes.toBytes("CT");

    public final static byte[] METRICS_CODE_BULK_ALARM_CF_QUALIFIER = Bytes.toBytes("DA");


    public final static String TRACE_APP_SHIP = "sauron_trace_app_ship";

    public final static byte[] TRACE_APP_SHIP_CF = Bytes.toBytes("TA");
















}
