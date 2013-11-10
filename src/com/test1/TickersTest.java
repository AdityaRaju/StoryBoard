package com.test1;

import com.google.gson.Gson;
import com.om.ai.AIWorker;
import com.om.context.ApplicationContext;
import com.om.dao.StockMetadataDAO;
import com.om.service.CompanyService;

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
public class TickersTest {

    public static void main(String[] args) throws Exception {
        ApplicationContext instance = ApplicationContext.getInstance();
        CompanyService companyService = (CompanyService) instance.getBean("companyService");
        StockMetadataDAO stockMetadataDAO = (StockMetadataDAO) instance.getBean("stockMetadataDAO");

        //NewsWorker newsWorker = (NewsWorker)instance.getBean("newsWorker");
        AIWorker aiWorker = (AIWorker)instance.getBean("AIWorker");

        String pattern = "twitter(?i)";
        Pattern pattern1 = Pattern.compile("\\b" + "twitter" + "\\b", Pattern.CASE_INSENSITIVE);
        //TODO: why gap is not working...
        String body = "\\n <div class=\\\"page-number\\\"><button type=\\\"button\\\" class=\\\"btn btn-info\\\" id=\\\"profileBtn\\\" maindiv=\\\"profile\\\">Get Estimates</button></div>\\n <h3>Stocks near highs face Apple , Google , Cisco, shaky sentiment</h3>\\n <div>\\n\\n Nov. 10, 2013, 8:00 a.m. EST\\n\\n A string of economic data releases may be clouded by shutdown\\n\\n By Wallace Witkowski, MarketWatch\\n\\n <p>SAN FRANCISCO (MarketWatch) — As concerns over an overheated stock market run high, investor attention this week will shift to the economy, the next Fed chief, and the outlook from tech bellwether Cisco Systems Inc. </p>\\n\\n <p>The week’s data releases are likely to be muddied by the government’s October shutdown, making it even harder to figure out whether the economic recovery is solid enough for the Federal Reserve to back off its stimulus. Some earnings reports—notably Cisco CSCO +1.73% , Wal-Mart Stores Inc. WMT +0.58% , and a few other retailers—may help fill in the blanks.\\n\\n Last week, the Dow Jones Industrial Average DJIA +1.08% finished up 0.9%, the S&amp;P 500 Index SPX +1.34% gained 0.5%, while the Nasdaq Composite Index COMP +1.60% shed less than 0.1%.\\n\\n Stocks finished higher on the day Friday , including a record close for the Dow, after a report that the U.S. economy added more jobs than expected. Economic forecasters, however, are torn about the reliability of the October jobs report. Atlanta Fed President Lockhart said on Friday that some data will be less reliable through the end of the year.\\n\\n Even so, earlier in the week, San Francisco Fed President John Williams told reporters that consumer confidence data will be a big factor going forward in the decision on when to start tapering some $85 billion a month in asset purchases. On Friday, consumer sentiment data hit its lowest level since 2011.\\n\\n In the meantime, some investors are reacting to perceived signs of a frothy market. Outflows hit U.S. equity funds as stocks traded near record highs.\\n\\n “The market’s due for a rest and that’s what we’re seeing,” said Paul Nolte, managing director at Dearborn Partners. Nolte said he still expects a 3% to 5% pullback from recent highs.\\n\\n Societe Generale is taking an even gloomier view. It’s predicted a 15% correction for U.S. stocks, citing rising bond yields.\\n\\n Currently, the Dow industrials and S&amp;P 500 are 0.3% off their all-time highs, and the Nasdaq is off 1.2% from its latest 13-year high.\\n\\n Third-quarter productivity and October capacity utilization data will be of particular interest to Robert Pavlik, chief market strategist at Banyan Partners.\\n\\n “Hopefully those numbers show a little growth, if manufacturing is keeping pace,” Pavlik said.\\n\\n Thursday also brings the Senate confirmation hearing of Janet Yellen to head the Federal Reserve. Yellen is expected to continue the dovish Fed policies of current chairman Ben Bernanke. Treasury yields, which spiked at the end of the week, are particularly vulnerable to any signs she’ll want to curtail the Fed’s bond purchase program as soon as this year.\\n\\n Most financial markets are open Monday, Veterans Day, though there are no government data releases.</p><p></p></div>\\n";
        List<String> keywords = aiWorker.getKeywords(body, null, false);

        for (String keyword : keywords) {
            //sb.append(keyword).append(",");
            List<Map<String, Object>> results = stockMetadataDAO.searchCompaniesByRegEx(keyword);
            if(results != null && results.size()>0){
                Map<String, Object> result = results.get(0);
                System.out.println("result = " + result.get("LONG_COMP_NAME"));
            }
            /*for (Map<String, Object> result : results) {
                System.out.println("result = " + result.get("LONG_COMP_NAME"));
            }*/
        }

        String sentence = "U.S. Hot Stocks: Santarus, AVG Tech, Gap, Lennar, Groupon";
        //String result1 = sentence.replaceAll("(?i)fox", "twitter <span class='abc'>$1$3</span>");
        //System.out.println("Input: " + sentence);
        //System.out.println("Output: " + result1);
       // System.out.println("Twitter all str IBM ".replaceAll(pattern1, "twitter <span class='abc'>$1$3</span>"));
        //StringUtils.replaceEach()

    }
}
