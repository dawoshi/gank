package com.qings.dao;

import com.qings.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by qings on 2017/7/16.
 */
@Repository
public interface ArticleDao extends JpaRepository<Article, String> {
}
