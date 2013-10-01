package com.jimplush.goose;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/24/13
 * Time: 6:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArticalUtil {

    private static final Logger logger = LoggerFactory.getLogger(ArticalUtil.class);


    public static String buildImagePath(String image,String domain) {

        String newImage = image.replace(" ", "%20");

        try {
            if(!StringUtils.startsWithIgnoreCase(image, "http:")){
                //pageURL = new URL(this.targetUrl);
                URL imageURL = new URL("http",domain, image);

                newImage = imageURL.toString();
            }
        } catch (MalformedURLException e) {
            logger.error("Unable to get Image Path --> " + image);
        }
        return newImage;

    }

    static Set<String> ignoreImgCandidates = new HashSet<String>();

    static{
        ignoreImgCandidates.add("facebookshare.png");
        ignoreImgCandidates.add("emailshare.png");
        ignoreImgCandidates.add("authorboxfade.png");
    }

    public static boolean isValidImage(String src){
        for(String s:ignoreImgCandidates){
            if(StringUtils.endsWith(src, s)){
                return false;
            }
        }
        return true;
    }

    public static String getFormattedDate(int addOrMinusFromCurrent){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

        return sd.format(getDate(addOrMinusFromCurrent));
    }

    public static Date getDate(int addOrMinusFromCurrent){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, addOrMinusFromCurrent);
        return c.getTime();
    }
}
