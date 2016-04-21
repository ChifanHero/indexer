package com.lightning.data.indexer.notification.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.bson.Document;

import com.lightning.data.indexer.mongodb.MongoDBClient;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class Notification {
	
	private long triggerTime;
	private long finishTime;
	private Document document = new Document();
	private DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
	
	public void setTriggerTime(long triggerTime) {
		this.triggerTime = triggerTime;
		document.append("triggerTime", format.format(new Date(this.triggerTime)));
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
		document.append("triggerTime", format.format(new Date(this.finishTime)));
	}

	public void send() {
		MongoClient mongoClient = MongoDBClient.getClient();
		MongoDatabase db = mongoClient.getDatabase("indexer-log");
		removeOldRecords(db);
		db.getCollection("LastJob").insertOne(document);
		mongoClient.close();
	}
	
	private void removeOldRecords(MongoDatabase db) {
		db.getCollection("LastJob").deleteMany(new Document());
	}

}
