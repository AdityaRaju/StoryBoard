package com.om.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
