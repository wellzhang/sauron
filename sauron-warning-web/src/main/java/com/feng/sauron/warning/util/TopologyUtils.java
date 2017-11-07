package com.feng.sauron.warning.util;

import com.feng.sauron.warning.web.vo.TopologyData;
import com.feng.sauron.warning.web.vo.TopologyLink;
import com.feng.sauron.warning.web.vo.TopologyNode;
import com.feng.sauron.warning.web.vo.TopologyNodeURL;

import org.apache.commons.lang.math.RandomUtils;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wei.wang@fengjr.com
 * @version 2015年11月16日 下午6:16:07
 */
public class TopologyUtils {

	private static String dbName = InfluxdbUtils.dbName;

	public static void main(String[] args) {

		Date endTime = new Date();

		Date startTime = new Date(endTime.getTime() - (1000 * 60 * 30));

		TopologyData topologyData = get("sauron", null, startTime, endTime);
		String longJSon = JsonUtils.toLongJSon(topologyData);
		System.out.println(longJSon);
	}

	public static TopologyData get(String appName, String host, Date startTime, Date endTime) {

		if (appName == null || appName.length() == 0) {
			return null;
		}

		String start_Time = DateUtils.dateFormat(startTime.getTime() - 1000 * 3600 * 8);
		String end_Time = DateUtils.dateFormat(endTime.getTime() - 1000 * 3600 * 8);

		StringBuilder sb = new StringBuilder();

		String tagType = "source";

		sb.append("SHOW TAG VALUES FROM \"sauron\" WITH KEY = \"" + tagType + "\" where appName = '" + appName + "'");

		if (host != null && host.length() > 0) {
			sb.append(" and host = '").append(host).append("' ");
		}

		List<String> tagTypeValues = getTagValue(sb.toString(), dbName, tagType);

		TopologyData topologyData2 = new TopologyData();

		topologyData2.setStartTime(System.currentTimeMillis() - 1800000);
		topologyData2.setEndTime(System.currentTimeMillis());

		topologyData2.setLinkDataArray(new ArrayList<TopologyLink>());
		topologyData2.setNodeDataArray(new ArrayList<TopologyNode>());

		for (String string : tagTypeValues) {

			boolean flag = false;
			if (appName.equals(string)) {// 相等说明自己调用自己或者别人 ，不想等 说明别人掉自己
				flag = true;
			}

			TopologyData topologyData = get(appName, string, flag, host, start_Time, end_Time);

			topologyData2.getLinkDataArray().addAll(topologyData.getLinkDataArray());

			topologyData2.getNodeDataArray().addAll(topologyData.getNodeDataArray());

		}

		return topologyData2;
	}

	private static TopologyData get(String appName, String source, boolean flag, String host, String start_Time, String end_Time) {

		StringBuilder sb = new StringBuilder();

		String tagType = "type";

		sb.append("SHOW TAG VALUES FROM \"sauron\" WITH KEY = \"" + tagType + "\" where appName = '" + appName + "' and source = '" + source + "' ");

		if (host != null && host.length() > 0) {
			sb.append(" and host = '").append(host).append("' ");
		}

		List<String> tagTypeValues = getTagValue(sb.toString(), dbName, tagType);

		tagTypeValues.remove("Mybatis");
		tagTypeValues.remove("Controller");

		String tagDetail = "detail";

		List<TopologyLink> linkDataArray = new ArrayList<>();

		List<TopologyNode> nodeDataArray = new ArrayList<>();

		for (String type : tagTypeValues) {

			StringBuilder sb4 = new StringBuilder();

			sb4.append("select sum(count) as total  , sum(exception) as exception , mean(tp90) as tp90  from sauron   where  appName = '" + appName + "'");

			sb4.append(" and source = '").append(source).append("' ");
			sb4.append(" and type = '").append(type).append("' ");
			sb4.append(" and time >= '").append(start_Time).append("'");
			sb4.append(" and time <= '").append(end_Time).append("'");

			if (host != null && host.length() > 0) {
				sb4.append(" and host = '").append(host).append("' ");
			}

			List<String> value_ = getValue(sb4.toString(), dbName);

			String total_ = "0";
			String exception_ = "0";
			String tp90_ = "0";

			if (value_.size() >= 4) {

				total_ = value_.get(1);
				exception_ = value_.get(2);
				tp90_ = value_.get(3);
			}

			TopologyLink link = new TopologyLink();

			int parseIntException = new Double(exception_).intValue();
			int parseIntTotal_ = new Double(total_).intValue();
			int parseIntTp90 = new Double(tp90_).intValue();

			link.setFailCount(parseIntException);

			link.setStatus(calculateStatus(parseIntException, parseIntTotal_, parseIntTp90));

			// link.setStatus(parseIntException / (parseIntTotal_ == 0 ? 1 : parseIntTotal_) * 100 + "%");
			link.setTimeCost(parseIntTp90);
			link.setTotalCount(parseIntTotal_);

			if (flag) {
				link.setFrom(appName);
				link.setTo(type);
			} else {
				link.setFrom(type);
				link.setTo(appName);
			}

			link.setType(type);

			StringBuilder sb5 = new StringBuilder();

			sb5.append("select top(tp100,1) as max  , traceId  from sauron   where  appName = '" + appName + "'");

			sb5.append(" and source = '").append(source).append("' ");
			sb5.append(" and type = '").append(type).append("' ");
			sb5.append(" and time >= '").append(start_Time).append("'");
			sb5.append(" and time <= '").append(end_Time).append("'");

			if (host != null && host.length() > 0) {
				sb5.append(" and host = '").append(host).append("' ");
			}

			List<String> value_5 = getValue(sb5.toString(), dbName);

			if (value_5.size() >= 3) {

				String traceid = value_5.get(2);

				link.setTraceID(traceid);

			}

			linkDataArray.add(link);

			TopologyNode node = new TopologyNode();

			node.setCategory(type);
			node.setKey(type);
			node.setNodeName(type);

			StringBuilder sb2 = new StringBuilder();

			sb2.append("SHOW TAG VALUES FROM \"sauron\" WITH KEY = \"" + tagDetail + "\" where appName = '" + appName + "' and source = '" + source + "' and type = '" + type + "'");
			sb2.append(" ");

			List<String> tagDeatilValues = getTagValue(sb2.toString(), dbName, tagDetail);

			List<TopologyNodeURL> urlList = new ArrayList<>();

			for (String detail : tagDeatilValues) {

				TopologyNodeURL url = new TopologyNodeURL();

				url.setAppName(source);
				url.setUrl(detail);

				StringBuilder sb3 = new StringBuilder();

				sb3.append("select sum(count) as total  , sum(exception) as exception , mean(tp90) as tp90  from sauron   where  appName = '" + appName + "' and source = '" + source
						+ "' and type = '" + type + "' and detail = \"" + detail + "\" ");
				sb3.append(" and time >= '").append(start_Time).append("' and time <= '").append(end_Time).append("'");

				List<String> value = getValue(sb3.toString(), dbName);

				if (value.size() >= 2) {
					String total = value.get(1);
					int parseIntTotal = new Double(total).intValue();
					url.setTotalCount(parseIntTotal);

					String total_exception = value.get(2);
					int parseIntTotal_exception = new Double(total_exception).intValue();
					url.setErrorCount(parseIntTotal_exception);

					String tp90__ = value.get(3);
					int tp90_int = new Double(tp90__).intValue();
					url.setTimeCost(tp90_int);

				}

				StringBuilder sb1 = new StringBuilder();

				sb1.append("select top(tp100,1) as max , traceId  from sauron   where  appName = '" + appName + "' and source = '" + source + "' and type = '" + type + "' and detail = \"" + detail
						+ "\" ");
				sb1.append(" and time >= '").append(start_Time).append("' and time <= '").append(end_Time).append("'");

				List<String> value_4 = getValue(sb1.toString(), dbName);

				if (value_4.size() >= 3) {
					String traceid = value_4.get(2);
					url.setTraceID(traceid);
				}

				urlList.add(url);

			}

			node.setUrlList(urlList);

			nodeDataArray.add(node);

		}

		TopologyData topologyData = new TopologyData();

		topologyData.setLinkDataArray(linkDataArray);
		topologyData.setNodeDataArray(nodeDataArray);

		return topologyData;

	}

