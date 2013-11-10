package com.om.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mail.TestMail;
import com.om.dao.PortfolioDAO;
import com.om.dao.SlideDAO;
import com.om.dao.StockMetadataDAO;
import com.om.util.DateTimeUtil;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

    @Autowired
    SlideDAO slideDAO;

    @Autowired
    TwilloService twilloService;



    static String SL_LIST= "SLIDE_LIST_OBJ";

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/addSlide")
    @Consumes("application/x-www-form-urlencoded")
    public String addSlide(@FormParam("data")String data,@Context HttpServletRequest servletRequest)throws Exception{
        Gson gson = new Gson();
        Type type =new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> dataObj = (Map<String, String>)gson.fromJson(data,type);
        HttpSession session = servletRequest.getSession();
        List<Map<String,String>> slideList = null;
        if(session.getAttribute(SL_LIST) == null){
            slideList = new ArrayList<Map<String, String>>();
        }else{
            slideList = (List<Map<String,String>>)session.getAttribute(SL_LIST);
        }
        dataObj.put("slideId",slideDAO.getNextSlideId());
        slideList.add(dataObj);
        session.setAttribute(SL_LIST, slideList);
        HashMap<String,Object> outObj = new HashMap<String, Object>();
        return gson.toJson(outObj);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/clean")
    @Consumes("application/x-www-form-urlencoded")
    public String cleanPresentation(@FormParam("data")String data,@Context HttpServletRequest servletRequest)throws Exception{
        Gson gson = new Gson();
        HashMap<String,Object> outObj = new HashMap<String, Object>();
        HttpSession session = servletRequest.getSession();
        session.removeAttribute(SL_LIST);
        return gson.toJson(outObj);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/save")
    @Consumes("application/x-www-form-urlencoded")
    public String savePresentation(@FormParam("data")String data,@Context HttpServletRequest servletRequest)throws Exception{
        Gson gson = new Gson();
        Type type =new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> saveObjData = (Map<String, String>)gson.fromJson(data,type);
        HttpSession session = servletRequest.getSession();
        List<Map<String,String>> slideList  = (List<Map<String,String>>)session.getAttribute(SL_LIST);
        HashMap<String,Object> insertObj = new HashMap<String, Object>();

        String nextPPTId = slideDAO.getNextPPTId();
        insertObj.put("pptid", nextPPTId);
        insertObj.put("slides",slideList);
        insertObj.put("header","Welcome to Presentation on ..... !");

        slideDAO.insertPPT(insertObj);

        new TestMail().test(nextPPTId);

        String url = "http://localhost:9991/start1.html?q=" + nextPPTId;


        String body = "Jim Walsh has created a Analyst research presentation. Take a look @ "+url;
        try{
        twilloService.sendSMS(null,"8605144014",body);
            twilloService.sendSMS(null,"3475156410",body);
            //3475156410
        }catch(Exception e){}

        //slideList.add(dataObj);
        HashMap<String,Object> outObj = new HashMap<String, Object>();
        return gson.toJson(outObj);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getPPT")
    @Consumes("application/x-www-form-urlencoded")
    public String getPresentation(@FormParam("data")String data,@Context HttpServletRequest servletRequest)throws Exception{
        Gson gson = new Gson();
        Type type =new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> saveObjData = (Map<String, String>)gson.fromJson(data,type);
        Map<String, Object> result = slideDAO.getPPT(saveObjData.get("pid"));

        //slideList.add(dataObj);
        HashMap<String,Object> outObj = new HashMap<String, Object>();
        List<Map<String, String>> slides = (List<Map<String, String>>) result.get("slides");
        Map<String, Object> retObj = new HashMap<String, Object>();
        retObj.put("msg",result.get("header").toString()) ;
        Map<String, String> currentSlideMap = slides.get(0);

        if(slides.size() > 1){
            retObj.put("nextSlide",slides.get(1)) ;
        }
        currentSlideMap.put("nextSlideId",slides.get(1).get("slideId"));
        retObj.put("currSlide", currentSlideMap) ;
        outObj.put("data",retObj);
        HttpSession session = servletRequest.getSession();
        //List<Map<String,String>> slideList = (List<Map<String,String>>)session.getAttribute(SL_LIST);
        session.setAttribute(SL_LIST, slides);

        return gson.toJson(outObj);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/prevNext")
    @Consumes("application/x-www-form-urlencoded")
    public String prevNext(@FormParam("data")String data,@Context HttpServletRequest servletRequest)throws Exception{
        Gson gson = new Gson();
        Type type =new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> inputObj = (Map<String, String>)gson.fromJson(data,type);
        String slideId = inputObj.get("slideId");//servletRequest.getParameter("slideId");
        HttpSession session = servletRequest.getSession();
        List<Map<String,String>> slideList = (List<Map<String,String>>)session.getAttribute(SL_LIST);
        Map<String,String> slide = null;
        int counter = 0;
        boolean found = false;
        for (Map<String, String> map : slideList) {
             if(StringUtils.equalsIgnoreCase((String)map.get("slideId"),slideId)){
                 slide = map;
                  found = true;
                 break;
             }
            if(!found){
                counter++;
            }
        }
        //Map<String, Object> result = slideDAO.getPPT(servletRequest.getParameter("pid"));
        Map<String, Object> retObj = new HashMap<String, Object>();
        //retObj.put("msg",result.get("header").toString()) ;
        //Map<String, String> currentSlideMap = slides.get(0);
        if(StringUtils.equalsIgnoreCase(inputObj.get("type"),"prev")){
            if(slideList.size() > counter){
                retObj.put("nextSlide",slideList.get(counter+1)) ;
                slide.put("nextSlideId",slideList.get(counter+1).get("slideId"));
            }

            if(counter != 0){
                retObj.put("prevSlide",slideList.get(counter-1)) ;
                slide.put("prevSlideId",slideList.get(counter-1).get("slideId"));
            }
        }else{
            if(slideList.size() > counter+1){
                retObj.put("nextSlide",slideList.get(counter+1)) ;
                slide.put("nextSlideId",slideList.get(counter+1).get("slideId"));
            }

            if(counter != 0){
                retObj.put("prevSlide",slideList.get(counter-1)) ;
                slide.put("prevSlideId",slideList.get(counter-1).get("slideId"));
            }
        }


        retObj.put("currSlide", slide) ;
        //outObj.put("data",retObj);
        //slideList.add(dataObj);
        HashMap<String,Object> outObj = new HashMap<String, Object>();
        outObj.put("data",retObj);
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