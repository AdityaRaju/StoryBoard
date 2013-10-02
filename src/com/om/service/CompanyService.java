package com.om.service;

import com.google.gson.Gson;
import com.om.dao.StockMetadataDAO;
import com.om.util.DateTimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/6/13
 * Time: 6:51 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/company")
public class CompanyService {



    @Autowired
    StockMetadataDAO stockMetadataDAO;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    public String search(@RequestParam("q") String data,@Context HttpServletRequest servletRequest) throws Exception {

        System.out.println("q = " + data+" , "+servletRequest.getParameter("q"));

        List<Map<String, Object>> records = null;
        if(StringUtils.equalsIgnoreCase(servletRequest.getParameter("m"),"true")){
            records = stockMetadataDAO.searchCompaniesBy("ID_BB_GLOBAL",servletRequest.getParameter("q"));

        }else{
            records = stockMetadataDAO.searchCompaniesByAny(servletRequest.getParameter("q"));
        }
         Map<String,Object> dataObj = new HashMap<String, Object>();
        dataObj.put("data", records);

        Gson gson = new Gson();
        return gson.toJson(dataObj);
    }








}
