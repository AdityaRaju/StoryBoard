package com.om.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jimplush.goose.cleaners.DefaultDocumentCleaner;
import com.jimplush.goose.outputformatters.DefaultOutputFormatter;
import com.om.ai.AIWorker;
import com.om.dao.StockMetadataDAO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/6/13
 * Time: 6:51 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/eswidget")
public class EstimateWidgetService {

     @Autowired
    AIWorker aiWorker;

    @Autowired
    StockMetadataDAO stockMetadataDAO;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/find")
    @Consumes("application/x-www-form-urlencoded")

    public String search(@FormParam("data") String data,@Context HttpServletRequest servletRequest) throws Exception {
        Gson gson = new Gson();
        Type type =new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> dataObj = (Map<String, String>)gson.fromJson(data,type);
        String clean = dataObj.get("clean") ;
        String q = dataObj.get("q");
        //System.out.println("q = " + data+" , "+ q);
        String formattedText = "";
        if(StringUtils.isNotBlank(clean)){
            formattedText = q;
        }else{
            Document doc = Jsoup.parse(q);
            DefaultDocumentCleaner documentCleaner = new DefaultDocumentCleaner();
            Document cleandDoc = documentCleaner.clean(doc);
            DefaultOutputFormatter defaultOutputFormatter = new DefaultOutputFormatter();
            formattedText = defaultOutputFormatter.getFormattedText(doc.body());
        }

        Map<String,String> keywordTickerMatch = new HashMap<String, String>();

        List<String> keywords = aiWorker.getKeywords(formattedText, null, false);
        StringBuilder sb = new StringBuilder();
        for (String keyword : keywords) {
            sb.append(keyword).append(",");
            List<Map<String, Object>> results = stockMetadataDAO.searchCompaniesByRegEx(keyword);
            if(results != null && results.size()>0){
                Map<String, Object> result = results.get(0);
                //System.out.println("result = " + result.get("ticker"));
                keywordTickerMatch.put(keyword,(String)result.get("ticker"));
            }
            //headLineStr.replaceAll()
            /*for (Map<String, Object> result : results) {
                System.out.println("result = " + result.get("LONG_COMP_NAME"));
            }*/
        }
        //headline.put("tickerMatchMap",keywordTickerMatch);
        //headline.put("HeadlineStr",replaceStockInfo(keywordTickerMatch,headLine));
        //System.out.println("StringUtils.toStringArray(keywords) = " + sb.toString()); //StringUtils.toStringArray(keywords));


        Map<String,Object> retObj = new HashMap<String, Object>();
        retObj.put("data", keywordTickerMatch);

        return gson.toJson(retObj);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/news/{symbol}")
    @Consumes("application/x-www-form-urlencoded")
    public String newsFor(@PathParam("symbol")@DefaultValue("all") String symbol) throws Exception {
          String news = "";
        if(StringUtils.endsWithIgnoreCase(symbol,"all")){
            Client client = Client.create();

            WebResource webResource = client
                    .resource("http://betawebapi.dowjones.com/fintech/articles/api/v1/source/424/?count=20");

            ClientResponse response = webResource.accept("application/json")
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            //Gson gson = new Gson();

            news = response.getEntity(String.class);
           // return output;
        }else{

            Client client = Client.create();

            WebResource webResource = client
                    .resource("http://betawebapi.dowjones.com/fintech/articles/api/v1/instrument/"+symbol+"/?count=20");

            ClientResponse response = webResource.accept("application/json")
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            //Gson gson = new Gson();

            news = response.getEntity(String.class);

        }
        Gson gson = new Gson();
        HashMap<String,Object> newsMap = gson.fromJson(news, HashMap.class);
        List<Map<String,Object>> headlines = (List<Map<String,Object>>)newsMap.get("Headlines");
        for (Map<String, Object> headline : headlines) {
            Map<String,String> keywordTickerMatch = new HashMap<String, String>();
            //System.out.println("headline = " + headline.get("Headline"));
            String headLine = (String)headline.get("Headline");
            String headLineStr = headLine;
            //headline.put("HeadlineStr",headLine);
            List<String> keywords = aiWorker.getKeywords(headLine, null, false);
            StringBuilder sb = new StringBuilder();
            for (String keyword : keywords) {
                sb.append(keyword).append(",");
                List<Map<String, Object>> results = stockMetadataDAO.searchCompaniesByRegEx(keyword);
                if(results != null && results.size()>0){
                    Map<String, Object> result = results.get(0);
                    System.out.println("result = " + result.get("ticker"));
                    keywordTickerMatch.put(keyword,(String)result.get("ticker"));
                }
                //headLineStr.replaceAll()
                /*for (Map<String, Object> result : results) {
                    System.out.println("result = " + result.get("LONG_COMP_NAME"));
                }*/
            }
            headline.put("tickerMatchMap",keywordTickerMatch);
            headline.put("HeadlineStr",replaceStockInfo(keywordTickerMatch,headLine));
            //System.out.println("StringUtils.toStringArray(keywords) = " + sb.toString()); //StringUtils.toStringArray(keywords));
            //newsWorker.getKeywords((String)headline.get("Headline"),"",false);
        }
        String output = gson.toJson(newsMap);

        return output;


        //return null;
    }

    private String replaceStockInfo(Map<String,String> keywordTickerMatch,String text){
        Pattern p = Pattern.compile("[\\s+]");
        // Split input with the pattern
        StringBuilder sb = new StringBuilder();
        String[] result = p.split(text);
        for (int i = 0; i < result.length; i++) {
            String key = result[i];
            String val = keywordTickerMatch.get(key.toLowerCase());
            if(StringUtils.isNotBlank(val)){
                sb.append("<span class='news-ticker' n-ticker='" + val+
                        "'>").append(key).append("</span> ");
            }else{
                sb.append(key).append(" ");
            }
        }

        return sb.toString();
    }










    }
