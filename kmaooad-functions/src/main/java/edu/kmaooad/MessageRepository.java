package edu.kmaooad;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;

public class MessageRepository {

    private final MongoCollection<Document> collection;

    MessageRepository(String mongoUrl, String databaseName, String collectionName) {
        MongoClient mongoClient = MongoClients.create(mongoUrl);
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        this.collection = database.getCollection(collectionName);
        this.collection.find();

        System.out.println("Connected to DB");
    }

    public void addMessage(JSONObject message) {
        collection.insertOne(new Document(message.toMap()));
    }
}
