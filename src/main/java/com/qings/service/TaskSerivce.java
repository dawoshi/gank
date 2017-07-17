package com.qings.service;

import com.qings.dao.ArticleDao;
import com.qings.elasticsearch.repository.ArticleRepository;
import com.qings.site.SeventeenPageProcessor;
import com.qings.site.common.CustomPipeline;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

import java.util.Date;

/**
 * Created by qings on 2017/7/16.
 */
@Component
public class TaskSerivce {

    private Logger logger = Logger.getLogger(TaskSerivce.class);

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ArticleRepository articleRepository;

//    @Scheduled(cron = "0 0/1 * * * ?")
    public void task(){
        logger.info("开始执行定时任务");
        Spider.create(new SeventeenPageProcessor()).addPipeline(new CustomPipeline(articleRepository)).addUrl(SeventeenPageProcessor.URL_LIST).thread(3).run();
    }
}
