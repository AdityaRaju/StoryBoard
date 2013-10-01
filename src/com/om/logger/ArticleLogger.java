package com.om.logger;

import com.mongodb.util.JSON;
import com.om.dao.URLCrawlerDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


import com.mongodb.util.JSON;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ArticleLogger {

    private Logger log = Logger.getLogger("urlCrawler");

    @Autowired
    URLCrawlerDAO urlCrawlerDAO;

    public  void log(String s){
        //System.out.println("URL Crawler = " + s);

        log.info(s);


    }

    public void insertArticleLog(Map<String,Object> dataMap){

        //log it to DB too...
        try{
            urlCrawlerDAO.insertRecord(dataMap);
        }catch (Exception e){
            System.out.println(String.format("Exception while inserting crawl log for %s  is = %s", JSON.serialize(dataMap), e.getMessage()));
        }

    }


}

