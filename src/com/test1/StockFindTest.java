package com.test1;

import com.om.context.ApplicationContext;
import com.om.dao.StockMetadataDAO;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 10/1/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class StockFindTest {

    public static void main(String[] args) {
        StockMetadataDAO stockMetadataDAO = (StockMetadataDAO) ApplicationContext.getInstance().getBean("stockMetadataDAO");

        System.out.println("stockMetadataDAO.searchCompaniesBy(\"ibm\") = " + stockMetadataDAO.searchCompaniesByAny("inter"));
    }
}
