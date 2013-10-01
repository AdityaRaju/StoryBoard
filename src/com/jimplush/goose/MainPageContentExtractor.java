/**
 * Licensed to Gravity.com under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  Gravity.com licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jimplush.goose;

import com.jimplush.goose.cleaners.DefaultDocumentCleaner;
import com.jimplush.goose.cleaners.DocumentCleaner;
import com.jimplush.goose.images.BestImageGuesser;
import com.jimplush.goose.images.ImageExtractor;
import com.jimplush.goose.network.AudioNotSupportedException;
import com.jimplush.goose.network.HtmlFetcher;
import com.jimplush.goose.network.MaxBytesException;
import com.jimplush.goose.network.NotHtmlException;
import com.jimplush.goose.outputformatters.DefaultOutputFormatter;
import com.jimplush.goose.outputformatters.OutputFormatter;
import com.jimplush.goose.texthelpers.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * User: jim plush
 * Date: 12/16/10
 * a lot of work in this class is based on Arc90's readability code that does content extraction in JS
 * I wasn't able to find a good server side codebase to acheive the same so I started with their base ideas and then
 * built additional metrics on top of it such as looking for clusters of english stopwords.
 * Gravity was doing 30+ million links per day with this codebase across a series of crawling servers for a project
 * and it held up well. Our current port is slightly different than this one but I'm working to align them so the goose
 * project gets the love as we continue to move forward.
 *
 * Cougar: God dammit, Mustang! This is Ghost Rider 117. This bogey is all over me. He's got missile lock on me. Do I have permission to fire?
 * Stinger: Do not fire until fired upon...
 */


public class MainPageContentExtractor {


  // PRIVATE PROPERTIES BELOW

  private static final Logger logger = LoggerFactory.getLogger(MainPageContentExtractor.class);

  private static final StringReplacement MOTLEY_REPLACEMENT = StringReplacement.compile("&#65533;", string.empty);

  private static final StringReplacement ESCAPED_FRAGMENT_REPLACEMENT = StringReplacement.compile("#!", "?_escaped_fragment_=");

  private static final ReplaceSequence TITLE_REPLACEMENTS = ReplaceSequence.create("&raquo;").append("»");
  private static final StringSplitter PIPE_SPLITTER = new StringSplitter("\\|");
  private static final StringSplitter DASH_SPLITTER = new StringSplitter(" - ");
  private static final StringSplitter ARROWS_SPLITTER = new StringSplitter("»");
  private static final StringSplitter COLON_SPLITTER = new StringSplitter(":");
  private static final StringSplitter SPACE_SPLITTER = new StringSplitter(" ");

  private static final Set<String> NO_STRINGS = new HashSet<String>(0);
  private static final String A_REL_TAG_SELECTOR = "a[rel=tag], a[href*=/tag/]";
    public static final String DISCOVER_YOURSELF = "Discover Yourself";


    /**
   * holds the configuration settings we want to use
   */
  private Configuration config;

  // sets the default cleaner class to prep the HTML for parsing
  private DocumentCleaner documentCleaner;
  // the MD5 of the URL we're currently parsing, used to references the images we download to the url so we
  // can more easily clean up resources when we're done with the page.
  private String linkhash;
  // once we have our topNode then we want to format that guy for output to the user
  private OutputFormatter outputFormatter;
  private ImageExtractor imageExtractor;


  /**
   * you can optionally pass in a configuration object here that will allow you to override the settings
   * that goose comes default with
   */
  public MainPageContentExtractor() {
    this.config = new Configuration();
  }

  /**
   * overloaded to accept a custom configuration object
   *
   * @param config
   */
  public MainPageContentExtractor(Configuration config) {
    this.config = config;
  }


  /**
   * @param urlToCrawl - The url you want to extract the text from
   * @param html       - if you already have the raw html handy you can pass it here to avoid a network call
   * @return
   */
  public Article extractContent(String urlToCrawl, String html) throws Exception {

    return performExtraction(urlToCrawl, html);


  }

  /**
   * @param urlToCrawl - The url you want to extract the text from, makes a network call
   * @return
   */
  public Article extractContent(String urlToCrawl) throws Exception {
    String html = null;
    return performExtraction(urlToCrawl, html);
  }

