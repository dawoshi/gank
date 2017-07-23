package com.qings.controller;

import com.alibaba.fastjson.JSONObject;
import com.qings.common.ApiResponse;
import com.qings.common.Heros;
import com.qings.common.CommonProperties;
import com.qings.elasticsearch.documents.Article;
import com.qings.elasticsearch.documents.User;
import com.qings.elasticsearch.repository.ArticleRepository;
import com.qings.elasticsearch.repository.UserRepository;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.search.MatchQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by qings on 2017/7/17.
 */
@Controller
public class IndexController {


    private final int MAX_RESULT = 10;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ElasticsearchOperations elasticsearchTemplate;

    @RequestMapping("index")
    public String index(){

        return "index";
    }

    @RequestMapping("delete")
    @ResponseBody
    public ApiResponse delSource(@RequestParam(value = "q", defaultValue = "")String param,@RequestParam(value = "code", defaultValue = "0")Integer code){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(param) || !CommonProperties.CODE.equals(code)){
            apiResponse.setCode(4002);
        }else{
            if(CommonProperties.CODE.equals(code)){
                MatchQueryBuilder matchQuery = QueryBuilders.matchQuery(param,"sitename");
                DeleteQuery deleteQuery = new DeleteQuery();
                deleteQuery.setQuery(matchQuery);
                elasticsearchTemplate.delete(deleteQuery, Article.class);
            }
            apiResponse.setCode(2000);
        }
        return apiResponse;
    }

    @RequestMapping("find")
    @ResponseBody
    public ApiResponse findInformation(@RequestParam(value = "q", defaultValue = "")String param, @RequestParam(value = "c", defaultValue = "0")Integer cursor){
        ApiResponse apiResponse = new ApiResponse();
        Page<Article> resultPage;
        if(StringUtils.isBlank(param.trim())){
            resultPage = articleRepository.findAll(new PageRequest(cursor*MAX_RESULT,MAX_RESULT, Sort.Direction.DESC,"publish"));
        }else{
            HighlightBuilder.Field field1 = new HighlightBuilder.Field("introduction");
            field1.preTags("<span style=\"color:red;\">");
            field1.postTags("</span>");
            HighlightBuilder.Field field2 = new HighlightBuilder.Field("title");
            field2.preTags("<span style=\"color:red;\">");
            field2.postTags("</span>");
            HighlightBuilder.Field field3 = new HighlightBuilder.Field("author");
            field3.preTags("<span style=\"color:red;\">");
            field3.postTags("</span>");
            HighlightBuilder.Field field4 = new HighlightBuilder.Field("sitename");
            field4.preTags("<span style=\"color:red;\">");
            field4.postTags("</span>");
            MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery(param,"introduction","title","author","sitename");
            NativeSearchQuery searchQuery = (new NativeSearchQueryBuilder()).withQuery(query)
                    .withHighlightFields(field1,field2,field3,field4)
                    .withPageable(new PageRequest(cursor*MAX_RESULT,MAX_RESULT, Sort.Direction.DESC,"publish")).build();
            resultPage = queryForPage(searchQuery);
        }
        if(null != resultPage && !resultPage.getContent().isEmpty()){
            List<Article> articleList = resultPage.getContent();
            apiResponse.setCount(resultPage.getContent().size());
            apiResponse.setList(new ArrayList<>(articleList));
            apiResponse.setPages(resultPage.getTotalPages());
            if(resultPage.hasNext()) apiResponse.setCursor(cursor+1);
            return apiResponse;
        }
        apiResponse.setCount(0);
        return apiResponse;
    }

    @RequestMapping(value = "auto_complete", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse autoCompletion(@RequestParam(value = "param", defaultValue = "")String param){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(param)){
            apiResponse.setCount(0);
            return apiResponse;
        }
        HighlightBuilder.Field field1 = new HighlightBuilder.Field("title");
        field1.preTags("<span style=\"color:red;\">");
        field1.postTags("</span>");
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("title",param);
        NativeSearchQuery searchQuery = (new NativeSearchQueryBuilder()).withQuery(matchQuery)
                .withHighlightFields(field1)
                .withPageable(new PageRequest(0,MAX_RESULT, Sort.Direction.DESC,"publish")).build();
        Page<Article> resultPage = queryForPage(searchQuery);
        apiResponse.setCount(resultPage.getNumber());
        apiResponse.setList(new ArrayList<>(resultPage.getContent()));
        return apiResponse;
    }

    private List<String> heroList = new ArrayList<>(5);
    /**
     * 查询最近出现频率最高的几名英雄
     * @return
     */
    @RequestMapping(value = "hotheros", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse findHotHeros(){
        ApiResponse apiResponse = new ApiResponse();
        List<String> resultList = new ArrayList<>(5);
        if(!heroList.isEmpty()) {
            resultList = this.heroList;
        }else{
            Map<String, Long> herosMap = new HashedMap(Heros.heros.size());
            for (String hero : Heros.heros) {
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("introduction", hero);
                NativeSearchQuery searchQuery = (new NativeSearchQueryBuilder()).withQuery(termQueryBuilder)
                        .withIndices(CommonProperties.INDEX_NAME).withTypes(CommonProperties.TYPE_ARTICLE)
                        .build();
                long count = elasticsearchTemplate.count(searchQuery);
                herosMap.put(hero, count);
            }
            List<Map.Entry<String, Long>> entryList = new ArrayList<>(herosMap.entrySet());
            Collections.sort(entryList, new Comparator<Map.Entry<String, Long>>() {
                @Override
                public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                    if (o1.getValue() > o2.getValue()) {
                        return -1;
                    } else if (o1.getValue() < o2.getValue()) {
                        return 1;
                    }
                    return 0;
                }
            });
            for (Map.Entry<String, Long> entry : entryList) {
                if (resultList.size() == 3) {
                    break;
                }
                resultList.add(entry.getKey());
            }
            heroList.addAll(resultList);
        }
        apiResponse.setList(new ArrayList<>(resultList));
        apiResponse.setCount(resultList.size());
        return apiResponse;
    }

    @RequestMapping(value = "subscribe", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse subscribe(final String body){
        ApiResponse apiResponse = new ApiResponse();
        if(StringUtils.isBlank(body.trim())){
            apiResponse.setCode(400);
            apiResponse.setErrorDescription("请求数据为空");
            return apiResponse;
        }
        JSONObject json = JSONObject.parseObject(body);
        if(isValidUser(json)){
            User user = new User(UUID.randomUUID().toString());
            user.setSubscribe(true);
            user.setValid(false);
            user.setMailAddr(json.getString("mailAddr"));
            user.setCycle(json.getInteger("cycle"));
            user.setHeros(json.getString("heros"));
            User resultUser = userRepository.save(user);
            if(resultUser != null) apiResponse.setCode(200);
        }
        return apiResponse;
    }

    private boolean isValidUser(JSONObject json){
        if(json.containsKey("mailAddr") && json.containsKey("cycle") && json.containsKey("heros")){
            return true;
        }
        return false;
    }

    /**
     * 自定义高亮查询
     * @param searchQuery
     * @return
     */
    private Page<Article> queryForPage(SearchQuery searchQuery){
        return elasticsearchTemplate.queryForPage(searchQuery,Article.class,new SearchResultMapper(){

            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clzz, Pageable pageable) {
                List<Article> articles = new ArrayList(MAX_RESULT);
                for(SearchHit hit:response.getHits()){
                    if (response.getHits().getHits().length <= 0) {
                        return null;
                    }
                    Article article = new Article();
                    article.setId(hit.getId());
                    article.setAuthor(hit.getHighlightFields().containsKey("author")?hit.getHighlightFields().get("author").fragments()[0].toString():hit.getSource().get("author").toString());
                    article.setIntroduction(hit.getHighlightFields().containsKey("introduction")?hit.getHighlightFields().get("introduction").fragments()[0].toString():hit.getSource().get("introduction").toString());
                    article.setPublish(new Date(Long.valueOf(hit.getSource().get("publish").toString())));
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

}
