package com.qings.site.common;

import com.qings.dao.ArticleDao;
import com.qings.elasticsearch.documents.Article;
import com.qings.elasticsearch.repository.ArticleRepository;
import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;
import java.util.UUID;

/**
 * Created by qings on 2017/7/16.
 */
public class CustomPipeline implements Pipeline {

    private ArticleRepository articleRepository;

    public CustomPipeline(ArticleRepository articleRepository){
        this.articleRepository = articleRepository;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(StringUtils.isBlank(resultItems.get("title"))){
            return;
        }
        Article article = new Article();
        article.setId(UUID.randomUUID().toString());
        article.setTitle(resultItems.get("title"));
        article.setIntroduction(resultItems.get("introduction"));
        article.setAuthor(resultItems.get("author"));
        article.setPublish(resultItems.get("publish"));
        article.setSitename(resultItems.get("site_name"));
        article.setUrl(resultItems.get("url"));
        article.setCreated(new Date());
        articleRepository.save(article);
    }
}
