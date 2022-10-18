package edu.kmaooad.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;

public class MessageRepositoryImpl implements MessageRepository {

    private final String url;
    private final String databaseName;
    private final String collectionName;

    private MongoCollection<Document> collection;

    public MessageRepositoryImpl(String mongoUrl, String databaseName, String collectionName) {
        this.url = mongoUrl;
        this.databaseName = databaseName;
        this.collectionName = collectionName;

        init();
    }

    public void init() {
        MongoClient mongoClient = MongoClients.create(url);
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        this.collection = database.getCollection(collectionName);
        this.collection.find();
    }

    public void addMessage(JSONObject message) {
        collection.insertOne(new Document(message.toMap()));
    }
}
