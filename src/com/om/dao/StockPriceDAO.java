package com.om.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.om.util.DateTimeUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/6/13
 * Time: 9:14 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class StockPriceDAO extends MongoDBBaseDAO {

    public DBCollection portfoliosTable = null;
    public DBCollection cinfoTable = null;
    //public DBCollection userHistoryTable = null;


    public static final String PORTFOLIOS_DB_TABLE = "cfields";


    public static final String CINFO_DB_TABLE = "pricefeed";



    // public static final String USERSHISTORY_TABLE = "userhistory";


    public String insertRecord(Map<String, Object> dataMap) throws Exception {
        return super.insertRecord(portfoliosTable, dataMap);
        //System.out.println("result.getField(\"_id\") = " + result.getField("_id"));

        //return (String)result.getField("_id");

    }

    public String insertPriceRecord(Map<String, Object> dataMap) throws Exception {
        return super.insertRecord(cinfoTable, dataMap);
        //System.out.println("result.getField(\"_id\") = " + result.getField("_id"));

        //return (String)result.getField("_id");

    }

    public List<Map<String,Object>> getCompanyHistoricalData(String bbgid){
        Map<String, Date> dtMap = new HashMap<String, Date>();
        dtMap.put("$gte", DateTimeUtil.getDateSince(-100));
        dtMap.put("$lt",DateTimeUtil.getHoursSince(1));

        //BasicDBObject query = new BasicDBObject("dt",dtMap);
        BasicDBObject q1 = new BasicDBObject("ID_BB_GLOBAL",bbgid);
        q1.put("date",dtMap);
        //q.put("name",  java.util.regex.Pattern.compile(m));
       // DBCursor dbObjects = cinfoTable.find("");
        DBCursor dbObjects = cinfoTable.find(q1);

        List<Map<String,Object>> results = new ArrayList<Map<String, Object>>();
        while(dbObjects.hasNext()) {
            BasicDBObject next = (BasicDBObject)dbObjects.next();
            Map<String,Object> map = next.toMap();
            results.add(map);
            System.out.println("map = " + map);
        }
        return results;

    }

    public String getCompanyMarketCap(String bbgid){
        Map<String, Date> dtMap = new HashMap<String, Date>();
        dtMap.put("$gte", DateTimeUtil.getDateSince(-2));
        dtMap.put("$lt",DateTimeUtil.getHoursSince(1));

        //BasicDBObject query = new BasicDBObject("dt",dtMap);
        BasicDBObject q1 = new BasicDBObject("ID_BB_GLOBAL",bbgid);
        q1.put("date",dtMap);
        //q.put("name",  java.util.regex.Pattern.compile(m));
        // DBCursor dbObjects = cinfoTable.find("");
        DBCursor dbObjects = cinfoTable.find(q1);

        List<Map<String,Object>> results = new ArrayList<Map<String, Object>>();
        while(dbObjects.hasNext()) {
            BasicDBObject next = (BasicDBObject)dbObjects.next();
            Map<String,Object> map = next.toMap();
            results.add(map);
            System.out.println("map = " + map);
            return map.get("market_cap").toString();
        }
        return "240.18";

    }






    @PostConstruct
    private void connect()throws Exception{
        //usersdb = mongoDBConnectionManager.getDatabase(OPENSTREET_SCHEMA);
        portfoliosTable = super.getTable(super.openMarketSchema, PORTFOLIOS_DB_TABLE);
        cinfoTable = super.getTable(super.openMarketSchema, CINFO_DB_TABLE);
        //loadAllMetaData();
        //userHistoryTable = super.getTable(usersdb, USERSHISTORY_TABLE);
        //super.newsWorker = this.newsWorker;

    }
}
