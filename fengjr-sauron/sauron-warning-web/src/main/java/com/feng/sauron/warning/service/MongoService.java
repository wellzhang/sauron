package com.feng.sauron.warning.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import com.fengjr.sauron.dao.mongodb.MongoDao;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

import javax.annotation.Resource;

@Service
@Deprecated
public class MongoService {

//	@Resource(name = "mongoDao")
	MongoDao mongoDao;

	public void saveMsg(String colName, Map<String, Object> logData) {
		Document document = new Document(logData);
		mongoDao.insertOne(colName, document);
	}

	public List<Document> findMsg(String colName, Bson bson, Integer limitCount) {

		if (limitCount == null) {
			limitCount = 20;
		}

		List<Document> arrayList = new ArrayList<>();
		FindIterable<Document> byFragment = mongoDao.getByFragment(colName, bson).limit(limitCount).sort(new BasicDBObject("logtime",-1));
		MongoCursor<Document> iterator = byFragment.iterator();

		while (iterator.hasNext()) {
			Document document = (Document) iterator.next();
			document.remove("_id");
			arrayList.add(document);
		}
		return arrayList;
	}
}
