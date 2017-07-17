package com.qings.elasticsearch.repository;

import com.qings.elasticsearch.documents.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by qings on 2017/7/16.
 */
@Repository
public interface ArticleRepository extends ElasticsearchRepository<Article,String> {
}