   static Set<String> ignoreDescList = new HashSet<String>();
    static Set<String> ignoreTitleExactList = new HashSet<String>();
    static Set<String> ignoreTitleMatchList = new HashSet<String>();
    static {
        ignoreDescList.add("If you have problems or questions with Slashdot");
        ignoreDescList.add("Upgrade to the latest Flash Player for improved playback performance");
        ignoreDescList.add("You can earn the Well Connected badge by linking your Facebook");
        ignoreDescList.add("The server encountered an internal error or misconfiguration and was unable to complete your request");
        ignoreDescList.add("By clicking the \"Like\" button you will ");
        ignoreDescList.add("Where not otherwise specified, this work is licensed under a Creative Commons License permitting non-commercial sharing with attribution. Boing Boing is a trademark of Happy Mutants LLC in the United States and other countries.");
        ignoreDescList.add("We understand you'd like to delete your account. If you delete your account all of your information including your comments");
        ignoreDescList.add("About.com is a valuable resource for content that helps people to solve the large");
        ignoreDescList.add("Your web browser must have JavaScript enabled in order for this application to");
        ignoreDescList.add("The page you are trying to reach cannot be found.");
        ignoreDescList.add("1. Scroll down to find the podcast you're looking for. All active podcasts can be found on this page");
        ignoreDescList.add("Based on your reading history you may be interested");
        ignoreDescList.add("Please click on the link inside the email to complete your registration");
        ignoreDescList.add("When you tweet with a location, Twitter stores that location. You can switch location on/off before each Tweet");
        ignoreDescList.add("Login to manage your products and services from The New York Times and the International Herald Tri");
        ignoreDescList.add("This website uses technologies not supported by this browser. For the best experience please upgrade your browser");

        ignoreTitleExactList.add("photo");
        ignoreTitleExactList.add("BBC News");
        ignoreTitleExactList.add("rdio");
        ignoreTitleExactList.add("500 Internal Server Error");
        ignoreTitleExactList.add("News Viewer");
        ignoreTitleExactList.add("America's Finest News Source");
        ignoreTitleExactList.add("News from The Associated Press");
        ignoreTitleExactList.add("The Verge");
        ignoreTitleExactList.add("The Verge");
        ignoreTitleExactList.add("A photo from @HBO");
        //NBCNews.com Video Player, Reuters gallery , Videos - NewsHour[startsWith], POLITICO Livestream,Discover Yourself!
        ignoreTitleExactList.add("NBCNews.com Video Player");
        ignoreTitleExactList.add("Reuters gallery");
        ignoreTitleExactList.add("POLITICO Livestream");
        ignoreTitleExactList.add("Discover Yourself!");
        ignoreTitleExactList.add("Whoops...");
        ignoreTitleExactList.add("Redirecting");
        ignoreTitleExactList.add("Nationwide Deals - LivingSocial");
        ignoreTitleExactList.add("Fox News");
        ignoreTitleExactList.add("The Economist - Economist Radio");
        ignoreTitleExactList.add("Are you looking for the new App.net");
        ignoreTitleExactList.add("Welcome to Examiner.com");
        ignoreTitleExactList.add("BB reader:");
        ignoreTitleExactList.add("Are you looking for the new App.net");
        ignoreTitleExactList.add("Welcome to Examiner.com");
        ignoreTitleExactList.add("Breaking News, World, US, DC News & Analysis");
        ignoreTitleExactList.add("CNN-IBN Videos: Watch Last 24hr News Videos");
        ignoreTitleExactList.add("Special News Show Videos");
        ignoreTitleExactList.add("Business, Financial & Economic News, Stock Quotes");
        ignoreTitleExactList.add("US News and World Report");
        ignoreTitleExactList.add("TODAY Video Player");
        ignoreTitleExactList.add("WordPress.com");





        ignoreTitleExactList.add("About.com: Do more.");

        ignoreTitleMatchList.add("real time stock quote for");
        ignoreTitleMatchList.add("Videos - NewsHour");
        ignoreTitleMatchList.add("Podcast and audio downloads for business and arts from Financial Times");

        //rdio
    }

    private boolean ignoreTitle(String title){


        for (String s : ignoreTitleExactList) {
            if(StringUtils.equalsIgnoreCase(title,s)){
                return true;
            }
        }

        for (String s : ignoreTitleMatchList) {
            if(StringUtils.startsWithIgnoreCase(title,s)){
                return true;
            }
        }
        if(StringUtils.startsWith(title, DISCOVER_YOURSELF) && StringUtils.length(title)== (DISCOVER_YOURSELF.length()+1)){
            return true;
        }

        return false ;
    }

  private boolean ignoreDesc(String desc){
      for (String s : ignoreDescList) {
          if(StringUtils.startsWithIgnoreCase(desc,s)){
              return true;
          }
      }


      return false ;
  }


