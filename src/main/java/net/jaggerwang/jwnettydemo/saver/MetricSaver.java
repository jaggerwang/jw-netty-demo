package net.jaggerwang.jwnettydemo.saver;

import com.mongodb.client.MongoCollection;
import net.jaggerwang.jwnettydemo.config.MongoDBConfig;
import org.bson.Document;

public class MetricSaver {

    public boolean save(Metric metric) {
        MongoCollection coll = MongoDBConfig.getDbQt().getCollection("metric");
        Document document = new Document()
                .append("name", metric.getName())
                .append("value", metric.getValue())
                .append("time", metric.getTime());
        coll.insertOne(document);
        return true;
    }
}
