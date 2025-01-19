package com.shashi.service.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.shashi.beans.TrainException;
import com.shashi.beans.UserBean;
import com.shashi.constant.ResponseCode;
import com.shashi.constant.UserRole;
import com.shashi.service.UserService;
import com.shashi.utility.DBUtil;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final String COLLECTION_NAME;
    private MongoCollection<Document> collection;

    public UserServiceImpl(UserRole userRole) {
        if (userRole == null) {
            userRole = UserRole.CUSTOMER;
        }
        // Convert to lower case to match MongoDB collection names
        this.COLLECTION_NAME = userRole.toString().toLowerCase();
        MongoClient mongoClient = DBUtil.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase(DBUtil.getDatabaseName());
        this.collection = database.getCollection(this.COLLECTION_NAME);
    }
    
    @Override
    public UserBean getUserByEmailId(String customerEmailId) throws TrainException {
        Document query = new Document("mailid", customerEmailId);
        Document result = collection.find(query).first();
        if (result != null) {
            return convertDocumentToUserBean(result);
        } else {
            throw new TrainException(ResponseCode.NO_CONTENT);
        }
    }

    @Override
    public List<UserBean> getAllUsers() throws TrainException {
        List<UserBean> customers = new ArrayList<>();
        for (Document doc : collection.find()) {
            customers.add(convertDocumentToUserBean(doc));
        }
        if (customers.isEmpty()) {
            throw new TrainException(ResponseCode.NO_CONTENT);
        }
        return customers;
    }

    @Override
    public String updateUser(UserBean customer) {
        Document query = new Document("mailid", customer.getMailId());
        Document update = new Document("$set", new Document("fname", customer.getFName())
                .append("lname", customer.getLName())
                .append("addr", customer.getAddr())
                .append("phno", customer.getPhNo()));
        UpdateResult result = collection.updateOne(query, update);
        if (result.getModifiedCount() > 0) {
            return ResponseCode.SUCCESS.toString();
        } else {
            return ResponseCode.FAILURE.toString();
        }
    }

    @Override
    public String deleteUser(UserBean customer) {
        Document query = new Document("mailid", customer.getMailId());
        DeleteResult result = collection.deleteOne(query);
        if (result.getDeletedCount() > 0) {
            return ResponseCode.SUCCESS.toString();
        } else {
            return ResponseCode.FAILURE.toString();
        }
    }

    @Override
    public String registerUser(UserBean customer) {
        try {
            Document query = new Document("mailid", customer.getMailId());
            if (collection.find(query).first() != null) {
                return ResponseCode.FAILURE + " : User With Id: " + customer.getMailId() + " is already registered";
            }
            Document doc = new Document("mailid", customer.getMailId())
                    .append("pword", customer.getPWord())
                    .append("fname", customer.getFName())
                    .append("lname", customer.getLName())
                    .append("addr", customer.getAddr())
                    .append("phno", customer.getPhNo());
            collection.insertOne(doc);
            return ResponseCode.SUCCESS.toString();
        } catch (Exception e) {
            return ResponseCode.FAILURE + " : " + e.getMessage();
        }
    }

    @Override
    public UserBean loginUser(String username, String password) throws TrainException {
        Document query = new Document("mailid", username).append("pword", password);
        Document result = collection.find(query).first();
        if (result != null) {
            return convertDocumentToUserBean(result);
        } else {
            throw new TrainException(ResponseCode.UNAUTHORIZED);
        }
    }

    private UserBean convertDocumentToUserBean(Document doc) {
        UserBean customer = new UserBean();
        customer.setFName(doc.getString("fname"));
        customer.setLName(doc.getString("lname"));
        customer.setAddr(doc.getString("addr"));
        customer.setMailId(doc.getString("mailid"));
        customer.setPhNo(doc.getLong("phno"));
        customer.setPWord(doc.getString("pword"));
        return customer;
    }
}
