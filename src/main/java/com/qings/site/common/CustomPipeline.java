package com.qings.site.common;

import com.qings.dao.ArticleDao;
import com.qings.entities.Article;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.UUID;

/**
 * Created by qings on 2017/7/16.
 */
public class CustomPipeline implements Pipeline {

    private ArticleDao articleDao;

    public CustomPipeline(ArticleDao articleDao){
        this.articleDao = articleDao;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        if(StringUtils.isBlank(resultItems.get("title"))){
            return;
        }
        Article article = new Article();
        article.setId(UUID.randomUUID().toString());
        article.setTitle(resultItems.get("title"));
        article.setAuthor(resultItems.get("author"));
        article.setPublish(resultItems.get("created"));
        article.setSitename(resultItems.get("site_name"));
        article.setUrl(resultItems.get("url"));
        articleDao.save(article);
    }
}
