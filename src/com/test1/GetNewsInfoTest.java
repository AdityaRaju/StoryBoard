package com.test1;

import com.google.gson.Gson;
import com.om.ai.AIWorker;
import com.om.context.ApplicationContext;
import com.om.dao.StockMetadataDAO;
import com.om.dao.StockPriceDAO;
import com.om.service.CompanyService;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 10/1/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetNewsInfoTest {

    public static void main(String[] args) throws Exception {
        ApplicationContext instance = ApplicationContext.getInstance();
        CompanyService companyService = (CompanyService) instance.getBean("companyService");
        StockMetadataDAO stockMetadataDAO = (StockMetadataDAO) instance.getBean("stockMetadataDAO");

        //NewsWorker newsWorker = (NewsWorker)instance.getBean("newsWorker");
        AIWorker aiWorker = (AIWorker)instance.getBean("AIWorker");

        String pattern = "twitter(?i)";
        Pattern pattern1 = Pattern.compile("\\b" + "twitter" + "\\b", Pattern.CASE_INSENSITIVE);
        //TODO: why gap is not working...
        String sentence = "U.S. Hot Stocks: Santarus, AVG Tech, Gap, Lennar, Groupon";
        //String result1 = sentence.replaceAll("(?i)fox", "twitter <span class='abc'>$1$3</span>");
        //System.out.println("Input: " + sentence);
        //System.out.println("Output: " + result1);
       // System.out.println("Twitter all str IBM ".replaceAll(pattern1, "twitter <span class='abc'>$1$3</span>"));
        //StringUtils.replaceEach()
        String news = companyService.newsFor("aapl");
        Gson gson = new Gson();
        HashMap<String,Object> newsMap = gson.fromJson(news, HashMap.class);
        List<Map<String,Object>> headlines = (List<Map<String,Object>>)newsMap.get("Headlines");
        for (Map<String, Object> headline : headlines) {
            System.out.println("headline = " + headline.get("Headline"));
            String headLine = (String)headline.get("Headline");
            List<String> keywords = aiWorker.getKeywords(headLine, null, false);
            StringBuilder sb = new StringBuilder();
            for (String keyword : keywords) {
                 sb.append(keyword).append(",");
                List<Map<String, Object>> results = stockMetadataDAO.searchCompaniesByRegEx(keyword);
                if(results != null && results.size()>0){
                    Map<String, Object> result = results.get(0);
                    System.out.println("result = " + result.get("LONG_COMP_NAME"));
                }
                /*for (Map<String, Object> result : results) {
                    System.out.println("result = " + result.get("LONG_COMP_NAME"));
                }*/
            }
            System.out.println("StringUtils.toStringArray(keywords) = " + sb.toString()); //StringUtils.toStringArray(keywords));
            //newsWorker.getKeywords((String)headline.get("Headline"),"",false);
        }
        //System.out.println("stockMetadataDAO.searchCompaniesBy(\"ibm\") = " + stockMetadataDAO.searchCompaniesByAny("inter"));

    }
}
