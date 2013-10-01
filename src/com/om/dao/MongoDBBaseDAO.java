package com.om.dao;

import com.mongodb.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/6/13
 * Time: 12:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class MongoDBBaseDAO {

    public DB openMarketSchema = null;
    @Autowired
    public MongoDBConnectionManager mongoDBConnectionManager;

    @PostConstruct
    private void connect()throws Exception{
        openMarketSchema = mongoDBConnectionManager.getDatabase(OPENSTREET_SCHEMA);
        //openMarketSchema = super.getTable(super.openMarketSchema, PORTFOLIOS_DB_TABLE);
        //userHistoryTable = super.getTable(usersdb, USERSHISTORY_TABLE);
        //super.newsWorker = this.newsWorker;

    }

    public static final String OPENSTREET_SCHEMA = "storyboard";

    public Set<String> getAllTables(DB db){
        Set<String> colls = db.getCollectionNames();

        for (String s : colls) {
            System.out.println(s);
        }
        return colls;
    }

    public DBCollection getTable(DB db,String colName){
        DBCollection coll = db.getCollection(colName);
        return coll;
    }

    public String insertRecord(DBCollection dbCollection,Map<String,Object> dataMap){
        //System.out.println("dataMap = " + dataMap);
        BasicDBObject basicDBObject = new BasicDBObject(dataMap);
        dbCollection.insert(basicDBObject);

        ObjectId id = (ObjectId) basicDBObject.get("_id");
        return id.toString();

    }

    /*public void insertRecord(DBCollection dbCollection,Map<String,Object> dataMap){
        //System.out.println("dataMap = " + dataMap);
        dbCollection.u(new BasicDBObject(dataMap));

    }*/

    public WriteResult update(DBObject q,
                              DBObject o,
                              boolean upsert,
                              boolean multi){
        return null;
    }
}
