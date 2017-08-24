package com.feng.sauron.warning.util;

import java.util.Comparator;

/**
 * Created by lianbin.wang on 2016/11/16.
 * <p>
 * 排序：
 * 错误排序结果：
 * 0
 * 0.1
 * 0.1.1
 * 0.12
 * 0.12.1
 * 0.2
 * 0.2.1
 * 正确排序结果：
 * 0
 * 0.1
 * 0.1.1
 * 0.2
 * 0.2.1
 * 0.12
 * 0.12.1
 */
public enum SpanIdComparator implements Comparator<String> {

    INSTANCE;

    @Override
    public int compare(String s1, String s2) {
        String v1[] = s1.split("\\.");
        String v2[] = s2.split("\\.");

        int len1 = v1.length;
        int len2 = v2.length;
        int lim = Math.min(len1, len2);

        int k = 0;
        while (k < lim) {
            String c1 = v1[k];
            String c2 = v2[k];

            if (!c1.equals(c2)) {
                return Integer.valueOf(c1).compareTo(Integer.valueOf(c2));
            }

            k++;
        }

        return len1 - len2;
    }
}
