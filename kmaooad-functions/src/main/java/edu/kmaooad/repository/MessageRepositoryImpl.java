package edu.kmaooad.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.kmaooad.exception.DatabaseConnectionException;
import edu.kmaooad.exception.InvalidOperationException;
import org.bson.Document;
import org.json.JSONObject;

public class MessageRepositoryImpl implements MessageRepository {

    private final String url;
    private final String databaseName;
    private final String collectionName;

    private MongoCollection<Document> collection;

    public MessageRepositoryImpl(String mongoUrl, String databaseName, String collectionName) throws DatabaseConnectionException {
        this.url = mongoUrl;
        this.databaseName = databaseName;
        this.collectionName = collectionName;

        init();
    }

    public void init() throws DatabaseConnectionException {
        try {
            MongoClient mongoClient = MongoClients.create(url);
            MongoDatabase database = mongoClient.getDatabase(databaseName);

            this.collection = database.getCollection(collectionName);
            this.collection.find();
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex.getMessage());
        }
    }

    public void addMessage(JSONObject message) throws InvalidOperationException {
        try {
            collection.insertOne(new Document(message.toMap()));
        } catch (Exception ex) {
            throw new InvalidOperationException();
        }
    }
}
