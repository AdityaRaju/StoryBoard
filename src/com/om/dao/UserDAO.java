package com.om.dao;

import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class UserDAO extends MongoDBBaseDAO {

   // public DB usersdb = null;
    public DBCollection userInfoTable = null;
    public DBCollection userHistoryTable = null;


    public static final String USERSINFO_TABLE = "userinfo";


    public static final String USERSHISTORY_TABLE = "userhistory";




    @PostConstruct
    private void connectCrawlerDB()throws Exception{
        //usersdb = mongoDBConnectionManager.getDatabase(USERS_DB);
        userInfoTable = super.getTable(super.openMarketSchema, USERSINFO_TABLE);
        userHistoryTable = super.getTable(super.openMarketSchema, USERSHISTORY_TABLE);
        //super.newsWorker = this.newsWorker;

    }

    public String insertHistoryRecord(Map<String, Object> dataMap) throws Exception {
        return super.insertRecord(userHistoryTable, dataMap);
        //System.out.println("result.getField(\"_id\") = " + result.getField("_id"));

        //return (String)result.getField("_id");

    }

    public boolean userHadRt(String post_id,String sn_usr_key, String session_id) throws Exception {
        BasicDBObject cond = new BasicDBObject();
        cond.put("post_id", post_id);
        cond.put("sn_usr_key",sn_usr_key);

        int count = userHistoryTable.find(cond).count();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }


    public Set<String> getUserHistoryPostIds(String sn_usr_key) throws Exception {
        BasicDBObject query = new BasicDBObject();
        //cond.put("post_id", post_id);
        query.put("sn_usr_key",sn_usr_key);
        Set<String> postKeys = new HashSet<String>();
        DBCursor cursor = userHistoryTable.find(query);
        while(cursor.hasNext()){
            DBObject next = cursor.next();
            postKeys.add((String)next.get("post_id"));
        }
        return postKeys;
    }

    public List<Map<String, Object>> getUserHistoryPosts(String sn_usr_key) throws Exception {
        List<Map<String, Object>> matchingPostsValues = new ArrayList<Map<String, Object>>();
        BasicDBObject query = new BasicDBObject();
        //cond.put("post_id", post_id);
        query.put("sn_usr_key",sn_usr_key);

        DBCursor cursor = userHistoryTable.find(query);
        while(cursor.hasNext()){
            matchingPostsValues.add((Map<String, Object>)cursor.next());

        }
        return matchingPostsValues;
    }

    public String insertRecord(Map<String, Object> dataMap) throws Exception {
        return super.insertRecord(userInfoTable, dataMap);
        //System.out.println("result.getField(\"_id\") = " + result.getField("_id"));

        //return (String)result.getField("_id");

    }

    public Map<String, Object> getUserInfo(String objectKey) throws Exception {
        try{
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(objectKey));

            DBCursor cursor = userInfoTable.find(query);
            while(cursor.hasNext()){
                return (Map<String, Object>)cursor.next();
            }
            //return super.insertRecord(userInfoTable, dataMap);
            //System.out.println("result.getField(\"_id\") = " + result.getField("_id"));

            //return (String)result.getField("_id");
        }catch(Exception e){
            BasicDBObject query = new BasicDBObject();
            query.put("scr_name", objectKey);

            DBCursor cursor = userInfoTable.find(query);
            while(cursor.hasNext()){
                return (Map<String, Object>)cursor.next();
            }
        }
        return null;
    }

    public String isUserExist(String u,String email) throws Exception {
        try{
            BasicDBObject query = new BasicDBObject();
            query.put("scr_name", u);

            DBCursor cursor = userInfoTable.find(query);
            while(cursor.hasNext()){
                return "UES";
            }

            try{
                query = new BasicDBObject();
                query.put("email", email);

                cursor = userInfoTable.find(query);
                while(cursor.hasNext()){
                    return "UEE";
                }
            }catch(Exception e1){}
            //return super.insertRecord(userInfoTable, dataMap);
            //System.out.println("result.getField(\"_id\") = " + result.getField("_id"));

            //return (String)result.getField("_id");
        }catch(Exception e){
            try{
                BasicDBObject query = new BasicDBObject();
                query.put("email", email);

                DBCursor cursor = userInfoTable.find(query);
                while(cursor.hasNext()){
                    return "UEE";
                }
            }catch(Exception e1){}
        }
        return "N";
    }

    public Map<String, Object> checkUserInfo(String u,String p) throws Exception {

        BasicDBObject query = new BasicDBObject();
        query.put("scr_name", u);

        DBCursor cursor = userInfoTable.find(query);
        while(cursor.hasNext()){
            //return "UES";
            Map<String, Object> userObj = (Map<String, Object>) cursor.next();
            if(!StringUtils.equals((String) userObj.get("password"), p)){
                throw new Exception("Username passed as -->"+u);
            }

            return  userObj;
        }



        return null;
    }





}

