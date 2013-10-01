package com.om.test;

import com.om.context.ApplicationContext;
import com.om.dao.PortfolioDAO;
import com.om.util.DateTimeUtil;

import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/6/13
 * Time: 1:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class PortfolioDAOGetTest {
    public static void main(String[] args) throws Exception {

        PortfolioDAO portfolioDAO = (PortfolioDAO) ApplicationContext.getInstance().getBean("portfolioDAO");

        List<Map<String, Object>> allPortfolios = portfolioDAO.getAllPortfolios();
        for (Map<String, Object> portfolio : allPortfolios) {
            System.out.println(portfolio.get("name"));

        }
    }

}
