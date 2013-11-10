package com.om.test;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 11/9/13
 * Time: 7:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test1 {
    public static void main(String[] args) {
        Map<String,String> testMap = new HashMap<String,String>();
        testMap.put("twitter","TWTR");
        String s ="Twitter all replace 1 ";

        Pattern p = Pattern.compile("[\\s+]");
        // Split input with the pattern
        StringBuilder sb = new StringBuilder();
        String[] result = p.split(s);
        for (int i = 0; i < result.length; i++) {
            String key = result[i];
            String val = testMap.get(key.toLowerCase());
            if(StringUtils.isNotBlank(val)){
                sb.append("<span class='news-ticker' n-ticker='" + val+
                        "'>").append(key).append("</span> ");
            }else{
                sb.append(key).append(" ");
            }
        }
        System.out.println("sb.toString() = " + sb.toString());
    }
}
