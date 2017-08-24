package com.fengjr.sauron.dao.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by bingquan.an@fengjr.com on 2015/9/2.
 */
public class MongoConfig implements InitializingBean {

	private final static Logger logger = LoggerFactory.getLogger(MongoConfig.class);

	private MongoClient mongoClient = null;
	private MongoDatabase database = null;
	private HashMap<String, MongoCollection<Document>> collectionMap = null;

	private List<String> mongosAddr;
	private List<ServerAddress> serverAddresses;
	private String dbName;
	private String user;
	private String pass;
	private HashSet<String> collectionNames;

	private MongoConfig() {
	};

	public MongoCollection<Document> getCollection(String colName) {

		return this.collectionMap.get(colName);
	}

	public void afterPropertiesSet() throws Exception {

		serverAddresses = new ArrayList<ServerAddress>(mongosAddr.size());
		String[] addrs;
		for (String addr : mongosAddr) {
			addrs = addr.split(":");
			serverAddresses.add(new ServerAddress(addrs[0], Integer.valueOf(addrs[1])));
			logger.info("MongoConfig mongosAddrs[ {}:{} ]", addrs[0], addrs[1]);
		}

//		MongoCredential credential = MongoCredential.createCredential(user, dbName, pass.toCharArray());

//		mongoClient = new MongoClient(serverAddresses, Arrays.asList(credential));
		mongoClient = new MongoClient(serverAddresses);
		database = mongoClient.getDatabase(dbName);
		collectionMap = new HashMap<String, MongoCollection<Document>>(collectionNames.size());

		for (String colName : collectionNames) {
			collectionMap.put(colName, database.getCollection(colName));
		}

	}

	public void setMongosAddr(List<String> mongosAddr) {
		this.mongosAddr = mongosAddr;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setCollectionNames(HashSet<String> collectionNames) {
		this.collectionNames = collectionNames;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

}
