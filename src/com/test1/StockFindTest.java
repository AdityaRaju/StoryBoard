package com.test1;

import com.om.context.ApplicationContext;
import com.om.dao.StockMetadataDAO;
import com.om.dao.StockPriceDAO;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 10/1/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class StockFindTest {

    public static void main(String[] args) {
        ApplicationContext instance = ApplicationContext.getInstance();
        StockMetadataDAO stockMetadataDAO = (StockMetadataDAO) instance.getBean("stockMetadataDAO");

        StockPriceDAO stockPriceDAO = (StockPriceDAO)instance.getBean("stockPriceDAO");
        //stockPriceDAO.getCompanyHistoricalData("BBG000GZQ728");
        stockPriceDAO.getCompanyMarketCap("BBG000GZQ728");
        //System.out.println("stockMetadataDAO.searchCompaniesBy(\"ibm\") = " + stockMetadataDAO.searchCompaniesByAny("inter"));

    }
}
