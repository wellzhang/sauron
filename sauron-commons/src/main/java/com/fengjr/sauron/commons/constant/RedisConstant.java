package com.fengjr.sauron.commons.constant;

/**
 * Created by bingquan.an@fengjr.com on 2015/10/30.
 */
public class RedisConstant {

	public final static int EXPIRED_HASH_FREQUENCY_TARGET = 60 * 10;
    public final static int EXPIRED_HASH_FREQUENCY_TARGET_MINUTE = 60 * 60 * 24;
    public final static int EXPIRED_HASH_FREQUENCY_TARGET_HOURS = 60 * 60 * 24 * 3;
    public final static int EXPIRED_HASH_TP_TARGET_MINUTE_METRICS = 60 * 60 * 24;
    public final static int EXPIRED_HASH_TP_TARGET_HOURS_METRICS = 60 * 60 * 24 * 3;

    public final static String HASH_FREQUENCY_TARGET_MINUTE = "key_frequency_%s_%s";
    public final static String HASH_FREQUENCY_TARGET_HOURS = "key_frequency_hours_%s_%s";
    public final static String ZSET_TP_TARGET_MINUTE = "key_tp_%s_%s";
    public final static String HASH_TP_TARGET_MINUTE_METRICS = "key_tp_metrics_%s_%s";
    public final static String ZSET_TP_TARGET_HOURS = "key_tp_sampleing_%s_%s";
    public final static String HASH_TP_TARGET_HOURS_METRICS = "key_tp_hours_%s_%s";


//  public final static String HASH_KEY_PULL_COUNT = "pull_count";
    public final static String HASH_KEY_SAMPLING_COUNT = "sampling_count";
    public final static String HASH_KEY_SAMPLING_SUMVAULE = "sampling_sumvalue";
    public final static String HASH_KEY_SAMPLING_AVGVAULE = "sampling_avgvalue";
//  public final static String HASH_KEY_SAMPLING_FLAG = "sampling_flag";

//  public final static int THRESHOLD_MAX_REQUEST_COUNT = 12; //请求次数
    public final static int THRESHOLD_SAMPLING_COUNT = 10;    //最低采样样本数
    public final static String SET_METHOD_REG = "key_method_reg";    //method注册缓存
    public final static String SET_APPDATA_REG = "key_appdata_reg";    //method注册缓存
//  public final static String ARCHIVED_METHOD_OFFSET_MINUTE = "archived_offset_minute_%s_%s";
}
