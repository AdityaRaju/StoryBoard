package com.om.dao;

import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/6/13
 * Time: 9:14 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class StockMetadataDAO extends MongoDBBaseDAO {

    public DBCollection portfoliosTable = null;
    public DBCollection cinfoTable = null;
    //public DBCollection userHistoryTable = null;


    public static final String PORTFOLIOS_DB_TABLE = "cfields";


    public static final String CINFO_DB_TABLE = "company";



    // public static final String USERSHISTORY_TABLE = "userhistory";


    public String insertRecord(Map<String, Object> dataMap) throws Exception {
        return super.insertRecord(portfoliosTable, dataMap);
        //System.out.println("result.getField(\"_id\") = " + result.getField("_id"));

        //return (String)result.getField("_id");

    }
    public String insertCInfoRecord(Map<String, Object> dataMap) throws Exception {
        return super.insertRecord(cinfoTable, dataMap);
        //System.out.println("result.getField(\"_id\") = " + result.getField("_id"));

        //return (String)result.getField("_id");

    }



    public List<Map<String,Object>> searchCompanies(String queryStr){


        return null;

    }
    public List<Map<String,Object>> searchCompaniesBy(String key){
        return null;
    }

    public List<Map<String,Object>> getAllCompanies(){
        //BasicDBObject likeObj = new BasicDBObject();
        //q.put("name",  java.util.regex.Pattern.compile(m));
        DBCursor dbObjects = cinfoTable.find();
        List<Map<String,Object>> results = new ArrayList<Map<String, Object>>();
        while(dbObjects.hasNext()) {
            BasicDBObject next = (BasicDBObject)dbObjects.next();
            Map<String,Object> map = next.toMap();
            results.add(map);
            System.out.println("map = " + map);
        }
        return results;

    }

    public List<Map<String,Object>> searchCompaniesBy(String key,String value){
        //BasicDBObject likeObj = new BasicDBObject();
        BasicDBObject dbObject = new BasicDBObject(key,value);
        //q.put("name",  java.util.regex.Pattern.compile(m));
        System.out.println("query = " + dbObject);
        DBCursor dbObjects = cinfoTable.find(dbObject);
        List<Map<String,Object>> results = new ArrayList<Map<String, Object>>();
        while(dbObjects.hasNext()) {
            BasicDBObject next = (BasicDBObject)dbObjects.next();
            Map<String,Object> map = next.toMap();
            results.add(map);
            System.out.println("map = " + map);
        }
        return results;

    }

    public List<Map<String,Object>> searchCompaniesByAny(String keyword){
        //BasicDBObject likeObj = new BasicDBObject();
        BasicDBObject dbObject1 = new BasicDBObject("ticker", Pattern.compile(keyword, Pattern.CASE_INSENSITIVE));

        //q.put("name",  java.util.regex.Pattern.compile(m));
        System.out.println("query = " + dbObject1);
        DBCursor dbObjects = cinfoTable.find(dbObject1);
        List<Map<String,Object>> results = new ArrayList<Map<String, Object>>();
        while(dbObjects.hasNext()) {
            BasicDBObject next = (BasicDBObject)dbObjects.next();
            Map<String,Object> map = next.toMap();
            results.add(map);
            //System.out.println("map = " + map);
        }

        BasicDBObject dbObject2 = new BasicDBObject("LONG_COMP_NAME", Pattern.compile(keyword, Pattern.CASE_INSENSITIVE));

        dbObjects = cinfoTable.find(dbObject2);
        while(dbObjects.hasNext()) {
            BasicDBObject next = (BasicDBObject)dbObjects.next();
            Map<String,Object> map = next.toMap();
            results.add(map);
            //System.out.println("map = " + map);
        }
        return results;

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
