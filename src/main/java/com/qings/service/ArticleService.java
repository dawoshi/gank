package com.qings.service;

import com.qings.dao.ArticleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by qings on 2017/7/16.
 */
@Service
public class ArticleService {

    @Autowired
    private ArticleDao articleDao;


}
