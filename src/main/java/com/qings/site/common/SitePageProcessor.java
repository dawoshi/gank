package com.qings.site.common;

import us.codecraft.webmagic.Site;

/**
 * Created by qings on 2017/6/20.
 */
public class SitePageProcessor {

    public static final String USER_AGENT= "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36";

    protected Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(100)
            .setUserAgent(USER_AGENT);

    protected static String replaceHTML(String str){
        return str!=null?str.replaceAll("\\<.*?>","").replaceAll("&nbsp;",""):"";
    }
}
