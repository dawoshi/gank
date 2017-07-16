package com.qings.elasticsearch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Created by qings on 2017/7/16.
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "org/springframework/data/elasticsearch/repositories")
public class Config {

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
//        try {
//            Client client = TransportClient.builder().build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host1"),9300));
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
        return new ElasticsearchTemplate(nodeBuilder().local(true).node().client());
    }
}