package com.fengjr.sauron.dao.mongodb;

import java.util.List;

import javax.annotation.Resource;

import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

import com.feng.sauron.client.annotations.TraceClass;
import com.feng.sauron.client.annotations.TraceMethod;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

/**
 * Created by bingquan.an@fengjr.com on 2015/9/6.
 */
//@TraceClass
//@Repository("mongoDao")
public class MongoDao implements InitializingBean {

	//@Resource(name = "mongoFactory")
	MongoFactory mongoFactory;

	private MongoConfig wafConfig;

	public void afterPropertiesSet() throws Exception {
		this.wafConfig = mongoFactory.getMongoConfig("sauron");
	}

	public void insertOne(MongoCollection<Document> collection, Document document) {

		collection.insertOne(document);
	}

	public void insertMany(MongoCollection<Document> collection, List<Document> documents) {

		collection.insertMany(documents);
	}

	
	@TraceMethod
	public void insertOne(String collectionName, Document document) {

		wafConfig.getCollection(collectionName).insertOne(document);
	}

	public void insertMany(String collectionName, List<Document> documents) {

		wafConfig.getCollection(collectionName).insertMany(documents);
	}


	@TraceMethod
	public FindIterable<Document> getByFragment(String collectionName, Bson fragment) {


		FindIterable<Document> iterable = wafConfig.getCollection(collectionName).find(fragment);

		return iterable;
	}

	public String createIndex_ori(String collectionName, Bson fragment) {

		return wafConfig.getCollection(collectionName).createIndex(fragment);
	}

	public String createIndex_ori(String collectionName, Bson fragment,IndexOptions indexOptions) {

		return wafConfig.getCollection(collectionName).createIndex(fragment,indexOptions);
	}



	public String createIndex(String collectionName, String index, int index_fix,IndexOptions indexOptions) {

		String createIndex = null;
		try {
			ListIndexesIterable<Document> listIndexes = wafConfig.getCollection(collectionName).listIndexes();

			MongoCursor<Document> iterator = listIndexes.iterator();
			boolean flag = true;

			while (iterator.hasNext()) {
				Document document = (Document) iterator.next();
				for (String string : document.keySet()) {
					if ("key".equals(string)) {
						Document object = (Document) document.get(string);
						if (object.containsKey(index) && object.containsValue(index_fix)) {
							flag = false;
						}
					}
				}
			}
			if (flag) {
				if(indexOptions == null)
					createIndex = createIndex_ori(collectionName, new BasicDBObject(index, index_fix));
				else
					createIndex = createIndex_ori(collectionName, new BasicDBObject(index, index_fix),indexOptions);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return createIndex;
	}
}
