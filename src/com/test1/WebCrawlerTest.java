package com.test1;

import com.jimplush.goose.cleaners.DefaultDocumentCleaner;
import com.jimplush.goose.outputformatters.DefaultOutputFormatter;
import com.om.WebCrawler;
import com.om.context.ApplicationContext;
import com.om.dao.StockMetadataDAO;
import com.om.dao.StockPriceDAO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 10/1/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class WebCrawlerTest {
    static String html = "<div class=\"page\" style=\"font-family: Georgia, Palatino, Times, 'Times New Roman', serif; font-size: 18px; line-height: 28px;\"><div class=\"page-number\"><button type=\"button\" class=\"btn btn-info\">Get Estimates</button></div><h3 class=\"title\">Groupon, Priceline rally after results; Twitter drops</h3><div>\n" +
            "                <p>\n" +
            "                    <a href=\"/Search?m=Column&amp;mp=Tech%20Stocks\">Tech Stocks</a>\n" +
            "                <span>\n" +
            "                      <a href=\"/Search?m=Column&amp;mp=Tech%20Stocks\">Archives</a>\n" +
            "                      <span>|</span>\n" +
            "                      <a href=\"/tools/alerts/newsColumn.asp?selectedType=3&amp;Column=Tech+Stocks&amp;chkProduct2=0\">Email alerts</a>\n" +
            "                </span>\n" +
            "                </p>\n" +
            "                <p>\n" +
            "                    Nov. 8, 2013, 4:19 p.m. EST\n" +
            "                </p>\n" +
            "\n" +
            "\n" +
            "            </div>\n" +
            "            <span content=\"http://www.marketwatch.com/story/groupon-priceline-rally-after-results-twitter-drops-2013-11-08\" itemprop=\"permalink\"></span>\n" +
            "            <!-- Methode filePath: \"/WSJ30/MW/Live/2013/11/08/Stories/tech stocks 11 08.xml\" -->\n" +
            "\n" +
            "\n" +
            "            <p style=\"font-weight: bold;\">\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                By <a href=\"mailto:rcrum@marketwatch.com\">Rex Crum</a>, MarketWatch\n" +
            "\n" +
            "\n" +
            "            </p>\n" +
            "            <p class=\"clear\" style=\"font-weight: bold;\">\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                SAN FRANCISCO (MarketWatch) — Tech stocks rallied Friday, with <span><a data-ls-seen=\"1\" href=\"http://www.marketwatch.com/companies/Groupon?lc=int_mb_1001\">Groupon</a></span> Inc. and Priceline.com Inc. among the top gainers due to enthusiasm over those companies’ upbeat  quarterly earnings reports.\n" +
            "\n" +
            "            </p>\n" +
            "            <p>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                However, Twitter                 <span>\n" +
            "                    <span>\n" +
            "                        <a href=\"http://www.marketwatch.com/investing/stock/TWTR\">TWTR</a>\n" +
            "                            <span>-7.24%</span>\n" +
            "                    </span>\n" +
            "                </span>\n" +
            "                &nbsp;failed to join in the sector’s rally one day after it made its debut as a publicly traded company.\n" +
            "\n" +
            "            </p>\n" +
            "            <p>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                Groupon                 <span>\n" +
            "                    <span>\n" +
            "                        <a href=\"http://www.marketwatch.com/investing/stock/GRPN\">GRPN</a>\n" +
            "                            <span>+6.43%</span>\n" +
            "                    </span>\n" +
            "                </span>\n" +
            "                &nbsp;rose more than 6% to close at $10.11 following the company’s third-quarter earnings report late Thursday. Groupon said it lost $2.58 million, to break even on a per-share basis, on revenue of $595.1 million, compared to a net loss of $2.98 million, or break-even on a per-share basis, on $568.6 million in sales in the same period a year ago.\n" +
            "\n" +
            "            </p>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "            <p>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                Excluding one-time items, Groupon would have earned 2 cents a share. Analysts surveyed by FactSet had forecast Groupon to earn a penny a share on revenue of $615.7 million. Gene Munster, who covers Groupon for Piper Jaffray, said that while the company’s sales were below Wall Street’s forecasts, “by Groupon standards, they were acceptable and in general a sign of stability.”\n" +
            "\n" +
            "            </p>\n" +
            "            <p>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                Online travel site Priceline.com Inc.                 <span>\n" +
            "                    <span>\n" +
            "                        <a href=\"http://www.marketwatch.com/investing/stock/PCLN\">PCLN</a>\n" +
            "                            <span>+4.92%</span>\n" +
            "                    </span>\n" +
            "                </span>\n" +
            "                &nbsp;ended the day with a gain of nearly 5%, to close at $1,073.20 a share a day after it reported better-than-expected third-quarter results. Priceline said it earned $17.30 a share, excluding one-time items, on revenue of $2.27 billion, up from a profit of $12.40 a share on $1.71 billion in sales in the year-ago period. Analysts had forecast Priceline to earn $16.15 a share on $2.22 billion in revenue.\n" +
            "\n" +
            "            </p>\n" +
            "            <p>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                Priceline said longtime Chief Executive Jeffery Boyd would step down, effective Jan. 1, and Darren Huston, currently CEO of Priceline’s Booking.com business would become CEO of Priceline. Boyd will remain as chairman of Priceline’s board of directors.\n" +
            "\n" +
            "            </p>\n" +
            "            <p>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                Twitter shares fell 7.2% to close at $41.65, <a href=\"/story/twitter-volatile-on-second-day-down-5-2013-11-08\" data-ls-seen=\"1\">a day after the company went public </a>at $26 a share and finished its first day of trading at $44.90.\n" +
            "\n" +
            "            </p>\n" +
            "\n" +
            "\n" +
            "\n" +
            "            <div class=\"auxiliary\" style=\"width: 377px;\">\n" +
            "                <div>\n" +
            "                    <img alt=\"\" width=\"377\" height=\"252\" src=\"http://ei.marketwatch.com/Multimedia/2013/11/08/Photos/ME/MW-BO872_twitte_20131108093255_ME.jpg?uuid=b05d2266-4882-11e3-b684-00212803fad6\" title=\"\">\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                    <span class=\"converted-anchor\">Enlarge Image</span>\n" +
            "\n" +
            "                </div>\n" +
            "        <span>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "</span>\n" +
            "            </div>\n" +
            "\n" +
            "\n" +
            "            <p>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                Among other leading tech stocks, gains came from <span><a data-ls-seen=\"1\" href=\"http://www.marketwatch.com/companies/Apple_Inc?lc=int_mb_1001\">Apple Inc.</a></span>                 <span>\n" +
            "                    <span>\n" +
            "                        <a href=\"http://www.marketwatch.com/investing/stock/AAPL\">AAPL</a>\n" +
            "                            <span>+1.57%</span>\n" +
            "                    </span>\n" +
            "                </span>\n" +
            "                , Netflix Inc.                 <span>\n" +
            "                    <span>\n" +
            "                        <a href=\"http://www.marketwatch.com/investing/stock/NFLX\">NFLX</a>\n" +
            "                            <span>+2.46%</span>\n" +
            "                    </span>\n" +
            "                </span>\n" +
            "                , Amazon.com Inc.                 <span>\n" +
            "                    <span>\n" +
            "                        <a href=\"http://www.marketwatch.com/investing/stock/AMZN\">AMZN</a>\n" +
            "                            <span>+1.96%</span>\n" +
            "                    </span>\n" +
            "                </span>\n" +
            "                , <span><a data-ls-seen=\"1\" href=\"http://www.marketwatch.com/companies/Yahoo_Inc?lc=int_mb_1001\">Yahoo Inc.</a></span>                 <span>\n" +
            "                    <span>\n" +
            "                        <a href=\"http://www.marketwatch.com/investing/stock/YHOO\">YHOO</a>\n" +
            "                            <span>+3.15%</span>\n" +
            "                    </span>\n" +
            "                </span>\n" +
            "                &nbsp;and <span><a data-ls-seen=\"1\" href=\"http://www.marketwatch.com/companies/Facebook?lc=int_mb_1001\">Facebook</a></span>  Inc.                 <span>\n" +
            "                    <span>\n" +
            "                        <a href=\"http://www.marketwatch.com/investing/stock/FB\">FB</a>\n" +
            "                            <span>-0.06%</span>\n" +
            "                    </span>\n" +
            "                </span>\n" +
            "                .\n" +
            "\n" +
            "            </p>\n" +
            "            <p>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                The <span><a data-ls-seen=\"1\" href=\"http://www.marketwatch.com/investing/index/comp?lc=int_mb_1001\">Nasdaq</a></span> Composite Index                 <span>\n" +
            "                    <span>\n" +
            "                        <a href=\"http://www.marketwatch.com/investing/index/COMP\">COMP</a>\n" +
            "                            <span>+1.60%</span>\n" +
            "                    </span>\n" +
            "                </span>\n" +
            "                &nbsp;rose almost 62 points, or 1.6%, to close at 3,919.23, while the Philadelphia Semiconductor Index                 <span>\n" +
            "                    <span>\n" +
            "                        <a href=\"http://www.marketwatch.com/investing/index/SOX\">SOX</a>\n" +
            "                            <span>+1.17%</span>\n" +
            "                    </span>\n" +
            "                </span>\n" +
            "                &nbsp;and the <span><a data-ls-seen=\"1\" href=\"http://www.marketwatch.com/companies/Morgan_Stanley?lc=int_mb_1001\">Morgan Stanley</a></span> High Tech 35 Index                 <span>\n" +
            "                    <span>\n" +
            "                        <a href=\"http://www.marketwatch.com/investing/index/MSH\">MSH</a>\n" +
            "                            <span>+1.32%</span>\n" +
            "                    </span>\n" +
            "                </span>\n" +
            "                &nbsp;each also rose more than 1%.\n" +
            "\n" +
            "            </p>\n" +
            "            <p>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "                &nbsp;                    <span></span>\n" +
            "\n" +
            "\n" +
            "            </p>\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "            </div>\n" +
            "            ";

    public static void main(String[] args) throws Exception {
        ApplicationContext instance = ApplicationContext.getInstance();
        WebCrawler webCrawler = (WebCrawler) instance.getBean("webCrawler");

        //System.out.println("webCrawler = " + webCrawler.getArticle("http://www.marketwatch.com/story/a-week-of-yardsticks-for-tesla-twitter-and-economy-2013-11-08").getCleanedArticleText());

        Document doc = Jsoup.parse(html);
        DefaultDocumentCleaner documentCleaner = new DefaultDocumentCleaner();
        Document cleandDoc = documentCleaner.clean(doc);
        DefaultOutputFormatter defaultOutputFormatter = new DefaultOutputFormatter();
        String formattedText = defaultOutputFormatter.getFormattedText(doc.body());
        System.out.println("formattedText = " + formattedText);
    }
}
