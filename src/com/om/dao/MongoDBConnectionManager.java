package com.om.dao;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/6/13
 * Time: 12:47 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MongoDBConnectionManager {

    private Mongo mongoDBConnection = null;

    @PostConstruct
    public void createDBConnection() throws Exception {
        try {

            String hostName = InetAddress.getLocalHost().getHostName();
            System.out.println("hostName = " + hostName);
           if (StringUtils.containsIgnoreCase(hostName, "local")) {
                System.out.println("Trying to connect to mongodb using local ---------------- %%%%%%%%%%%%%% ");
                mongoDBConnection = new Mongo();
            } else {
                System.out.println("Trying to Remote connect using snews port 27017 ---------------- %%%%%%%%%%%%%% ");
                mongoDBConnection = new Mongo(new ServerAddress("snews.servehttp.com", 27017));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception -->"+e.getMessage()+" , Trying to connect to MongoDB using locally as last resort ---------------- %%%%%%%%%%%%%% ");
            mongoDBConnection = new Mongo();
        }
    }

    public DB getDatabase(String dbName) throws Exception {
        return mongoDBConnection.getDB(dbName);
    }
}
