/*
 * Copyright 2012. Bloomberg Finance L.P.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:  The above
 * copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package com.bloomberglp.blpapi.examples;

import com.bloomberglp.blpapi.*;
import com.om.context.ApplicationContext;
import com.om.dao.StockMetadataDAO;
import com.om.dao.StockPriceDAO;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleHistoryExample {

    public static void main(String[] args) throws Exception
    {
        SimpleHistoryExample example = new SimpleHistoryExample();
        example.run(args);
        System.out.println("Press ENTER to quit");
        //System.in.read();
    }

    private void run(String[] args) throws Exception
    {
        String serverHost = "10.8.8.1";
        int serverPort = 8194;

        SessionOptions sessionOptions = new SessionOptions();
        sessionOptions.setServerHost(serverHost);
        sessionOptions.setServerPort(serverPort);

        System.out.println("Connecting to " + serverHost + ":" + serverPort);
        Session session = new Session(sessionOptions);
        if (!session.start()) {
            System.err.println("Failed to start session.");
            return;
        }
        if (!session.openService("//blp/refdata")) {
            System.err.println("Failed to open //blp/refdata");
            return;
        }
        Service refDataService = session.getService("//blp/refdata");
        ApplicationContext context = ApplicationContext.getInstance();
        StockMetadataDAO stockMetadataDAO = (StockMetadataDAO) context.getBean("stockMetadataDAO");

        StockPriceDAO stockPriceDAO = (StockPriceDAO) context.getBean("stockPriceDAO");
        List<Map<String,Object>> allCompanies = stockMetadataDAO.getAllCompanies();
        for (Map<String, Object> company : allCompanies) {
            try{
                String source = company.get("PARSEKYABLE_DES_SOURCE").toString();
                if(StringUtils.isNotBlank(source)){
                    pullDataForSingleStock(company, source, session, refDataService,stockPriceDAO);
                }
            }catch(Exception e){
                System.out.println("e = " + e.getMessage()+" -- "+company);
            }
        }


    }

    private void pullDataForSingleStock(Map<String, Object> company, String s, Session session, Service refDataService, StockPriceDAO stockPriceDAO) throws Exception, InterruptedException, ParseException {
        Request request = refDataService.createRequest("HistoricalDataRequest");

        Element securities = request.getElement("securities");
        //securities.appendValue("IBM US Equity");
        //securities.appendValue("MSFT US Equity");
        //securities.appendValue("IBM US Equity");
        securities.appendValue(s);

        Element fields = request.getElement("fields");
        fields.appendValue("PX_LAST");
        fields.appendValue("OPEN");
        fields.appendValue("CUR_MKT_CAP");

        request.set("periodicityAdjustment", "ACTUAL");
        request.set("periodicitySelection", "DAILY");
        request.set("startDate", "20110101");
        request.set("endDate", "20131001");
        request.set("maxDataPoints", 1000);
        request.set("returnEids", true);

        System.out.println("Sending Request: " + request);
        session.sendRequest(request, null);

        while (true) {
            Event event = session.nextEvent();
            MessageIterator msgIter = event.messageIterator();
            while (msgIter.hasNext()) {
                Message msg = msgIter.next();

                Name name = msg.messageType();
                System.out.println("name = " + name.toString());
                if(name.toString().equalsIgnoreCase("HistoricalDataResponse")){
                    Element securityData = msg.getElement("securityData");

                    Element fieldData = securityData.getElement("fieldData");


                    int i = fieldData.numValues();
                    /**
                     * int numElements = address.numElements();
                     for (int i = 0; i < numElements; ++i) {
                     Element e = address.getElement(i);
                     System.out.println(e.name() +  =  + e.getValueAsString());
                     }
                     */
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    for(int j=0;j<i;++j){
                        Element fieldDataElem = fieldData.getValueAsElement(j);
                        //2013-10-01
                        String date = fieldDataElem.getElementAsString("date");
                        Date dateObj = sdf.parse(date);
                        double open = fieldDataElem.getElementAsFloat64("OPEN");
                        double currMarketCap = fieldDataElem.getElementAsFloat64("CUR_MKT_CAP");
                        double lastPrice = fieldDataElem.getElementAsFloat64("PX_LAST");
                        System.out.println(" --- "+ date +" ---- "+  open+" - "+" - "+currMarketCap+" - "+lastPrice);
                        Map<String,Object> data = new HashMap<String, Object>();
                        data.put("date",dateObj);
                        data.put("market_cap",currMarketCap);
                        data.put("last",lastPrice);
                       // data.put("open",date);
                        data.put("company_id",company.get("_id"));
                        data.put("ID_BB_GLOBAL",company.get("ID_BB_GLOBAL"));
                        stockPriceDAO.insertPriceRecord(data);
                    }
                    //System.out.println(" --- "+fieldData+" ---- "+ i);
                }

            }
            if (event.eventType() == Event.EventType.RESPONSE) {
                break;
            }
        }
    }
}
