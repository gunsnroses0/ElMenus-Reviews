package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import Commands.Command;

public class Review {
	
	private static int DbPoolCount = 4 ; 
	private static final String COLLECTION_NAME = "reviews";
	static String host = System.getenv("MONGO_URI");
//	static MongoClientURI uri = new MongoClientURI(host);
//	static MongoClient mongoClient = new MongoClient(uri);
//	static MongoDatabase database = mongoClient.getDatabase("El-Menus");

	private static MongoCollection<Document> collection = null;

	public static HashMap<String, Object> create(HashMap<String, Object> atrributes,String id) {
		System.out.println("inside create --> mongo host down");
		System.out.println(host);
		
		try {
			System.out.println("INTRY");
			MongoClientOptions.Builder options = MongoClientOptions.builder()
                    .connectionsPerHost(DbPoolCount);
			MongoClientURI uri = new MongoClientURI(host , options);
			MongoClient mongoClient = new MongoClient(uri);
			MongoDatabase database = mongoClient.getDatabase(("El-Menus"));
			// Retrieving a collection
			MongoCollection<Document> collection = database.getCollection("reviews");
			Document newReview = new Document();

			for (String key : atrributes.keySet()) {
				newReview.append(key, atrributes.get(key));
			}

				newReview.append("target_id", id);
			collection.insertOne(newReview);

		} catch (Exception e) {
			System.out.println(e);
		}
		return atrributes;

	}


	public static int getDbPoolCount() {
		return DbPoolCount;
	}


	public static void setDbPoolCount(int dbPoolCount) {
		DbPoolCount = dbPoolCount;
	}


	public static HashMap<String, Object> get(String messageId) {


		// Retrieving a collection
		MongoClientOptions.Builder options = MongoClientOptions.builder()
                .connectionsPerHost(DbPoolCount);
		MongoClientURI uri = new MongoClientURI(host , options);
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase(("El-Menus"));
		MongoCollection<Document> collection = database.getCollection("reviews");
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(messageId));

		System.out.println(query.toString());
		HashMap<String, Object> message = null;
		Document doc = collection.find(query).first();
		JSONParser parser = new JSONParser(); 
		try {
			JSONObject json = (JSONObject) parser.parse(doc.toJson());
		
			message = Command.jsonToMap(json);
			
			System.out.println(message.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return message;
	}
}
