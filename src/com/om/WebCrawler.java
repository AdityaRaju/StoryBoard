package com.om;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 4/27/13
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */

import com.jimplush.goose.Article;
import com.jimplush.goose.Configuration;
import com.jimplush.goose.ContentExtractor;
import com.jimplush.goose.network.AudioNotSupportedException;
import com.om.dao.URLCrawlerDAO;
import com.om.logger.ArticleLogger;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 6/13/12
 * Time: 7:07 AM
 * To change this template use.
 */
@Component
public class WebCrawler {

    public static final String ERROR = "error";
    @Autowired
    ArticleLogger articleLogger;

    @Autowired
    URLCrawlerDAO urlCrawlerDAO;

    //Map<String, String> cachedURLs = new HashMap<String, String>();
    private static final String SEPERATOR = " %&@ ";
    public static final String PAGE_NOT_FOUND = "Page Not Found";
    public static final String CRAWLER_ERROR = "{error:";




    public Article crawlURL(String url) {
        System.out.println("crawling for url = " + url);
        //ApplicationLogger.log("crawling for url = " + url);
        Article article = null;
        try {
            article = getArticle(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return article;

    }







        public List<Map<String, Object>> crawlAllCached( String mainWebSiteURL)
    {
        return urlCrawlerDAO.findAllBy(mainWebSiteURL);
    }

    public Map<String, Object> crawlURL(String url,String mainWebSiteURL,String title) {
        Map<String, Object> dbObj = urlCrawlerDAO.find(url);
        //String json = cachedURLs.get(url);
        //System.out.println(url+"json = " + json);
        //if (StringUtils.equalsIgnoreCase(json, ERROR_DATA)){
        if (dbObj == null ){
            if (StringUtils.isNotBlank(url)) {
                try {
                    //http://t.co/652ae1aa
                    System.out.println("crawling for url = " + url);
                    //ApplicationLogger.log("crawling for url = " + url);
                    Article article = getArticle(url);
                    if(article != null){
                    Map<String, Object> retObj = new HashMap<String, Object>();
                    retObj.put("t", article.getTitle());
                    if (article.getTopImage() != null) {
                        if(StringUtils.isNotBlank(article.getTopImage().getImageSrc())){
                            retObj.put("i", article.getTopImage().getImageSrc());
                            retObj.put("iw", String.valueOf(article.getImageWidth()));
                            retObj.put("ih", String.valueOf(article.getImageHeight()));
                        }

                    }
                    if(org.apache.commons.lang.StringUtils.isNotBlank((String) retObj.get("ih"))){
                        int height = Integer.parseInt( (String)retObj.get("ih"));
                        if(height > 150){
                            retObj.put("ih",150);
                        }
                    }

                    if(org.apache.commons.lang.StringUtils.isNotBlank((String) retObj.get("i"))){
                        retObj.put("iExist","T");
                    }else{
                        retObj.put("iExist","F");
                    }
                    String desc = (String)retObj.get("d");

                    if(org.apache.commons.lang.StringUtils.startsWithIgnoreCase(desc, "Log in to manage your products")){
                        desc="";
                        retObj.put("d",desc);
                    }
                    /*String cleanedArticleText = article.getCleanedArticleText();
                    if(StringUtils.length(cleanedArticleText)>500){
                        cleanedArticleText = new StringBuilder().append(cleanedArticleText.substring(0, 490)).append("...").toString();
                    }*/
                    retObj.put("d", article.getCleanedArticleText());
                    retObj.put("u", url);
                    retObj.put("mKeywords",article.getMetaKeywords());
                    retObj.put("mDesc",article.getMetaDescription());
                    retObj.put("tags", StringUtils.join(article.getTags(),","));
                    retObj.put("oImgs", StringUtils.join(article.getImageCandidates(),","));
                    retObj.put("dt", new Date());
                    retObj.put("src",mainWebSiteURL);
                    retObj.put("title",title);
                        retObj.put("href",url);

                    if(retObj != null && retObj.get("i") != null){
                        Map<String,Map<String,Object>> sizesMap = new HashMap<String, Map<String,Object>>();
                        Map<String,Object> mediaSizesMap = new HashMap<String, Object>();
                        mediaSizesMap.put("w",retObj.get("iw"));
                        mediaSizesMap.put("h",retObj.get("ih"));
                        mediaSizesMap.put("resize","fit");
                        sizesMap.put("small",mediaSizesMap);

                        Map<String,Object> medaiInfo = new HashMap<String, Object>();
                        medaiInfo.put("type","P");
                        medaiInfo.put("media_url",(String)retObj.get("i"));
                        medaiInfo.put("sizes",sizesMap);
                        medaiInfo.put("aimg",true);
                        retObj.put("medaiInfo",medaiInfo);
                        //map.put("thumbnail_url",crawledObj.get("i"));
                    }

                    //http://s.wsj.net/img/MW_profile_lg.gif



                    String responseMap = new JSONObject(retObj).toString();
                    // ignoring Page Not found articles....
                    if(StringUtils.startsWithIgnoreCase(article.getTitle(), PAGE_NOT_FOUND)){
                        return null;
                    }
                    articleLogger.log(new StringBuffer(url).append(SEPERATOR).append(responseMap).toString());
                    articleLogger.insertArticleLog(retObj);
                    //cachedURLs.put(url, responseMap);
                    return retObj;
                    }else{
                        return null;
                    }
                    //System.out.println("article = " + article.getTitle());
                    //System.out.println("article = " + article.getTopImage().getImageSrc());
                    //System.out.println("article.getCleanedArticleText() = " + article.getCleanedArticleText());
                } catch (Exception e) {
                    if(e instanceof AudioNotSupportedException){


                        articleLogger.log(new StringBuffer(url).append(SEPERATOR).append(CRAWLER_ERROR + "\"").append("Audio file, work it later..").append("\"}").toString());
                        Map<String,Object> errorMap = new HashMap<String, Object>();
                        errorMap.put("u",url);
                        errorMap.put(ERROR,"Audio file, work it later..");
                        articleLogger.insertArticleLog(errorMap);
                    }
                    e.printStackTrace();
                    System.err.println("Exception for ->" + url + "---e.getMessage() = " + e.getMessage());
                    //if(StringUtils.isNotBlank(e.getMessage())){
                    articleLogger.log(new StringBuffer(url).append(SEPERATOR).append(CRAWLER_ERROR + "\"").append(e.getMessage()).append("\"}").toString());
                    Map<String,Object> errorMap = new HashMap<String, Object>();
                    errorMap.put("u",url);
                    errorMap.put(ERROR,e.getMessage());
                    articleLogger.insertArticleLog(errorMap);
                    //}
                    //TODO: for now ignoring all the error data article url's
                    //in fear of if I call, same URL which is error, they might block me ???...:-((
                    //cachedURLs.put(url, ERROR_DATA);
                }
            }
        } else {
            //String json = cachedURLs.get(url);
            if(StringUtils.isNotBlank((String) dbObj.get(ERROR))){
                return null;
            }else{
                return dbObj;
            }
            /*if (StringUtils.equalsIgnoreCase(json, ERROR_DATA)) {
                return null;
            } else if(StringUtils.containsIgnoreCase(json,CRAWLER_ERROR)){
                  return null;
            }
            else {
                return gson.fromJson(json, mapType);
            }*/
        }

        return null;

    }

    public Article getArticle(String url)  throws Exception {
        return getArticle(url, true);
    }

    private Article getArticle(String url, boolean fetchImages) throws Exception  {
        return getArticle(url, fetchImages ? DEFAULT_CONFIG : NO_IMAGE_CONFIG);
    }

    private Article getArticle(String url, Configuration config)  throws Exception {
        ContentExtractor extractor = new ContentExtractor(config);
        return extractor.extractContent(url);
    }

    private static final Configuration DEFAULT_CONFIG = new Configuration();
    private static final Configuration NO_IMAGE_CONFIG = new Configuration();

    static {
        NO_IMAGE_CONFIG.setEnableImageFetching(true);
    }
}

