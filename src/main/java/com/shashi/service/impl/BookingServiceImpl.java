package com.shashi.service.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.shashi.beans.HistoryBean;
import com.shashi.beans.TrainException;
import com.shashi.constant.ResponseCode;
import com.shashi.service.BookingService;
import com.shashi.utility.DBUtil;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class BookingServiceImpl implements BookingService {

    private final String COLLECTION_NAME = "history";
    private MongoCollection<Document> collection;

    public BookingServiceImpl() {
        MongoClient mongoClient = DBUtil.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase(DBUtil.getDatabaseName());
        this.collection = database.getCollection(this.COLLECTION_NAME);
    }

    @Override
    public List<HistoryBean> getAllBookingsByCustomerId(String customerEmailId) throws TrainException {
        List<HistoryBean> transactions = new ArrayList<>();
        try {
            for (Document doc : collection.find(eq("mailid", customerEmailId))) {
                transactions.add(convertDocumentToHistoryBean(doc));
            }
            if (transactions.isEmpty()) {
                throw new TrainException(ResponseCode.NO_CONTENT);
            }
        } catch (Exception e) {
            throw new TrainException(e.getMessage());
        }
        return transactions;
    }

    @Override
    public HistoryBean createHistory(HistoryBean details) throws TrainException {
        try {
            String transactionId = UUID.randomUUID().toString();
            Document doc = new Document("transid", transactionId)
                    .append("mailid", details.getMailId())
                    .append("tr_no", details.getTr_no())
                    .append("date", details.getDate())
                    .append("from_stn", details.getFrom_stn())
                    .append("to_stn", details.getTo_stn())
                    .append("seats", details.getSeats())
                    .append("amount", details.getAmount());
            collection.insertOne(doc);
            details.setTransId(transactionId);
            return details;
        } catch (Exception e) {
            throw new TrainException(e.getMessage());
        }
    }

    private HistoryBean convertDocumentToHistoryBean(Document doc) {
        HistoryBean history = new HistoryBean();
        history.setTransId(doc.getString("transid"));
        history.setMailId(doc.getString("mailid"));
        history.setTr_no(doc.getString("tr_no"));
        history.setDate(doc.getString("date"));
        history.setFrom_stn(doc.getString("from_stn"));
        history.setTo_stn(doc.getString("to_stn"));
        history.setSeats(doc.getInteger("seats"));
        history.setAmount(doc.getDouble("amount"));
        return history;
    }
}
