package com.om.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/6/13
 * Time: 1:13 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class PortfolioDAO extends MongoDBBaseDAO {


    public DBCollection portfoliosTable = null;
    //public DBCollection userHistoryTable = null;


    public static final String PORTFOLIOS_DB_TABLE = "portfolios";


   // public static final String USERSHISTORY_TABLE = "userhistory";


    public String insertRecord(Map<String, Object> dataMap) throws Exception {
        return super.insertRecord(portfoliosTable, dataMap);
        //System.out.println("result.getField(\"_id\") = " + result.getField("_id"));

        //return (String)result.getField("_id");

    }

    public List<Map<String,Object>> getAllPortfolios(){
        BasicDBObject query = new BasicDBObject();
        DBCursor dbCursor = portfoliosTable.find(query);
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
        //System.out.println(String.format(" %s -  Post count ---> %s", date, dbCursor.count()));
        while (dbCursor.hasNext()) {
            BasicDBObject next = (BasicDBObject) dbCursor.next();
            retList.add(next);
        }

        return retList;
    }


    @PostConstruct
    private void connect()throws Exception{
        //usersdb = mongoDBConnectionManager.getDatabase(OPENSTREET_SCHEMA);
        portfoliosTable = super.getTable(super.openMarketSchema, PORTFOLIOS_DB_TABLE);
        //userHistoryTable = super.getTable(usersdb, USERSHISTORY_TABLE);
        //super.newsWorker = this.newsWorker;

    }
}
