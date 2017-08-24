package com.feng.sauron.warning.util;

import com.feng.sauron.warning.service.CallstackItem;
import com.fengjr.sauron.dao.model.MetricsOriData;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lianbin.wang on 2016/11/17.
 */
public class MetricsOriDataUtils {

    public static final Comparator<MetricsOriData> metricsComparator = new Comparator<MetricsOriData>() {
        @Override
        public int compare(MetricsOriData o1, MetricsOriData o2) {
            return SpanIdComparator.INSTANCE.compare(o1.getSpanId(), o2.getSpanId());
        }
    };

    public static MetricsOriData findMaxSpanId(List<MetricsOriData> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        sortBySpanId(list);
        return list.get(0);
    }

    public static void sortBySpanId(final List<MetricsOriData> list) {
        Collections.sort(list, metricsComparator);
    }


    public static CallstackItem toCallstack(MetricsOriData data) {
        CallstackItem item = new CallstackItem();

        BeanUtils.copyProperties(data, item);

        String[] span = data.getSpanId().split("\\.");
        item.setIndent(span.length >= 1 ? span.length - 1 : 0);

        return item;
    }
}
