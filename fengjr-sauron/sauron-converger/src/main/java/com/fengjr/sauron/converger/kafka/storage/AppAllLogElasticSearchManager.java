package com.fengjr.sauron.converger.kafka.storage;

import java.net.InetAddress;
import java.util.Properties;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppAllLogElasticSearchManager {

	private String ELASTIC_PROFILE_NAME = "elasticsearch.properties";

	private TransportClient transportClient;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static String cluster, hosts, port;

	private static class ElasticSearchManagerHolder {
		private static final AppAllLogElasticSearchManager INSTANCE = new AppAllLogElasticSearchManager();
	}

	public static final AppAllLogElasticSearchManager getInstance() {
		return ElasticSearchManagerHolder.INSTANCE;
	}

	public final TransportClient getClient() {
		return getInstance().transportClient;
	}

	private AppAllLogElasticSearchManager() {
		try {
			Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(ELASTIC_PROFILE_NAME));
			cluster = properties.getProperty("es2.cluster");
			hosts = properties.getProperty("es2.host");
			port = properties.getProperty("es2.port");
		} catch (Exception ex) {
			// 如果读取失败
			logger.error("Could not get configuration from local files.Connected to default cluster!");
			throw new RuntimeException("Could not get configuration from local files.Connected to default cluster!");
		}

		try {
			Settings settings = Settings.settingsBuilder().put("cluster.name", cluster).put("client.transport.sniff", true).build();// 自动嗅探集群中其他节点.put("threadpool.bulk.size", 66).put("threadpool.bulk.queueSize", 80)

			transportClient = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hosts), Integer.parseInt(port)));

			Runtime.getRuntime().addShutdownHook(new ShutdownHookApp());

		} catch (Exception ex) {
			logger.error("ElasticSearch initialize error! It's will not functional!", ex);
		}

	}

	public static String ObjectToJson(Object object) {

		String json = null;
		try {
			ObjectMapper om = new ObjectMapper();
			om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			json = om.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public TransportClient getTransportClient() {
		return transportClient;
	}

	public void close() {
		transportClient.close();
	}

	// private static HashSet<String> set = new HashSet<>();

	// public void createMapping(String indices, String mappingType) {
	//
	// try {
	// if (!set.contains(indices)) {
	// set.add(indices);
	// getTransportClient().admin().indices()// ------
	// .prepareCreate(indices)// ------
	// .setSettings(Settings.builder()// ------
	// .put("index.number_of_shards", 10)// ------
	// .put("index.number_of_replicas", 1))// ------
	// .execute()// ------
	// .actionGet();// ------
	//
	// XContentBuilder builder = XContentFactory.jsonBuilder()// ------
	// .startObject()// ------
	// .startObject(indices)// ------
	// .startObject("properties")// ------
	// .startObject("categroy").field("type", "string").endObject()// ------
	// .startObject("appip").field("type", "string").endObject()// ------
	// .startObject("timestamp").field("type", "date").field("format", "strict_date_optional_time").endObject()// ------
	// .startObject("message").field("type", "string").endObject()// ------
	// // .startObject("message").field("type", "string").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()// ------
	// .endObject()// ------
	// .endObject()// ------
	// .endObject();// ------
	// PutMappingRequest mapping = Requests.putMappingRequest(indices).type(mappingType).source(builder);
	// getTransportClient().admin().indices().putMapping(mapping).actionGet();
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
}

class ShutdownHookApp extends Thread {

	public void run() {
		System.out.println("elasticsearch client close...");
		AppAllLogElasticSearchManager.getInstance().close();
	}
}
