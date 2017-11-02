package com.feng.sauron.warning.service;

import java.util.ArrayList;
import java.util.List;

import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.stereotype.Service;

import com.feng.sauron.warning.util.InfluxdbUtils;

/**
 * Created by Liuyb on 2015/12/21.
 */
@Service
public class InfluxdbService {

    public List<String> getTagValues(String dbName, String MeasurementName, String key) {
        List<String> tagValues = new ArrayList<String>();
        Query query = new Query("SHOW TAG VALUES FROM " + MeasurementName + " WITH KEY = \"" + key + "\"", dbName);
        QueryResult queryResult = InfluxdbUtils.getInfluxDB().query(query);
        if (!queryResult.hasError() && queryResult.getResults() != null) {
            for (QueryResult.Result result : queryResult.getResults()) {
                if (result != null && !result.hasError() && result.getSeries() != null) {
                    for (QueryResult.Series series : result.getSeries()) {
                        if (series != null && series.getValues() != null) {
                            for (List<Object> tagValueList : series.getValues()) {
                                if (tagValueList != null) {
                                    for (Object tagValue : tagValueList) {
                                        if (tagValue != null) {
                                            tagValues.add((String) tagValue);
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        return tagValues;
    }

    public List<String> getTagValuesByApp(String dbName, String MeasurementName, String appName, String key) {
        List<String> tagValues = new ArrayList<String>();
        Query query = new Query("SHOW TAG VALUES FROM " + MeasurementName + " WITH KEY = \"" + key + "\" WHERE appName = \'" + appName + "\'", dbName);
        QueryResult queryResult = InfluxdbUtils.getInfluxDB().query(query);
        if (!queryResult.hasError()) {
            for (QueryResult.Result result : queryResult.getResults()) {
                if (result != null && !result.hasError() && result.getSeries() != null) {
                    for (QueryResult.Series series : result.getSeries()) {
                        if (series != null && series.getValues() != null) {
                            for (List<Object> tagValueList : series.getValues()) {
                                if (tagValueList != null) {
                                    for (Object tagValue : tagValueList) {
                                        if (tagValue != null && ! (key.equals(tagValue))) {
                                            tagValues.add((String) (tagValue));
                                        }
                                        }
                                    }

                            }
                            }
                    }
                }
            }
        }
        return tagValues;
    }

    public QueryResult query(Query query) {
		return InfluxdbUtils.getInfluxDB().query(query);
	}
	
}