package com.shashi.service.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.shashi.beans.TrainBean;
import com.shashi.beans.TrainException;
import com.shashi.constant.ResponseCode;
import com.shashi.service.TrainService;
import com.shashi.utility.DBUtil;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class TrainServiceImpl implements TrainService {

    private final String COLLECTION_NAME = "train";
    private MongoCollection<Document> collection;

    public TrainServiceImpl() {
        MongoClient mongoClient = DBUtil.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase(DBUtil.getDatabaseName());
        this.collection = database.getCollection(this.COLLECTION_NAME);
    }

    @Override
    public String addTrain(TrainBean train) {
        try {
            Document query = new Document("tr_no", train.getTr_no());
            if (collection.find(query).first() != null) {
                return ResponseCode.FAILURE + " : Train with ID: " + train.getTr_no() + " already exists";
            }
            Document doc = new Document("tr_no", train.getTr_no())
                    .append("tr_name", train.getTr_name())
                    .append("from_stn", train.getFrom_stn())
                    .append("to_stn", train.getTo_stn())
                    .append("seats", train.getSeats())
                    .append("fare", train.getFare());
            collection.insertOne(doc);
            return ResponseCode.SUCCESS.toString();
        } catch (Exception e) {
            return ResponseCode.FAILURE + " : " + e.getMessage();
        }
    }

    @Override
    public String deleteTrainById(String trainNo) {
        try {
            Document query = new Document("tr_no", Long.parseLong(trainNo));
            DeleteResult result = collection.deleteOne(query);
            if (result.getDeletedCount() > 0) {
                return ResponseCode.SUCCESS.toString();
            } else {
                return ResponseCode.FAILURE.toString();
            }
        } catch (Exception e) {
            return ResponseCode.FAILURE + " : " + e.getMessage();
        }
    }

    @Override
    public String updateTrain(TrainBean train) {
        try {
            Document query = new Document("tr_no", train.getTr_no());
            Document update = new Document("$set", new Document("tr_name", train.getTr_name())
                    .append("from_stn", train.getFrom_stn())
                    .append("to_stn", train.getTo_stn())
                    .append("seats", train.getSeats())
                    .append("fare", train.getFare()));
            UpdateResult result = collection.updateOne(query, update);
            if (result.getModifiedCount() > 0) {
                return ResponseCode.SUCCESS.toString();
            } else {
                return ResponseCode.FAILURE.toString();
            }
        } catch (Exception e) {
            return ResponseCode.FAILURE + " : " + e.getMessage();
        }
    }

    @Override
    public TrainBean getTrainById(String trainNo) throws TrainException {
        try {
            Document query = new Document("tr_no", Long.parseLong(trainNo));
            Document result = collection.find(query).first();
            if (result != null) {
                return convertDocumentToTrainBean(result);
            } else {
                throw new TrainException(ResponseCode.NO_CONTENT);
            }
        } catch (Exception e) {
            throw new TrainException(e.getMessage());
        }
    }

    @Override
    public List<TrainBean> getAllTrains() throws TrainException {
        List<TrainBean> trains = new ArrayList<>();
        try {
            for (Document doc : collection.find()) {
                trains.add(convertDocumentToTrainBean(doc));
            }
            if (trains.isEmpty()) {
                throw new TrainException(ResponseCode.NO_CONTENT);
            }
        } catch (Exception e) {
            throw new TrainException(e.getMessage());
        }
        return trains;
    }

    @Override
    public List<TrainBean> getTrainsBetweenStations(String fromStation, String toStation) throws TrainException {
        List<TrainBean> trains = new ArrayList<>();
        try {
            for (Document doc : collection.find(Filters.and(
                    Filters.regex("from_stn", fromStation, "i"),
                    Filters.regex("to_stn", toStation, "i")
            ))) {
                trains.add(convertDocumentToTrainBean(doc));
            }
            if (trains.isEmpty()) {
                throw new TrainException(ResponseCode.NO_CONTENT);
            }
        } catch (Exception e) {
            throw new TrainException(e.getMessage());
        }
        return trains;
    }

    private TrainBean convertDocumentToTrainBean(Document doc) {
        TrainBean train = new TrainBean();
        train.setTr_no(doc.getLong("tr_no"));
        train.setTr_name(doc.getString("tr_name"));
        train.setFrom_stn(doc.getString("from_stn"));
        train.setTo_stn(doc.getString("to_stn"));
        train.setSeats(doc.getInteger("seats"));
        train.setFare(doc.getDouble("fare"));
        return train;
    }
}
