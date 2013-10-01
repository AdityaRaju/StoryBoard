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
package com.jimplush.goose.network;


import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.*;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * User: jim plush
 * Date: 12/16/10
 */


public class HtmlFetcher {

    private static final Logger logger = LoggerFactory.getLogger(HtmlFetcher.class);

    /**
     * holds a reference to our override cookie store, we don't want to store
     * cookies for head requests, only slows shit down
     */
    public static CookieStore emptyCookieStore;


    /**
     * holds the HttpClient object for making requests
     */
    private static HttpClient httpClient;
    public static final String USER_AGENT_STR = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:13.0) Gecko/20100101 Firefox/13.0.1";
    public static final String GZIP = "gzip";
    public static final String RESULT_HTML = "RESULT_HTML";
    public static final String LAST_REDIRECT_URI = "LAST_REDIRECT_URI";


    static {
        initClient();
    }


    public static HttpClient getHttpClient() {
        return httpClient;
    }

    private static int refreshCount = 0;

    /**
     * makes an http fetch to go retreive the HTML from a url, store it to disk and pass it off
     *
     * @param url
     * @return
     * @throws MaxBytesException
     * @throws NotHtmlException
     */
    public static Map<String, String> getHtml(String url) throws Exception, NotHtmlException {
        HttpGet httpget = null;
        Map<String, String> htmlResultMap = new HashMap<String, String>();
        String htmlResult = null;
        HttpEntity entity = null;
        InputStream instream = null;
        try {
            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, HtmlFetcher.emptyCookieStore);
            localContext.setAttribute(CoreProtocolPNames.USER_AGENT, USER_AGENT_STR);
            /* SSLSocketFactory sf = new SSLSocketFactory(
                    SSLContext.getInstance("TLS"),
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Scheme sch = new Scheme("https", 443, sf);
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);*/

            httpget = new HttpGet(url);
            httpget.setHeader("User-Agent", USER_AGENT_STR);
            // ******************** IMP **********************//
            /**
             * If we don't pass referer, we are getting a redirect url...meta tag refresh....
             * another thing is, we can pass any referer here...
             */
            httpget.setHeader("Referer", "http://www.google.com/url?sa=t&rct=j&q=&source=web&url=" + url);
            //httpget.setHeader("Referer", "http://www.snews.com");

            HttpResponse response = httpClient.execute(httpget, localContext);
            RedirectLocations redirectLocations = (RedirectLocations) localContext.getAttribute("http.protocol.redirect-locations");
            if(redirectLocations != null){
            int size = redirectLocations.getAll().size();
                if (size > 0) {
                    URI lastRedirectURI = redirectLocations.getAll().get(size - 1);
                    htmlResultMap.put(LAST_REDIRECT_URI, lastRedirectURI.toString());
                }
            } else{
                htmlResultMap.put(LAST_REDIRECT_URI, httpget.getURI().toString());
            }
            entity = response.getEntity();
            //TODO: Local context has all the info...check for images and handle them properly..


            if (entity != null) {
                instream = entity.getContent();

                // set the encoding type if utf-8 or otherwise
                String encodingType = "UTF-8";
                try {

                    //todo encoding detection could be improved
                    encodingType = EntityUtils.getContentCharSet(entity);

                    if (encodingType == null) {
                        encodingType = "UTF-8";
                    }
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Unable to get charset for: " + url);
                        logger.debug("Encoding Type is: " + encodingType);
                    }
                }

                //check whether it is a gzip stream
                boolean isGZipStream = false;
                if (response.getHeaders("Content-Encoding") != null && response.getHeaders("Content-Encoding").length > 0 && response.getHeaders("Content-Encoding")[0] != null) {
                    if (StringUtils.equalsIgnoreCase(response.getHeaders("Content-Encoding")[0].getValue(), GZIP)) {
                        isGZipStream = true;
                    }
                }

                Header[] contentTypeHeaders = response.getHeaders("Content-Type");
                if (contentTypeHeaders.length > 0) {

                    if (StringUtils.startsWithIgnoreCase(contentTypeHeaders[0].getValue(), "audio")) {
                        throw new AudioNotSupportedException();
                    }
                }

                try {
                    if (!isGZipStream) {
                        htmlResult = HtmlFetcher.convertStreamToString(instream, 15728640, encodingType).trim();
                    } else {
                        htmlResult = HtmlFetcher.convertGZipStreamToString(instream, 15728640, encodingType).trim();
                    }

                } finally {
                    EntityUtils.consume(entity);
                    //entity.consumeContent();
                }


            } else {
                logger.error("Unable to fetch URL Properly: " + url);
            }

        } catch (NullPointerException e) {
            // e.printStackTrace();
            logger.warn(e.toString() + " " + e.getMessage());

        } catch (MaxBytesException e) {

            logger.error("GRVBIGFAIL: " + url + " Reached max bytes size");
            throw e;
        } catch (SocketException e) {
            logger.warn(e.getMessage());

        } catch (SocketTimeoutException e) {
            logger.error(e.toString());
        } catch (Exception e) {
            //e.printStackTrace();;
            System.out.println("Unknown FAILURE FOR LINK: " + url + " " + e.toString());
            logger.error("Unknown FAILURE FOR LINK: " + url + " " + e.toString());
            if (e instanceof AudioNotSupportedException) {
                //abort the get..other wise the stream close method call down is taking hell lof ot time..
                try {
                    httpget.abort();
                } catch (Exception e1) {

                }
                throw e;
            }
            return null;
        } finally {

            if (instream != null) {
                try {
                    instream.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                }
            }
            if (httpget != null) {
                try {
                    httpget.abort();
                    entity = null;
                } catch (Exception e) {

                }
            }

        }

        if (logger.isDebugEnabled()) {
            logger.debug("starting...");
        }
        if (htmlResult == null || htmlResult.length() < 1) {
            if (logger.isDebugEnabled()) {
                logger.debug("HTMLRESULT is empty or null");
            }
            throw new NotHtmlException(htmlResultMap.get(LAST_REDIRECT_URI));
        }


        InputStream is;
        String mimeType = null;
        try {
            is = new ByteArrayInputStream(htmlResult.getBytes("UTF-8"));

            mimeType = URLConnection.guessContentTypeFromStream(is);
            //mimeType = "text/html";
            if (mimeType != null) {

                if (mimeType.equals("text/html") == true || mimeType.equals("application/xml") == true) {
                    htmlResultMap.put(RESULT_HTML, htmlResult);
                    return htmlResultMap;
                } else {
                    if (htmlResult.contains("<title>") == true && htmlResult.contains("<p>") == true) {
                        htmlResultMap.put(RESULT_HTML, htmlResult);
                        return htmlResultMap;
                    }


                    logger.error("GRVBIGFAIL: " + mimeType + " - " + url);
                    throw new NotHtmlException();
                }

            } else {

                Elements select = Selector.select("META[http-equiv=Content-Type]", Jsoup.parse(htmlResult));
                if (select.size() > 0) {
                    //this has a meta tag of refresh....do the refresh....
                    String content = select.attr("content").toLowerCase();
                    boolean isValid = StringUtils.contains(content, "text/html") || StringUtils.contains(content, "application/xml");
                    System.out.println("http-equiv=Content-Type found , sending content or/not = " + isValid + " , content came as:" + content);
                    if (isValid) {
                        htmlResultMap.put(RESULT_HTML, htmlResult);
                        return htmlResultMap;
                    }

                }

                //check for Selector.select(htmlResult,"META[URL=^http:]")
                if (refreshCount < 3) {
                    select = Selector.select("META[http-equiv=refresh]", Jsoup.parse(htmlResult));
                    if (select.size() > 0) {
                        //this has a meta tag of refresh....do the refresh....
                        String content = select.attr("content").toLowerCase();
                        String urlToTryAgain = StringUtils.substringAfter(content, "url=");
                        System.out.println("META[http-equiv=refresh] found , Trying this URL again[" + refreshCount + "] = " + urlToTryAgain);
                        refreshCount++;
                        return getHtml(urlToTryAgain);

                    }
                }

                throw new NotHtmlException();
            }


        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage());

        } catch (IOException e) {
            logger.warn(e.getMessage());
        } finally {
            refreshCount = 0;
        }

        htmlResultMap.put(RESULT_HTML, htmlResult);
        return htmlResultMap;
    }


    private static void initClient() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing HttpClient");
        }
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10 * 1000);
        ConnManagerParams.setMaxTotalConnections(httpParams, 20000);

        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRoute() {
            public int getMaxForRoute(HttpRoute route) {
                return 500;
            }
        });

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);


        /**
         * we don't want anything to do with cookies at this time
         */
        emptyCookieStore = new CookieStore() {

            public void addCookie(Cookie cookie) {

            }

            ArrayList<Cookie> emptyList = new ArrayList<Cookie>();

            public List<Cookie> getCookies() {
                return emptyList;
            }

            public boolean clearExpired(Date date) {
                return false;
            }

            public void clear() {

            }
        };

        // set request params
        httpParams.setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
        httpParams.setParameter("http.User-Agent", USER_AGENT_STR);
        httpParams.setParameter("http.language.Accept-Language", "en-us");
        httpParams.setParameter("http.protocol.content-charset", "UTF-8");
        httpParams.setParameter("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        httpParams.setParameter("Cache-Control", "max-age=0");
        httpParams.setParameter("http.connection.stalecheck", false); // turn off stale check checking for performance reasons


        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        final ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

        httpClient = new DefaultHttpClient(cm, httpParams);

        httpClient.getParams().setParameter("http.conn-manager.timeout", 120000L);
        httpClient.getParams().setParameter("http.protocol.wait-for-continue", 10000L);
        httpClient.getParams().setParameter("http.tcp.nodelay", true);
    }

    /**
     * reads bytes off the string and returns a string
     *
     * @param is
     * @param maxBytes The max bytes that we want to read from the input stream
     * @return String
     */
    public static String convertStreamToString(InputStream is, int maxBytes, String encodingType) throws MaxBytesException {

        char[] buf = new char[2048];
        Reader r = null;
        try {
            r = new InputStreamReader(is, encodingType);
            StringBuilder s = new StringBuilder();
            int bytesRead = 2048;
            while (true) {

                if (bytesRead >= maxBytes) {
                    throw new MaxBytesException();
                }

                int n = r.read(buf);
                bytesRead += 2048;
                if (n < 0)
                    break;
                s.append(buf, 0, n);
            }


            return s.toString();

        } catch (SocketTimeoutException e) {
            logger.warn(e.toString() + " " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.toString() + " Encoding: " + encodingType);

        } catch (IOException e) {
            logger.warn(e.toString() + " " + e.getMessage());
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (Exception e) {
                }
            }
        }
        return null;

    }

    public static String convertGZipStreamToString(InputStream is, int maxBytes, String encodingType) throws MaxBytesException {

        //ByteArrayInputStream bais = new ByteArrayInputStream(is);
        GZIPInputStream gzis = null;
        try {
            gzis = new GZIPInputStream(is);

            InputStreamReader reader = new InputStreamReader(gzis);
            BufferedReader in = new BufferedReader(reader);

            StringBuilder readBuilder = new StringBuilder();
            String readed;
            while ((readed = in.readLine()) != null) {
                readBuilder.append(readed);
            }
            return readBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;

    }
}
