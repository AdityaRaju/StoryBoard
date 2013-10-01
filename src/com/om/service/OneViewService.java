package com.om.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.om.WebLinkCrawler;
import com.om.dao.PortfolioDAO;
import com.om.dao.StockMetadataDAO;
import com.om.util.DateTimeUtil;
//import com.sruti.web.scrapper.WebSiteCommonContentHandler;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/6/13
 * Time: 6:51 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/ov")
public class OneViewService {


    //@Autowired
    //WebSiteCommonContentHandler webSiteCommonContentHandler;





    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/fetch")
    @Consumes("application/x-www-form-urlencoded")
    public String webSearch(@FormParam("u") String queryJSON ,@Context HttpServletRequest servletRequest) throws Exception {
        Gson gson = new Gson();

        //Map<String,String> dataMap = gson.fromJson(queryJSON,HashMap.class);
        String query = "http://"+queryJSON;
        System.out.println("Asking for URL =====> " + query);

        HashMap<String,Object> dataObj = new HashMap<String, Object>();
        dataObj.put("status","success");

        //dataObj.put("data",results);

        List<Map<String, Object>> mapList = null;//webSiteCommonContentHandler.allArticlesFor(query,true);
        //Gson gson = new Gson();
        /*for (Map<String, Object> map : mapList) {
            System.out.println(" = " + gson.toJson(map));
        }*/

        //Gson gson = new Gson();
        return gson.toJson(mapList);
    }




}