    public Article getArticleDocument(String urlToCrawl) throws Exception {

        urlToCrawl = getUrlToCrawl(urlToCrawl);
        try {
            new URL(urlToCrawl);

            this.linkhash = HashUtils.md5(urlToCrawl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL Passed in: " + urlToCrawl, e);
        }

        ParseWrapper parseWrapper = new ParseWrapper();
        Article article = null;
        String rawHtml = null;
        try {

            if (rawHtml == null) {
                boolean exception = false;
                String lastRedirectURL = null;
                Map<String,String> htmlReturnResult = null;
                try{
                    htmlReturnResult = HtmlFetcher.getHtml(urlToCrawl);
                }catch (MaxBytesException mbe){
                    exception = true;
                }catch (NotHtmlException nbe){
                    exception = true;
                    lastRedirectURL = nbe.getRedirectURL();
                }
                if(exception && StringUtils.isNotBlank(lastRedirectURL)){
                    System.out.println("Trying a last redirect URL, in case it might work  ===> " + lastRedirectURL+" for -->"+urlToCrawl);
                    htmlReturnResult = HtmlFetcher.getHtml(lastRedirectURL);
                }
                rawHtml = htmlReturnResult.get(HtmlFetcher.RESULT_HTML);
                //rawHtml = HtmlFetcher.getHtml(urlToCrawl);
            }

            article = new Article();

            article.setRawHtml(rawHtml);

            Document doc = parseWrapper.parse(rawHtml, urlToCrawl);

            article.setDocument(doc);
            // before we cleanse, provide consumers with an opportunity to extract the publish date
            article.setPublishDate(config.getPublishDateExtractor().extract(doc));

            // now allow for any additional data to be extracted
            article.setAdditionalData(config.getAdditionalDataExtractor().extract(doc));

            // grab the text nodes of any <a ... rel="tag">Tag Name</a> elements
            article.setTags(extractTags(doc));

            // now perform a nice deep cleansing
            DocumentCleaner documentCleaner = getDocCleaner();
            doc = documentCleaner.clean(doc);

            String title = getTitle(doc);

            article.setTitle(StringEscapeUtils.unescapeHtml(title));
            if(ignoreTitle(article.getTitle())){
                article.setTitle("");
            }
            article.setMetaDescription(getMetaTagInfo(doc, "desc"));
            article.setMetaKeywords(getMetaKeywords(doc));
            article.setCanonicalLink(getCanonicalLink(doc, urlToCrawl));
            article.setDomain(article.getCanonicalLink());


            // extract the content of the article
            article.setTopNode(calculateBestNodeBasedOnClustering(doc));

            if (article.getTopNode() != null) {

                // extract any movie embeds out from our main article content
                article.setMovies(extractVideos(article.getTopNode()));


                if (config.isEnableImageFetching()) {
                    HttpClient httpClient = HtmlFetcher.getHttpClient();
                    imageExtractor = getImageExtractor(httpClient, urlToCrawl,article.getDomain());
                    article.setTopImage(imageExtractor.getBestImage(doc, article.getTopNode()));

                }

                //get all image candidates...
                getAllImageCandidates(article);

                // grab siblings and remove high link density elements
                cleanupNode(article.getTopNode());


                outputFormatter = getOutputFormatter();



                article.setCleanedArticleText(outputFormatter.getFormattedText(article.getTopNode()));

                if(ignoreDesc(article.getCleanedArticleText())){
                    article.setCleanedArticleText(null);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("FINAL EXTRACTION TEXT: \n" + article.getCleanedArticleText());
                }

                if (config.isEnableImageFetching()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("\n\nFINAL EXTRACTION IMAGE: \n" + article.getTopImage().getImageSrc());
                    }
                }

            }

            if(StringUtils.isBlank(article.getCleanedArticleText())){
                article.setCleanedArticleText(article.getMetaDescription());
            }

            /*if(article.getTopImage() == null){
                if (config.isEnableImageFetching()) {
                    HttpClient httpClient = HtmlFetcher.getHttpClient();
                    imageExtractor = getImageExtractor(httpClient, urlToCrawl,true,article.getDomain());
                    article.setTopImage(imageExtractor.getBestImage(doc, article.getTopNode()));

                }
            }




            if(article.getTopImage() != null){
                String s =  article.getTopImage().getImageSrc();
                if(StringUtils.isNotBlank(s)){
                    getImgDetails(article);
                }

            } */



            // cleans up all the temp images that we've downloaded
            releaseResources();


        }catch (MaxBytesException mbe){
            logger.error("NotHtmlException on url: " + urlToCrawl + " " + mbe.getMessage());
        }catch (NotHtmlException nbe){
            logger.error("NotHtmlException on url: " + urlToCrawl + " " + nbe.getMessage());
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error("General Exception occured on url: " + urlToCrawl + " " + e.toString());
            if(e instanceof AudioNotSupportedException){
                throw e;
            }
//      throw new RuntimeException(e);
        }


        return article;
    }


  public Article performExtraction(String urlToCrawl, String rawHtml) throws Exception {

    urlToCrawl = getUrlToCrawl(urlToCrawl);
    try {
      new URL(urlToCrawl);

      this.linkhash = HashUtils.md5(urlToCrawl);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid URL Passed in: " + urlToCrawl, e);
    }

    ParseWrapper parseWrapper = new ParseWrapper();
    Article article = null;
    try {

      if (rawHtml == null) {
          boolean exception = false;
          String lastRedirectURL = null;
          Map<String,String> htmlReturnResult = null;
          try{
          htmlReturnResult = HtmlFetcher.getHtml(urlToCrawl);
          }catch (MaxBytesException mbe){
              exception = true;
          }catch (NotHtmlException nbe){
              exception = true;
              lastRedirectURL = nbe.getRedirectURL();
          }
          if(exception && StringUtils.isNotBlank(lastRedirectURL)){
              System.out.println("Trying a last redirect URL, in case it might work  ===> " + lastRedirectURL+" for -->"+urlToCrawl);
              htmlReturnResult = HtmlFetcher.getHtml(lastRedirectURL);
          }
          rawHtml = htmlReturnResult.get(HtmlFetcher.RESULT_HTML);
        //rawHtml = HtmlFetcher.getHtml(urlToCrawl);
      }

      article = new Article();

      article.setRawHtml(rawHtml);

      Document doc = parseWrapper.parse(rawHtml, urlToCrawl);


      // before we cleanse, provide consumers with an opportunity to extract the publish date
      article.setPublishDate(config.getPublishDateExtractor().extract(doc));

      // now allow for any additional data to be extracted
      article.setAdditionalData(config.getAdditionalDataExtractor().extract(doc));

      // grab the text nodes of any <a ... rel="tag">Tag Name</a> elements
      article.setTags(extractTags(doc));

      // now perform a nice deep cleansing
      DocumentCleaner documentCleaner = getDocCleaner();
      doc = documentCleaner.clean(doc);

        String title = getTitle(doc);

        article.setTitle(StringEscapeUtils.unescapeHtml(title));
        if(ignoreTitle(article.getTitle())){
            article.setTitle("");
        }
      article.setMetaDescription(getMetaTagInfo(doc, "desc"));
      article.setMetaKeywords(getMetaKeywords(doc));
      article.setCanonicalLink(getCanonicalLink(doc, urlToCrawl));
      article.setDomain(article.getCanonicalLink());


      // extract the content of the article
      article.setTopNode(calculateBestNodeBasedOnClustering(doc));

      if (article.getTopNode() != null) {

        // extract any movie embeds out from our main article content
        article.setMovies(extractVideos(article.getTopNode()));


        if (config.isEnableImageFetching()) {
          HttpClient httpClient = HtmlFetcher.getHttpClient();
          imageExtractor = getImageExtractor(httpClient, urlToCrawl,article.getDomain());
          article.setTopImage(imageExtractor.getBestImage(doc, article.getTopNode()));

        }

        //get all image candidates...
        getAllImageCandidates(article);

        // grab siblings and remove high link density elements
        cleanupNode(article.getTopNode());


        outputFormatter = getOutputFormatter();



       article.setCleanedArticleText(outputFormatter.getFormattedText(article.getTopNode()));

          if(ignoreDesc(article.getCleanedArticleText())){
              article.setCleanedArticleText(null);
          }
          if (logger.isDebugEnabled()) {
          logger.debug("FINAL EXTRACTION TEXT: \n" + article.getCleanedArticleText());
        }

        if (config.isEnableImageFetching()) {
          if (logger.isDebugEnabled()) {
            logger.debug("\n\nFINAL EXTRACTION IMAGE: \n" + article.getTopImage().getImageSrc());
          }
        }

      }

        if(StringUtils.isBlank(article.getCleanedArticleText())){
            article.setCleanedArticleText(article.getMetaDescription());
        }

        if(article.getTopImage() == null){
            if (config.isEnableImageFetching()) {
                HttpClient httpClient = HtmlFetcher.getHttpClient();
                imageExtractor = getImageExtractor(httpClient, urlToCrawl,true,article.getDomain());
                article.setTopImage(imageExtractor.getBestImage(doc, article.getTopNode()));

            }
        }




        if(article.getTopImage() != null){
            String s =  article.getTopImage().getImageSrc();
            if(StringUtils.isNotBlank(s)){
                getImgDetails(article);
            }

        }



      // cleans up all the temp images that we've downloaded
      releaseResources();


    }catch (MaxBytesException mbe){
        logger.error("NotHtmlException on url: " + urlToCrawl + " " + mbe.getMessage());
        }catch (NotHtmlException nbe){
        logger.error("NotHtmlException on url: " + urlToCrawl + " " + nbe.getMessage());
        } catch (Exception e) {
        //e.printStackTrace();
      logger.error("General Exception occured on url: " + urlToCrawl + " " + e.toString());
        if(e instanceof AudioNotSupportedException){
            throw e;
        }
//      throw new RuntimeException(e);
    }


    return article;
  }

