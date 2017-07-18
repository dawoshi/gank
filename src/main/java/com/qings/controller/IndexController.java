package com.qings.controller;

import com.google.common.collect.Lists;
import com.qings.common.ApiResponse;
import com.qings.elasticsearch.documents.Article;
import com.qings.elasticsearch.repository.ArticleRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.flexible.core.builders.QueryBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
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
    public ApiResponse findInformation(@RequestParam(value = "param", defaultValue = "")String param, @RequestParam(value = "cursor", defaultValue = "0")Integer cursor){
        ApiResponse apiResponse = new ApiResponse();
        Page<Article> resultPage;
        if(StringUtils.isBlank(param.trim())){
            resultPage = articleRepository.findAll(new PageRequest(cursor*MAX_RESULT,MAX_RESULT, Sort.Direction.DESC,"publish"));
        }else{
            HighlightBuilder.Field field1 = new HighlightBuilder.Field("introduction");
            field1.preTags("<span style=\"font-color:red;\">");
            field1.postTags("</span>");
            HighlightBuilder.Field field2 = new HighlightBuilder.Field("title");
            field2.preTags("<span style=\"font-color:red;\">");
            field2.postTags("</span>");
            HighlightBuilder.Field field3 = new HighlightBuilder.Field("author");
            field3.preTags("<span style=\"font-color:red;\">");
            field3.postTags("</span>");
            HighlightBuilder.Field field4 = new HighlightBuilder.Field("sitename");
            field4.preTags("<span style=\"font-color:red;\">");
            field4.postTags("</span>");
            MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery(param,"introduction","title","author","sitename");
            NativeSearchQuery searchQuery = (new NativeSearchQueryBuilder()).withQuery(query)
                    .withHighlightFields(field1,field2,field3,field4)
                    .withPageable(new PageRequest(cursor*MAX_RESULT,MAX_RESULT, Sort.Direction.DESC,"publish")).build();
            resultPage = elasticsearchTemplate.queryForPage(searchQuery,Article.class,new SearchResultMapper(){

                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clzz, Pageable pageable) {
                    List<Article> articles = new ArrayList<Article>(MAX_RESULT);
                    for(SearchHit hit:response.getHits()){
                        if (response.getHits().getHits().length <= 0) {
                            return null;
                        }
                        Article article = new Article();
                        article.setId(hit.getId());
                        article.setAuthor(hit.getHighlightFields().containsKey("author")?hit.getHighlightFields().get("author").fragments()[0].toString():hit.getSource().get("author").toString());
                        article.setIntroduction(hit.getHighlightFields().containsKey("introduction")?hit.getHighlightFields().get("introduction").fragments()[0].toString():hit.getSource().get("introduction").toString());
                        article.setPublish(hit.getSource().get("publish").toString());
                        article.setSitename(hit.getHighlightFields().containsKey("sitename")?hit.getHighlightFields().get("sitename").fragments()[0].toString():hit.getSource().get("sitename").toString());
                        article.setTitle(hit.getHighlightFields().containsKey("title")?hit.getHighlightFields().get("title").fragments()[0].toString():hit.getSource().get("title").toString());
                        article.setUrl(hit.getSource().get("url").toString());
                        articles.add(article);
                    }
                    if (!articles.isEmpty()) {
                        return new AggregatedPageImpl<>((List<T>) articles);
                    }
                    return null;
                }
            });
        }
        if(null != resultPage && !resultPage.getContent().isEmpty()){
            List<Article> articleList = resultPage.getContent();
            apiResponse.setCount(resultPage.getNumber());
            apiResponse.setList(articleList);
            apiResponse.setPages(resultPage.getTotalPages());
            if(resultPage.hasNext()) apiResponse.setCursor(cursor+1);
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
