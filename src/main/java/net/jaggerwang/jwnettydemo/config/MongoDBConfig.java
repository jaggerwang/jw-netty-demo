package net.jaggerwang.jwnettydemo.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConfig {

    private static MongoClient client;

    public static MongoClient getClient() {
        if (client == null) {
            MongoClientURI uri = new MongoClientURI(ApplicationConfig.getProperty("mongodb.uri"));
            client = new MongoClient(uri);
        }
        return client;
    }

    public static MongoDatabase getDbQt() {
        String name = ApplicationConfig.getProperty("mongodb.db");
        return getClient().getDatabase(name);
    }

    public static MongoCollection<Document> getCollQtDevice() {
        MongoDatabase db = getDbQt();
        return db.getCollection("device");
    }
}
