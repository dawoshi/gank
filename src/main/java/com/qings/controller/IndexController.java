package com.qings.controller;

import com.qings.common.ApiResponse;
import com.qings.elasticsearch.documents.Article;
import com.qings.elasticsearch.repository.ArticleRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by qings on 2017/7/17.
 */
@Controller
public class IndexController {


    private final int MAX_RESULT = 10;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ElasticsearchOperations elasticsearchTemplate;

    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @RequestMapping("find")
    @ResponseBody
    public ApiResponse findInformation(@RequestParam("param")String param, @RequestParam(value = "cursor", defaultValue = "0")Integer cursor){
        ApiResponse apiResponse = new ApiResponse();
        Page<Article> resultPage = null;
        if(StringUtils.isBlank(param.trim())){
            resultPage = articleRepository.findAll(new PageRequest(cursor,MAX_RESULT, Sort.Direction.DESC,"publish"));
        }else{
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(param,"introduction","title","author","sitename");
            resultPage = articleRepository.search(multiMatchQueryBuilder, new PageRequest(cursor,MAX_RESULT, Sort.Direction.DESC,"publish"));
        }
        if(null != resultPage && resultPage.getNumber()>0){
            List<Article> articleList = resultPage.getContent();
            apiResponse.setCount(resultPage.getNumber());
            apiResponse.setList(articleList);
            apiResponse.setPages(resultPage.getTotalPages());
            if(resultPage.hasNext()) apiResponse.setCursor(cursor*MAX_RESULT+1);
            return apiResponse;
        }
        apiResponse.setCount(0);
        return apiResponse;
    }

    @RequestMapping(value = "subscribe", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse subscribe(final String body){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(body.trim())){
            apiResponse.setError("400");
            apiResponse.setErrorDescription("请求数据为空");
            return apiResponse;
        }

        return apiResponse;
    }
}
