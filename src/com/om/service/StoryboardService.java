package com.om.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.om.dao.PortfolioDAO;
import com.om.dao.StockMetadataDAO;
import com.om.util.DateTimeUtil;
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
@Path("/sb")
public class StoryboardService {



    @Autowired
    StockMetadataDAO stockMetadataDAO;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/addSlide")
    @Consumes("application/x-www-form-urlencoded")
    public String addSlide(@FormParam("data")String data){
        Gson gson = new Gson();
        Type type =new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> dataObj = (Map<String, String>)gson.fromJson(data,type);

        HashMap<String,Object> outObj = new HashMap<String, Object>();
        /* List<Map<String,Object>> dataObj1 = new ArrayList<Map<String, Object>>();
        for(int i=0;i<10;i++){
            HashMap<String,Object> dataObj2 = new HashMap<String, Object>();
            dataObj2.put("ticker",UUID.randomUUID().toString());
            dataObj2.put("sector","Technology");
            dataObj1.add(dataObj2);
        }*/


        return gson.toJson(outObj);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    @Consumes("application/x-www-form-urlencoded")
    public String pload(@FormParam("data") String data ,@Context HttpServletRequest servletRequest) throws Exception {

        HashMap<String,Object> dataObj = new HashMap<String, Object>();
       /* List<Map<String,Object>> dataObj1 = new ArrayList<Map<String, Object>>();
        for(int i=0;i<10;i++){
            HashMap<String,Object> dataObj2 = new HashMap<String, Object>();
            dataObj2.put("ticker",UUID.randomUUID().toString());
            dataObj2.put("sector","Technology");
            dataObj1.add(dataObj2);
        }*/

        List<Map<String, Object>> records = stockMetadataDAO.searchCompaniesBy(data);
        float pct = 100/records.size();
        for (Map<String, Object> record : records) {
            record.put("pct",String.format("%.2f",pct));
        }
        dataObj.put("data", records);

        Gson gson = new Gson();
        return gson.toJson(dataObj);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/psearch")
    @Consumes("application/x-www-form-urlencoded")
    public String pSearch(@FormParam("query") String query ,@Context HttpServletRequest servletRequest) throws Exception {
        System.out.println("query = " + query);
        List<Map<String,Object>> results = stockMetadataDAO.searchCompanies(query);
        HashMap<String,Object> dataObj = new HashMap<String, Object>();
        /*List<Map<String,Object>> dataObj1 = new ArrayList<Map<String, Object>>();
        for(int i=0;i<10;i++){
            HashMap<String,Object> dataObj2 = new HashMap<String, Object>();
            dataObj2.put("ticker",UUID.randomUUID().toString());
            dataObj2.put("cn","Company Name........");
            dataObj1.add(dataObj2);
        }*/

        dataObj.put("data",results);

        Gson gson = new Gson();
        return gson.toJson(dataObj);
    }


    private Map<String,Object> generatePortfolioObject(String pName,int stdays
            ,int amount,List<String> stocksList,String imgName,String desc,int backersCnt,int pctfund, int fundDays){
        Map<String,Object> pdata = new HashMap<String, Object>();
        pdata.put("name",pName);
        pdata.put("imgSrc",imgName);
        pdata.put("desc",desc.toUpperCase());
        pdata.put("backersCnt",backersCnt);
        pdata.put("pctfund",pctfund);
        pdata.put("funddays",fundDays);
        //pdata.put("backersCnt",backersCnt);
        pdata.put("uid",UUID.randomUUID().toString());
        pdata.put("stocksList",stocksList);
        Calendar cal = Calendar.getInstance();
        String s = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH)+1 + "-" + cal.get(Calendar.DATE);
        System.out.println("s = " + s);
        Date stdate = DateTimeUtil.getDateSince(stdays);

        Date enddate = new Date();
        pdata.put("stdate",stdays);
        pdata.put("enddate",enddate);

        pdata.put("amt",amount);

        //Map<String,Object> stocks = new HashMap<String, Object>();

        /*List<String> stocksList = new ArrayList<String>();
        stocksList.add("ORCL");
        stocksList.add("MSFT");
        stocksList.add("TK");
        stocksList.add("TE");*/

        List<String> backers = new ArrayList<String>();
        backers.add("Joe");
        backers.add("John");
        backers.add("Victor");
        //stocksList.add("IBM");

        // pdata.put("stocks",stocksList);
        pdata.put("notes",desc.toUpperCase());
        pdata.put("strategy","Conservative");
        pdata.put("backers",backers);
        Map<String,Object> managerInfo = new HashMap<String, Object>();
        managerInfo.put("name","Joe Marker") ;
        managerInfo.put("profile","Working in BankOfAmerica as Portfolio Manager");
        pdata.put("managerInfo",managerInfo);
        return pdata;
    }


}