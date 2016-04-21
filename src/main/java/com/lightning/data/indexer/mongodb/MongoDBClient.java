package com.lightning.data.indexer.mongodb;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDBClient {
	
	private final static String URI = "mongodb://admin:admin@ds015750.mlab.com:15750/indexer-log";
	
	public static MongoClient getClient() {
		MongoClientURI uri = new MongoClientURI(URI);
		MongoClient mongoClient = new MongoClient(uri);
		return mongoClient;
	}

//	public static void main(String[] args) {
//		
//		MongoDatabase db = mongoClient.getDatabase("indexer-log");
//		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
//		db.getCollection("LastJob").insertOne(
//		        new Document("time",
//		                new Document()
//		                        .append("street", "2 Avenue")
//		                        .append("zipcode", "10075")
//		                        .append("building", "1480")
//		                        )
//		                .append("borough", "Manhattan")
//		                .append("cuisine", "Italian")
//		                .append("name", "Vella")
//		                .append("restaurant_id", "41704620"));
//		mongoClient.close();
//	}
}