    private Article getImgDetails(Article article){
        BufferedImage bi = null;
        try {
        URL url = new URL(article.getTopImage().getImageSrc());



            bi = ImageIO.read(url);

        article.setImageWidth(bi.getWidth());
        article.setImageHeight(bi.getHeight());

        } catch (Exception e) {
            logger.error("Error in reading img for ---> "+article.getTopImage().getImageSrc()+" , "+e.getMessage());
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }finally {
            bi = null;
        }
        return article;
    }

    private  boolean getAllImageCandidates(Article article){
        try{
            //select all image nodes in topNode ....
            //logger.info("Checking all Image Candidates");
            //System.out.println("Checking image by using TopNode logic...");
            Elements imgNodes = article.getTopNode().select("img");
            if(imgNodes.size() > 0){
                for (Element imgNode : imgNodes) {
                    //TODO: I can get the width and height right here...but are they good ? some might have ...some might not...
                    String src = imgNode.attr("src");
                    if(ArticalUtil.isValidImage(src)){
                    article.getImageCandidates().add(ArticalUtil.buildImagePath(src, article.getDomain()));
                    }
                }
            }

        }catch (Exception e){
            return false;
        }
        return true;
    }



  private Set<String> extractTags(Element node) {
    if (node.children().size() == 0) return NO_STRINGS;

    Elements elements = Selector.select(A_REL_TAG_SELECTOR, node);
    if (elements.size() == 0) return NO_STRINGS;

    Set<String> tags = new HashSet<String>(elements.size());
    for (Element el : elements) {
      String tag = el.text();
      if (!string.isNullOrEmpty(tag)) tags.add(tag);
    }

    return tags;
  }

  // used for gawker type ajax sites with pound sites
  private String getUrlToCrawl(String urlToCrawl) {
    String finalURL;
    if (urlToCrawl.contains("#!")) {
      finalURL = ESCAPED_FRAGMENT_REPLACEMENT.replaceAll(urlToCrawl);
    } else {
      finalURL = urlToCrawl;
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Goose Extraction: " + finalURL);
    }

    return finalURL;
  }


  // todo create a setter for this for people to override output formatter
  private OutputFormatter getOutputFormatter() {
    if (outputFormatter == null) {
      return new DefaultOutputFormatter();
    } else {
      return outputFormatter;
    }

  }


  private ImageExtractor getImageExtractor(HttpClient httpClient, String urlToCrawl, String domain) {

    if (imageExtractor == null) {
      BestImageGuesser bestImageGuesser = new BestImageGuesser(this.config, httpClient, urlToCrawl,domain);
      return bestImageGuesser;
    } else {
      return imageExtractor;
    }

  }

    private ImageExtractor getImageExtractor(HttpClient httpClient, String urlToCrawl,boolean checkMetaTagImage,String domain) {

        if (imageExtractor == null) {
            BestImageGuesser bestImageGuesser = new BestImageGuesser(this.config, httpClient, checkMetaTagImage,domain);
            return bestImageGuesser;
        } else {
            return imageExtractor;
        }

    }

  /**
   * todo allow for setter to override the default documentCleaner in case user wants more flexibility
   *
   * @return
   */
  private DocumentCleaner getDocCleaner() {
    if (this.documentCleaner == null) {
      this.documentCleaner = new DefaultDocumentCleaner();
    }
    return this.documentCleaner;
  }

