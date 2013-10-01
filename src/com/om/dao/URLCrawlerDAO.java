package com.om.dao;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/27/13
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 7/14/12
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class URLCrawlerDAO extends MongoDBBaseDAO{

    public static final String CRAWL_LOGS = "urlinfo";
    public static final String ARTICLE_DB = "articledb";
    @Autowired
    MongoDBConnectionManager mongoDBConnectionManager;

    DB crawlerLogDB = null;
    DBCollection crawlerLogTable = null;

    @PostConstruct
    private void connectCrawlerDB()throws Exception{
        crawlerLogDB = mongoDBConnectionManager.getDatabase(ARTICLE_DB);
        crawlerLogTable = super.getTable(crawlerLogDB, CRAWL_LOGS);

    }

    public void insertRecord(Map<String,Object> dataMap) throws Exception{
        super.insertRecord(crawlerLogTable,dataMap);

    }

    public DBCollection getCrawlerLogTable() {
        return crawlerLogTable;
    }

    public Map<String,Object> find(String url){
        BasicDBObject query = new BasicDBObject();

        query.put("u", url);
        DBCursor dbObjects = getCrawlerLogTable().find(query);
        while(dbObjects.hasNext()) {
            BasicDBObject next = (BasicDBObject)dbObjects.next();
            Map<String,Object> map = next.toMap();
            //System.out.println(String.format("Found map for = %s , %s ",url, map));
            return map;
        }
        return null;
    }

    public List<Map<String,Object>> findAllBy(String src){
        BasicDBObject query = new BasicDBObject();

        query.put("src", src);
        List<Map<String,Object>> results = new ArrayList<Map<String, Object>>();
        DBCursor dbObjects = getCrawlerLogTable().find(query);
        while(dbObjects.hasNext()) {
            BasicDBObject next = (BasicDBObject)dbObjects.next();
            Map<String,Object> map = next.toMap();
            results.add(map);
            //System.out.println(String.format("Found map for = %s , %s ",url, map));
            //return map;
        }
        return results;
    }
}