	/**
	 * 计算调用链状态 暂定规则：
	 * <p>
	 * errorCount >0 ? 1:0 errorCount/totalCount in (0%,10%)=1 [10%,50%)=2 [50%~,)=3 tp90 in (0,100]=0 (100,500]=1 (500,~)=2
	 * 
	 * @param errCount
	 *            错误数
	 * @param totalCount
	 *            总调用次数
	 * @param tp90
	 *            top90耗时(ms)
	 * @return
	 */
	public static String calculateStatus(int errCount, int totalCount, int tp90) {
		int score = 0;

		if (errCount > 0) {
			score += 1;
		}

		if (totalCount > 0) {
			double errorRate = errCount / totalCount * 1.0;
			if (errorRate > 0 && errorRate < 0.1) {
				score += 1;
			} else if (errorRate >= 0.1 && errorRate < 0.5) {
				score += 2;
			} else if (errorRate >= 0.5) {
				score += 3;
			}
		}

		if (tp90 > 100 && tp90 <= 500) {
			score += 1;
		} else if (tp90 > 500) {
			score += 2;
		}

		return score % 3 + "";
	}

	private static List<String> getTagValue(String sql, String dbName, String tag) {

		List<String> tagDeatilValues = new ArrayList<String>();

		QueryResult queryDetailResult = InfluxdbUtils.getInfluxDB().query(new Query(sql, dbName));

		if (!queryDetailResult.hasError() && queryDetailResult.getResults() != null) {
			for (QueryResult.Result result : queryDetailResult.getResults()) {
				if (result != null && !result.hasError() && result.getSeries() != null) {
					for (QueryResult.Series series : result.getSeries()) {
						if (series != null && series.getValues() != null) {
							for (List<Object> tagValueList : series.getValues()) {
								if (tagValueList != null) {
									for (Object tagValue : tagValueList) {
										if (tagValue != null && !(tag.equals(tagValue))) {
											tagDeatilValues.add(String.valueOf(tagValue));
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return tagDeatilValues;
	}

	private static List<String> getValue(String sql, String dbName) {

		List<String> tagDeatilValues = new ArrayList<String>();

		try {
			QueryResult queryDetailResult = InfluxdbUtils.getInfluxDB().query(new Query(sql, dbName));

			if (!queryDetailResult.hasError() && queryDetailResult.getResults() != null) {
				for (QueryResult.Result result : queryDetailResult.getResults()) {
					if (result != null && !result.hasError() && result.getSeries() != null) {
						for (QueryResult.Series series : result.getSeries()) {
							if (series != null && series.getValues() != null) {
								for (List<Object> valusList : series.getValues()) {
									if (valusList != null) {
										for (Object tagValue : valusList) {
											if (tagValue != null) {
												tagDeatilValues.add(tagValue.toString());
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tagDeatilValues;
	}

}
