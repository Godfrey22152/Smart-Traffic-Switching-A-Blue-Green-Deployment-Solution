package com.shashi.utility;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.shashi.beans.TrainException;
import com.shashi.constant.ResponseCode;

public class DBUtil {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    static {
        String uri = System.getenv("DB_URL");
        String dbName = System.getenv("DB_NAME");

        try {
            mongoClient = MongoClients.create(uri);
            database = mongoClient.getDatabase(dbName);
            System.out.println("MongoDB Connection Success!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static String getDatabaseName() {
        return System.getenv("DB_NAME");
    }

    public static MongoDatabase getDatabase() throws TrainException {
        if (database == null) {
            throw new TrainException(ResponseCode.DATABASE_CONNECTION_FAILURE);
        }
        return database;
    }

    public static boolean isDatabaseConnected() {
        try {
            if (database != null && database.listCollectionNames().first() != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
