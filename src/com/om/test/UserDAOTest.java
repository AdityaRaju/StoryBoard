package com.om.test;

import com.om.context.ApplicationContext;
import com.om.dao.UserDAO;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class UserDAOTest {

    public static void main(String[] args) throws Exception {
        UserDAO userDAO = (UserDAO) ApplicationContext.getInstance().getBean("userDAO");

        Map<String,Object> userData = new HashMap<String, Object>();
        //HashMap<String,Object> connections = new HashMap<String, Object>();
        HashMap<String,Object> srcData = new HashMap<String, Object>();

        srcData.put("userid","test1");
        srcData.put("password","test2");

        //connections.put("twitter",srcData);

        //userData.put("connections",connections);

        String s = userDAO.insertRecord(srcData);

        System.out.println("Object ID = " + s);

    }
}
