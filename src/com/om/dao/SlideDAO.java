package com.om.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.WriteResult;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/6/13
 * Time: 9:14 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SlideDAO extends MongoDBBaseDAO {

    public DBCollection portfoliosTable = null;
    public DBCollection slideIdGen = null;
    //public DBCollection userHistoryTable = null;




    public static final String SLIDE_ID_GEN = "slideidgen";



    // public static final String USERSHISTORY_TABLE = "userhistory";


    public String getNextSlideId() throws Exception {
        //return super.insertRecord(portfoliosTable, dataMap);

        BasicDBObject q = new BasicDBObject("_id","slideid");
        BasicDBObject inc = new BasicDBObject("$inc",new BasicDBObject("num",1));

        WriteResult update = slideIdGen.update(q, inc);
        System.out.println("update = " + update.getField("num"));
        //System.out.println("result.getField(\"_id\") = " + result.getField("_id"));

        //return (String)result.getField("_id");

        return "SL0"+getSlideIdGenValue();

    }

    public int getSlideIdGenValue(){
        //BasicDBObject likeObj = new BasicDBObject();
        BasicDBObject q = new BasicDBObject("_id","slideid");
        //q.put("name",  java.util.regex.Pattern.compile(m));
        System.out.println("query = " + q);
        DBCursor dbObjects = slideIdGen.find(q);
        List<Map<String,Object>> results = new ArrayList<Map<String, Object>>();
        while(dbObjects.hasNext()) {
            BasicDBObject next = (BasicDBObject)dbObjects.next();
            Map<String,Object> map = next.toMap();
            return ((Double)map.get("num")).intValue();
        }
        return 999999;

    }

    public String insertPriceRecord(Map<String, Object> dataMap) throws Exception {
        return super.insertRecord(slideIdGen, dataMap);
        //System.out.println("result.getField(\"_id\") = " + result.getField("_id"));

        //return (String)result.getField("_id");

    }






    @PostConstruct
    private void connect()throws Exception{
        //usersdb = mongoDBConnectionManager.getDatabase(OPENSTREET_SCHEMA);
        //portfoliosTable = super.getTable(super.openMarketSchema, PORTFOLIOS_DB_TABLE);
        slideIdGen = super.getTable(super.openMarketSchema, SLIDE_ID_GEN);
        //loadAllMetaData();
        //userHistoryTable = super.getTable(usersdb, USERSHISTORY_TABLE);
        //super.newsWorker = this.newsWorker;

    }
}
