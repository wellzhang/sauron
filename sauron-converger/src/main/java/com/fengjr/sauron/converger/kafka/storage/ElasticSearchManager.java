package com.fengjr.sauron.converger.kafka.storage;

import java.net.InetAddress;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ElasticSearchManager {

	private String ELASTIC_PROFILE_NAME = "elasticsearch.properties";

	private TransportClient transportClient;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static String cluster, hosts, port;

	private static class ElasticSearchManagerHolder {
		private static final ElasticSearchManager INSTANCE = new ElasticSearchManager();
	}

	public static final ElasticSearchManager getInstance() {
		return ElasticSearchManagerHolder.INSTANCE;
	}

	public final TransportClient getClient() {
		return getInstance().transportClient;
	}

	private ElasticSearchManager() {
		try {
			Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(ELASTIC_PROFILE_NAME));
			cluster = properties.getProperty("es.cluster");
			hosts = properties.getProperty("es.host");
			port = properties.getProperty("es.port");
		} catch (Exception ex) {
			// 如果读取失败
			logger.error("Could not get configuration from local files.Connected to default cluster!");
			throw new RuntimeException("Could not get configuration from local files.Connected to default cluster!");
		}

		try {
			Settings settings = Settings.settingsBuilder().put("cluster.name", cluster).put("client.transport.sniff", true).build();// 自动嗅探集群中其他节点

			transportClient = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hosts), Integer.parseInt(port)));

			Runtime.getRuntime().addShutdownHook(new ShutdownHook());

		} catch (Exception ex) {
			logger.error("ElasticSearch initialize error! It's will not functional!");
			logger.error(ex.getMessage());
		} catch (Throwable ex) {
			ex.printStackTrace();
			logger.error("tttttttttttElasticSearch initialize error! It's will not functional!");
			logger.error(ex.getMessage());
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

	public ActionResponse createIndex(String index, String type, String id, Object source) {
		return createIndex(index, type, id, ObjectToJson(source).getBytes());
	}

	public ActionResponse createIndex(String index, String type, String id, byte[] source) {

		IndexRequestBuilder sourceBuilder = getClient().prepareIndex(index, type).setSource(source);

		if (StringUtils.isNotBlank(id)) {
			sourceBuilder.setId(id);
		}
		return sourceBuilder.execute().actionGet();
	}

	public BulkResponse createIndexBulk(BulkRequestBuilder prepareBulk) {
		BulkResponse actionGet = prepareBulk.execute().actionGet();
		return actionGet;
	}

	public GetResponse get(String index, String type, String id) {
		return transportClient.prepareGet(index, type, id).get();
	}

	public GetResponse get(String index, String type, String id, boolean operationThread) {
		return transportClient.prepareGet(index, type, id).setOperationThreaded(operationThread).get();
	}

	public DeleteResponse delete(String index, String type, String id) {
		return transportClient.prepareDelete(index, type, id).get();
	}

	public UpdateResponse update(String index, String type, String id, XContentBuilder document) {
		return transportClient.prepareUpdate().setDoc(document).get();
	}

	public UpdateResponse upsert(String index, String type, String id, XContentBuilder document) throws ExecutionException, InterruptedException {
		IndexRequest indexRequest = new IndexRequest(index, type, id).source(document);
		UpdateRequest updateRequest = new UpdateRequest(index, type, id).doc(document).upsert(indexRequest);
		return transportClient.update(updateRequest).get();
	}

	public MultiGetResponse multiGet(List<MultiGetRequest.Item> items) {

		MultiGetRequestBuilder builder = transportClient.prepareMultiGet();
		for (MultiGetRequest.Item item : items) {
			builder.add(item);
		}
		return builder.get();
	}

	public SearchResponse search(String[] indices, String[] types, QueryBuilder queryBuilder, QueryBuilder filterBuilder, int pageNo, int pageSize) {
		int startIndx = pageNo < 0 ? 0 : pageNo * pageSize;
		SearchResponse response = transportClient.prepareSearch(indices).setTypes(types).setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(queryBuilder) // Query
				.setPostFilter(filterBuilder) // Filter
				.setFrom(startIndx).setSize(pageSize)// 翻页
				.setExplain(true)// 允许按匹配度排行
				.execute().actionGet();
		return response;
	}

	public MultiSearchResponse multiSearch(String[] indices, String[] types, SearchRequestBuilder[] requestBuilders, int pageNo, int pageSize) {

		int startIndex = pageNo < 0 ? 0 : pageNo * pageSize;

		MultiSearchRequestBuilder multiSearchRequestBuilder = transportClient.prepareMultiSearch();

		for (SearchRequestBuilder requestBuilder : requestBuilders) {

			requestBuilder.setFrom(startIndex);
			requestBuilder.setSize(pageSize);

			multiSearchRequestBuilder.add(requestBuilder);
		}
		return multiSearchRequestBuilder.execute().actionGet();
	}

	public TransportClient getTransportClient() {
		return transportClient;
	}

	public void close() {
		transportClient.close();
	}
}

class ShutdownHook extends Thread {

	public void run() {
		System.out.println("elasticsearch client close...");
		ElasticSearchManager.getInstance().close();
	}
}