  /**
   * attemps to grab titles from the html pages, lots of sites use different delimiters
   * for titles so we'll try and do our best guess.
   *
   *
   * @param doc
   * @return
   */
  private String getTitle(Document doc) {
    String title = string.empty;
      title =  getMetaTagInfo(doc,"title");
      if(!StringUtils.isBlank(title)){
         return title;
      }
    try {

      Elements titleElem = doc.getElementsByTag("title");
      if (titleElem == null || titleElem.isEmpty()) return string.empty;

      String titleText = titleElem.first().text();

      if (string.isNullOrEmpty(titleText)) return string.empty;

      boolean usedDelimeter = false;

      if (titleText.contains("|")) {
        titleText = doTitleSplits(titleText, PIPE_SPLITTER);
        usedDelimeter = true;
      }

      if (!usedDelimeter && titleText.contains("-")) {
        titleText = doTitleSplits(titleText, DASH_SPLITTER);
        usedDelimeter = true;
      }
      if (!usedDelimeter && titleText.contains("»")) {
        titleText = doTitleSplits(titleText, ARROWS_SPLITTER);
        usedDelimeter = true;
      }

      if (!usedDelimeter && titleText.contains(":")) {
        titleText = doTitleSplits(titleText, COLON_SPLITTER);
      }

      // encode unicode charz
      title = StringEscapeUtils.escapeHtml(titleText);

      // todo this is a hack until I can fix this.. weird motely crue error with
      // http://money.cnn.com/2010/10/25/news/companies/motley_crue_bp.fortune/index.htm?section=money_latest
      title = MOTLEY_REPLACEMENT.replaceAll(title);

      if (logger.isDebugEnabled()) {
        logger.debug("Page title is: " + title);
      }

    } catch (NullPointerException e) {
      logger.error(e.toString());
    }
    return title;

  }

  /**
   * based on a delimeter in the title take the longest piece or do some custom logic based on the site
   *
   * @param title
   * @param splitter
   * @return
   */
  private String doTitleSplits(String title, StringSplitter splitter) {
    int largetTextLen = 0;
    int largeTextIndex = 0;

    String[] titlePieces = splitter.split(title);

    // take the largest split
    for (int i = 0; i < titlePieces.length; i++) {
      String current = titlePieces[i];
      if (current.length() > largetTextLen) {
        largetTextLen = current.length();
        largeTextIndex = i;
      }
    }

    return TITLE_REPLACEMENTS.replaceAll(titlePieces[largeTextIndex]).trim();
  }

  private String getMetaContent(Document doc, String metaName) {
    Elements meta = doc.select(metaName);
    if (meta.size() > 0) {
      String content = meta.first().attr("content");
      return string.isNullOrEmpty(content) ? string.empty : content.trim();
    }
    return string.empty;
  }

    static Map<String,List<String>> metaTagsMap = new HashMap<String, List<String>>();
    static{
        List<String> descSet = new ArrayList<String>();
        descSet.add("meta[name=description]");
        descSet.add("meta[property=og:description]");
        descSet.add("meta[itemprop=description]");
        descSet.add("meta[name~=description]");
        descSet.add("meta[name*=/description/]");
        metaTagsMap.put("desc",descSet) ;

        List<String> titleSet = new ArrayList<String>();

        titleSet.add("meta[property=og:title]");
        titleSet.add("meta[itemprop=name]");
        titleSet.add("meta[property~=title]") ;                     //
        titleSet.add("meta[name*=/title/]");
        titleSet.add("meta[name=title]");
        metaTagsMap.put("title",titleSet) ;

        List<String> keyWordsSet = new ArrayList<String>();
        keyWordsSet.add("meta[name=keywords]");
        keyWordsSet.add("meta[property=og:keywords]");
        keyWordsSet.add("meta[itemprop=keywords]");
        keyWordsSet.add("meta[property~=keywords]");
        keyWordsSet.add("meta[name*=/keywords/]");
        keyWordsSet.add("meta[property*=/keywords/]");
        metaTagsMap.put("keyword",keyWordsSet) ;

        //

    }

  /**
   * if the article has meta description set in the source, use that
   */
  private String getMetaDescription(Document doc) {

       for(String s:metaTagsMap.get("desc")){
           String content = getMetaContent(doc, s);
           if(StringUtils.isNotBlank(content)){
               return content;
           }
       }

      return "";

      /*String metaDesc = getMetaContent(doc, "meta[name=description]");

      return getMetaContent(doc, "meta[name=description]");*/
  }

    private String getMetaTagInfo(Document doc,String keyword) {

        for(String s:metaTagsMap.get(keyword)){
            String content = getMetaContent(doc, s);
            if(StringUtils.isNotBlank(content)){
                return content;
            }
        }

        return "";

        /*String metaDesc = getMetaContent(doc, "meta[name=description]");

      return getMetaContent(doc, "meta[name=description]");*/
    }

  /**
   * if the article has meta keywords set in the source, use that
   */
  private String getMetaKeywords(Document doc) {
    //return getMetaContent(doc, "meta[name*=/keywords/]");
      return getMetaTagInfo(doc,"keyword");
  }

  /**
   * if the article has meta canonical link set in the url
   */
  private String getCanonicalLink(Document doc, String baseUrl) {
    Elements meta = doc.select("link[rel=canonical]");
    if (meta.size() > 0) {
      String href = meta.first().attr("href");
      return string.isNullOrEmpty(href) ? string.empty : href.trim();
    } else {
      return baseUrl;
    }

/*    Not sure what this is for
    // set domain based on canonicalUrl
    URL url = null;
    try {

      if (canonicalUrl != null) {
        if (!canonicalUrl.startsWith("http://")) {
          url = new URL(new URL(baseUrl), canonicalUrl);
        } else {
          url = new URL(canonicalUrl);
        }

      } else {
        url = new URL(baseUrl);
      }

    } catch (MalformedURLException e) {
      logger.error(e.toString(), e);
    }*/
  }

