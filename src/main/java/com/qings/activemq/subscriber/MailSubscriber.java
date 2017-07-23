package com.qings.activemq.subscriber;

import com.qings.activemq.MyConnectionFactory;
import com.qings.elasticsearch.documents.Article;
import com.qings.elasticsearch.documents.User;
import com.qings.elasticsearch.repository.ArticleRepository;
import com.qings.elasticsearch.repository.UserRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import java.util.Date;
import java.util.List;

/**
 * Created by qings on 2017/7/23.
 */
@RestController
@RequestMapping("message")
public class MailSubscriber {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MyConnectionFactory connectionFactory;

    public String findSubscriber(){
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        Iterable<User> it = userRepository.search(QueryBuilders.termQuery("isValid",true));
        Connection finalConnection = connection;
        it.forEach(user -> {
            int cycle = user.getCycle();
            Date date = user.getLastRemind();
            Integer lastRemind = 0;
            if(null != date) {
                lastRemind = Integer.valueOf(DateFormatUtils.format(DateUtils.addDays(date,cycle), "yyyyMMdd"));
            }
            if(Integer.valueOf(DateFormatUtils.format(new Date(), "yyyyMMdd")) == lastRemind){
                String heros = user.getHeros();
                if(StringUtils.isNotEmpty(heros)){
                    String[] arr = heros.split(",");
                    List<Article> articleList = null;
                    for(int i=0,j=arr.length;i<j;i++){
                        BoolQueryBuilder bool = QueryBuilders.boolQuery();
                        bool.must(QueryBuilders.termQuery("introduction",arr[i]));
                        bool.filter(QueryBuilders.rangeQuery("publish").lte(date.getTime()));
                        Page<Article> page = articleRepository.search(bool,new PageRequest(0,20));
                        articleList.addAll(page.getContent());
                    }
                    try {
                        Session session = finalConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
                        Destination destination = session.createTopic("mail");

                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        try {
            connection.stop();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return "true";
    }
}
