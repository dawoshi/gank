package com.qings.site;

import com.qings.elasticsearch.common.CommonProperties;
import com.qings.robots.RobotsDisallowedException;
import com.qings.robots.RobotsParser;
import com.qings.site.common.SitePageProcessor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.net.MalformedURLException;
import java.util.Date;

/**
 * Created by qings on 2017/7/16.
 */
public class SeventeenPageProcessor extends SitePageProcessor implements PageProcessor {

    private Logger logger = Logger.getLogger(SeventeenPageProcessor.class);

    public String ROOT_CATALOG = "http://news.17173.com";
    public final static String URL_LIST = "http://news.17173.com/z/pvp/list/zxwz.shtml";
    private String URL_LIST_PAGE = "http://news.17173.com/z/pvp/list/zxwz?_[0-9]{1,2}.shtml";
    private String URL_POST = "http://news.17173.com/z/pvp/content/$$/[0-9]{9}_[0-9].shtml";
    private String URL_POST2 = "http://news.17173.com/z/pvp/content/$$/[0-9]{9}.shtml";

    private RobotsParser parser = null;
    private RobotsParser getRobotsParser(){
        if(parser == null){
                if(parser == null) {
                    parser = new RobotsParser(USER_AGENT);
                    try {
                        parser.connect(ROOT_CATALOG);
                    } catch (RobotsDisallowedException e) {
                        logger.error(e.getMessage());
                    } catch (MalformedURLException e) {
                        logger.error(e.getMessage());
                    }
                }
        }
        return parser;
    }

    public SeventeenPageProcessor(){
        URL_POST = URL_POST.replace("$$","[0-9]{8}");
        URL_POST2 = URL_POST2.replace("$$","[0-9]{8}");
    }

    public SeventeenPageProcessor(Date date){
        URL_POST = URL_POST.replace("$$",DateFormatUtils.format(date,"MMddyyyy"));
        URL_POST2 = URL_POST2.replace("$$",DateFormatUtils.format(date,"MMddyyyy"));
    }

    @Override
    public void process(Page page) {
        try {
            if(getRobotsParser().isAllowed(page.getUrl().toString())) {
                if(page.getUrl().regex(URL_LIST).match()){
//                    爬取到指定列表后，继续爬取列表指定区域下的内容页
                    page.addTargetRequests(page.getHtml().links().regex(URL_POST).all());
                    page.addTargetRequests(page.getHtml().links().regex(URL_POST2).all());
                    page.addTargetRequests(page.getHtml().links().regex(URL_LIST_PAGE).all());
                }else if(page.getUrl().regex(URL_LIST_PAGE).match()){
                    page.addTargetRequests(page.getHtml().links().regex(URL_POST).all());
                    page.addTargetRequests(page.getHtml().links().regex(URL_POST2).all());
                }else{
//                    到达内容页
                    Selectable select = page.getUrl();
                    Html html = page.getHtml();
                    page.putField("site_name", CommonProperties.TYPE_17173);
                    page.putField("url",select.get());
                    page.putField("title",html.getDocument().title());
                    page.putField("created",html.getDocument().getElementsByClass("gb-final-date").eachText().get(0).replace("时间：",""));
                    page.putField("author",html.getDocument().getElementsByClass("gb-final-author").eachText().get(0).replace("作者：",""));
                }
            }
        } catch (MalformedURLException e) {
            logger.error("17173下"+page.getUrl().toString()+"不能被爬取");
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

}