  private String getDomain(String canonicalLink) {
    try {
      return new URL(canonicalLink).getHost();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * we're going to start looking for where the clusters of paragraphs are. We'll score a cluster based on the number of stopwords
   * and the number of consecutive paragraphs together, which should form the cluster of text that this node is around
   * also store on how high up the paragraphs are, comments are usually at the bottom and should get a lower score
   *
   * @return
   */
  private Element calculateBestNodeBasedOnClustering(Document doc) {
    Element topNode = null;

    // grab all the paragraph elements on the page to start to inspect the likely hood of them being good peeps
    ArrayList<Element> nodesToCheck = getNodesToCheck(doc);

    double startingBoost = 1.0;
    int cnt = 0;
    int i = 0;

    // holds all the parents of the nodes we're checking
    Set<Element> parentNodes = new HashSet<Element>();


    ArrayList<Element> nodesWithText = new ArrayList<Element>();


    for (Element node : nodesToCheck) {

      String nodeText = node.text();
      WordStats wordStats = StopWords.getStopWordCount(nodeText);
      boolean highLinkDensity = isHighLinkDensity(node);


      if (wordStats.getStopWordCount() > 2 && !highLinkDensity) {

        nodesWithText.add(node);
      }

    }

    int numberOfNodes = nodesWithText.size();
    int negativeScoring = 0; // we shouldn't give more negatives than positives
    // we want to give the last 20% of nodes negative scores in case they're comments
    double bottomNodesForNegativeScore = (float) numberOfNodes * 0.25;

    //if (logger.isDebugEnabled()) {
      logger.debug("About to inspect num of nodes with text: " + numberOfNodes);
    //}

    for (Element node : nodesWithText) {

      // add parents and grandparents to scoring
      // only add boost to the middle paragraphs, top and bottom is usually jankz city
      // so basically what we're doing is giving boost scores to paragraphs that appear higher up in the dom
      // and giving lower, even negative scores to those who appear lower which could be commenty stuff

      float boostScore = 0;

      if (isOkToBoost(node)) {
        if (cnt >= 0) {
          boostScore = (float) ((1.0 / startingBoost) * 50);
          startingBoost++;
        }
      }


      // check for negative node values
      if (numberOfNodes > 15) {
        if ((numberOfNodes - i) <= bottomNodesForNegativeScore) {
          float booster = (float) bottomNodesForNegativeScore - (float) (numberOfNodes - i);
          boostScore = -(float) Math.pow(booster, (float) 2);

          // we don't want to score too highly on the negative side.
          float negscore = Math.abs(boostScore) + negativeScoring;
          if (negscore > 40) {
            boostScore = 5;
          }
        }
      }


        logger.debug("Location Boost Score: " + boostScore + " on interation: " + i + "' id='" + node.parent().id() + "' class='" + node.parent().attr("class"));

      String nodeText = node.text();
      WordStats wordStats = StopWords.getStopWordCount(nodeText);
      int upscore = (int) (wordStats.getStopWordCount() + boostScore);
      updateScore(node.parent(), upscore);
      updateScore(node.parent().parent(), upscore / 2);
      updateNodeCount(node.parent(), 1);
      updateNodeCount(node.parent().parent(), 1);

      if (!parentNodes.contains(node.parent())) {
        parentNodes.add(node.parent());
      }

      if (!parentNodes.contains(node.parent().parent())) {
        parentNodes.add(node.parent().parent());
      }

      cnt++;
      i++;
    }


    // now let's find the parent node who scored the highest

    int topNodeScore = 0;
    for (Element e : parentNodes) {
        //System.out.println("ParentNode: score='" + e.attr("gravityScore") + "' nodeCount='" + e.attr("gravityNodes") + "' id='" + e.id() + "' class='" + e.attr("class") + "' ");
        if (logger.isDebugEnabled()) {
        logger.debug("ParentNode: score='" + e.attr("gravityScore") + "' nodeCount='" + e.attr("gravityNodes") + "' id='" + e.id() + "' class='" + e.attr("class") + "' ");
      }
      //int score = Integer.parseInt(e.attr("gravityScore")) * Integer.parseInt(e.attr("gravityNodes"));
      int score = getScore(e);
      if (score > topNodeScore) {
        topNode = e;
        topNodeScore = score;
      }

      if (topNode == null) {
        topNode = e;
      }
    }

    if (logger.isDebugEnabled()) {
      if (topNode == null) {
        logger.debug("ARTICLE NOT ABLE TO BE EXTRACTED!, WE HAZ FAILED YOU LORD VADAR");
      } else {
        String logText;
        String targetText = "";
        Element topPara = topNode.getElementsByTag("p").first();
        if (topPara == null) {
          topNode.text();
        } else {
          topPara.text();
        }

        if (targetText.length() >= 51) {
          logText = targetText.substring(0, 50);
        } else {
          logText = targetText;
        }
        logger.debug("TOPNODE TEXT: " + logText.trim());
        logger.debug("Our TOPNODE: score='" + topNode.attr("gravityScore") + "' nodeCount='" + topNode.attr("gravityNodes") + "' id='" + topNode.id() + "' class='" + topNode.attr("class") + "' ");
      }
    }


    return topNode;


  }

  /**
   * returns a list of nodes we want to search on like paragraphs and tables
   *
   * @return
   */
  private ArrayList<Element> getNodesToCheck(Document doc) {
    ArrayList<Element> nodesToCheck = new ArrayList<Element>();

    nodesToCheck.addAll(doc.getElementsByTag("a"));

    return nodesToCheck;

  }

  /**
   * checks the density of links within a node, is there not much text and most of it contains linky shit?
   * if so it's no good
   *
   * @param e
   * @return
   */
  private static boolean isHighLinkDensity(Element e) {

    Elements links = e.getElementsByTag("a");

    if (links.size() == 0) {
      return false;
    }

    String text = e.text().trim();
    String[] words = SPACE_SPLITTER.split(text);
    float numberOfWords = words.length;


    // let's loop through all the links and calculate the number of words that make up the links
    StringBuilder sb = new StringBuilder();
    for (Element link : links) {
      sb.append(link.text());
    }
    String linkText = sb.toString();
    String[] linkWords = SPACE_SPLITTER.split(linkText);
    float numberOfLinkWords = linkWords.length;

    float numberOfLinks = links.size();

    float linkDivisor = numberOfLinkWords / numberOfWords;
    float score = linkDivisor * numberOfLinks;

    if (logger.isDebugEnabled()) {
      String logText;
      if (e.text().length() >= 51) {
        logText = e.text().substring(0, 50);
      } else {
        logText = e.text();
      }
      logger.debug("Calulated link density score as: " + score + " for node: " + logText);
    }
    if (score > 1) {
      return true;
    }

    return false;
  }

  /**
   * alot of times the first paragraph might be the caption under an image so we'll want to make sure if we're going to
   * boost a parent node that it should be connected to other paragraphs, at least for the first n paragraphs
   * so we'll want to make sure that the next sibling is a paragraph and has at least some substatial weight to it
   *
   *
   * @param node
   * @return
   */
  private boolean isOkToBoost(Element node) {

    int stepsAway = 0;

    Element sibling = node.nextElementSibling();
    while (sibling != null) {

      if (sibling.tagName().equals("p")) {
        if (stepsAway >= 3) {
          if (logger.isDebugEnabled()) {
            logger.debug("Next paragraph is too far away, not boosting");
          }
          return false;
        }

        String paraText = sibling.text();
        WordStats wordStats = StopWords.getStopWordCount(paraText);
        if (wordStats.getStopWordCount() > 5) {
          if (logger.isDebugEnabled()) {
            logger.debug("We're gonna boost this node, seems contenty");
          }
          return true;
        }

      }

      // increase how far away the next paragraph is from this node
      stepsAway++;

      sibling = sibling.nextElementSibling();
    }


    return false;
  }


  /**
   * adds a score to the gravityScore Attribute we put on divs
   * we'll get the current score then add the score we're passing in to the current
   *
   * @param node
   * @param addToScore - the score to add to the node
   */
  private void updateScore(Element node, int addToScore) {
    int currentScore;
    try {
      String scoreString = node.attr("gravityScore");
      currentScore = string.isNullOrEmpty(scoreString) ? 0 : Integer.parseInt(scoreString);
    } catch (NumberFormatException e) {
      currentScore = 0;
    }
    int newScore = currentScore + addToScore;
    node.attr("gravityScore", Integer.toString(newScore));

  }

  /**
   * stores how many decent nodes are under a parent node
   *
   * @param node
   * @param addToCount
   */
  private void updateNodeCount(Element node, int addToCount) {
    int currentScore;
    try {
      String countString = node.attr("gravityNodes");
      currentScore = string.isNullOrEmpty(countString) ? 0 : Integer.parseInt(countString);
    } catch (NumberFormatException e) {
      currentScore = 0;
    }
    int newScore = currentScore + addToCount;
    node.attr("gravityNodes", Integer.toString(newScore));

  }


  /**
   * returns the gravityScore as an integer from this node
   *
   * @param node
   * @return
   */
  private int getScore(Element node) {
    if (node == null) return 0;
    try {
      String grvScoreString = node.attr("gravityScore");
      if (string.isNullOrEmpty(grvScoreString)) return 0;
      return Integer.parseInt(grvScoreString);
    } catch (NumberFormatException e) {
      return 0;
    }
  }


  /**
   * pulls out videos we like
   *
   * @return
   */
  private ArrayList<Element> extractVideos(Element node) {
    ArrayList<Element> candidates = new ArrayList<Element>();
    ArrayList<Element> goodMovies = new ArrayList<Element>();
    try {


      Elements embeds = node.parent().getElementsByTag("embed");
      for (Element el : embeds) {
        candidates.add(el);
      }
      Elements objects = node.parent().getElementsByTag("object");
      for (Element el : objects) {
        candidates.add(el);
      }
      if (logger.isDebugEnabled()) {
        logger.debug("extractVideos: Starting to extract videos. Found: " + candidates.size());
      }

      for (Element el : candidates) {

        Attributes attrs = el.attributes();

        for (Attribute a : attrs) {
          try {
            if (logger.isDebugEnabled()) {
              logger.debug(a.getKey() + " : " + a.getValue());
            }
            if ((a.getValue().contains("youtube") || a.getValue().contains("vimeo")) && a.getKey().equals("src")) {
              if (logger.isDebugEnabled()) {
                logger.debug("Found video... setting");
                logger.debug("This page has a video!: " + a.getValue());
              }
              goodMovies.add(el);

            }
          } catch (Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
          }
        }

      }
    } catch (NullPointerException e) {
      logger.error(e.toString(), e);
    } catch (Exception e) {
      logger.error(e.toString(), e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("extractVideos:  done looking videos");
    }
    return goodMovies;
  }


  /**
   * remove any divs that looks like non-content, clusters of links, or paras with no gusto
   *
   * @param node
   * @return
   */
  private Element cleanupNode(Element node) {
    if (logger.isDebugEnabled()) {
      logger.debug("Starting cleanup Node");
    }

    node = addSiblings(node);

    Elements nodes = node.children();
    for (Element e : nodes) {
      if (e.tagName().equals("p")) {
        continue;
      }
      if (logger.isDebugEnabled()) {
        logger.debug("CLEANUP  NODE: " + e.id() + " class: " + e.attr("class"));
      }
      boolean highLinkDensity = isHighLinkDensity(e);
      if (highLinkDensity) {
        if (logger.isDebugEnabled()) {
          logger.debug("REMOVING  NODE FOR LINK DENSITY: " + e.id() + " class: " + e.attr("class"));
        }
        e.remove();
        continue;
      }
      // now check for word density
      // grab all the paragraphs in the children and remove ones that are too small to matter
      Elements subParagraphs = e.getElementsByTag("p");


      for (Element p : subParagraphs) {
        if (p.text().length() < 25) {
          p.remove();
        }
      }

      // now that we've removed shorty paragraphs let's make sure to exclude any first paragraphs that don't have paras as
      // their next siblings to avoid getting img bylines
      // first let's remove any element that now doesn't have any p tags at all
      Elements subParagraphs2 = e.getElementsByTag("p");
      if (subParagraphs2.size() == 0 && !e.tagName().equals("td")) {
        if (logger.isDebugEnabled()) {
          logger.debug("Removing node because it doesn't have any paragraphs");
        }
        e.remove();
        continue;
      }

      //if this node has a decent enough gravityScore we should keep it as well, might be content
      int topNodeScore = getScore(node);
      int currentNodeScore = getScore(e);
      float thresholdScore = (float) (topNodeScore * .08);
      if (logger.isDebugEnabled()) {
        logger.debug("topNodeScore: " + topNodeScore + " currentNodeScore: " + currentNodeScore + " threshold: " + thresholdScore);
      }
      if (currentNodeScore < thresholdScore) {
        if (!e.tagName().equals("td")) {
          if (logger.isDebugEnabled()) {
            logger.debug("Removing node due to low threshold score");
          }
          e.remove();
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug("Not removing TD node");
          }
        }

        continue;
      }

    }

    return node;

  }


  /**
   * adds any siblings that may have a decent score to this node
   *
   * @param node
   * @return
   */
  private Element addSiblings(Element node) {
    if (logger.isDebugEnabled()) {
      logger.debug("Starting to add siblings");
    }
    int baselineScoreForSiblingParagraphs = getBaselineScoreForSiblings(node);

    Element currentSibling = node.previousElementSibling();
    while (currentSibling != null) {
      if (logger.isDebugEnabled()) {
        logger.debug("SIBLINGCHECK: " + debugNode(currentSibling));
      }

      if (currentSibling.tagName().equals("p")) {

        node.child(0).before(currentSibling.outerHtml());
        currentSibling = currentSibling.previousElementSibling();
        continue;
      }

      // check for a paraph embedded in a containing element
      int insertedSiblings = 0;
      Elements potentialParagraphs = currentSibling.getElementsByTag("p");
      if (potentialParagraphs.first() == null) {
        currentSibling = currentSibling.previousElementSibling();
        continue;
      }
      for (Element firstParagraph : potentialParagraphs) {
        WordStats wordStats = StopWords.getStopWordCount(firstParagraph.text());

        int paragraphScore = wordStats.getStopWordCount();

        if ((float) (baselineScoreForSiblingParagraphs * .30) < paragraphScore) {
          if (logger.isDebugEnabled()) {
            logger.debug("This node looks like a good sibling, adding it");
          }
          node.child(insertedSiblings).before("<p>" + firstParagraph.text() + "<p>");
          insertedSiblings++;
        }

      }

      currentSibling = currentSibling.previousElementSibling();
    }
    return node;


  }

  /**
   * we could have long articles that have tons of paragraphs so if we tried to calculate the base score against
   * the total text score of those paragraphs it would be unfair. So we need to normalize the score based on the average scoring
   * of the paragraphs within the top node. For example if our total score of 10 paragraphs was 1000 but each had an average value of
   * 100 then 100 should be our base.
   *
   * @param topNode
   * @return
   */
  private int getBaselineScoreForSiblings(Element topNode) {

    int base = 100000;

    int numberOfParagraphs = 0;
    int scoreOfParagraphs = 0;

    Elements nodesToCheck = topNode.getElementsByTag("p");

    for (Element node : nodesToCheck) {

      String nodeText = node.text();
      WordStats wordStats = StopWords.getStopWordCount(nodeText);
      boolean highLinkDensity = isHighLinkDensity(node);


      if (wordStats.getStopWordCount() > 2 && !highLinkDensity) {

        numberOfParagraphs++;
        scoreOfParagraphs += wordStats.getStopWordCount();
      }

    }

    if (numberOfParagraphs > 0) {
      base = scoreOfParagraphs / numberOfParagraphs;
      if (logger.isDebugEnabled()) {
        logger.debug("The base score for siblings to beat is: " + base + " NumOfParas: " + numberOfParagraphs + " scoreOfAll: " + scoreOfParagraphs);
      }
    }

    return base;


  }

  private String debugNode(Element e) {

    StringBuilder sb = new StringBuilder();
    sb.append("GravityScore: '");
    sb.append(e.attr("gravityScore"));
    sb.append("' paraNodeCount: '");
    sb.append(e.attr("gravityNodes"));
    sb.append("' nodeId: '");
    sb.append(e.id());
    sb.append("' className: '");
    sb.append(e.attr("class"));
    return sb.toString();

  }

  /**
   * cleans up any temp shit we have laying around like temp images
   * removes any image in the temp dir that starts with the linkhash of the url we just parsed
   */
  public void releaseResources() {
    if (logger.isDebugEnabled()) {
      logger.debug("STARTING TO RELEASE ALL RESOURCES");
    }
    File dir = new File(config.getLocalStoragePath());
    String[] children = dir.list();

    if (children == null) {
      logger.debug("No Temp images found for linkhash: " + this.linkhash);
    } else {
      for (int i = 0; i < children.length; i++) {
        // Get filename of file or directory
        String filename = children[i];

        if (filename.startsWith(this.linkhash)) {

          File f = new File(dir.getAbsolutePath() + "/" + filename);
          if (!f.delete()) {
            logger.error("Unable to remove temp file: " + filename);
          }
        }
      }
    }

  }


}
