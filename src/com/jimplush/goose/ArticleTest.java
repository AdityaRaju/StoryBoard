package com.jimplush.goose;

import junit.framework.TestCase;


/**
 * User: jim
 * Date: 12/16/10
 */

public class ArticleTest extends TestCase {


  public void testArticle()
  {
    Article article = new Article();
    article.setTitle("This is a title");
    assertEquals("This is a title", article.getTitle());
  }


  public void testSettingDomainOnArticle() {

    Article article = new Article();
    article.setDomain("http://us.rd.yahoo.com/finance/external/xwscheats/rss/SIG=131g8ln0p/*http://wallstcheatsheet.com/stocks/apple-heres-everything-that-mattered-from-wwdc-day-one.html/?ref=YF");
    //assertEquals("grapevinyl.com", article.getDomain());
      System.out.println("article = " + article.getTitle());
    //article.setDomain("http://www.economist.com/v/84/magnetic-morning/getting-nowhere");
    //assertEquals("www.economist.com", article.getDomain());
  }


}
