package com.qings.elasticsearch.repository;

import com.qings.elasticsearch.documents.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by qings on 2017/7/18.
 */
@Repository
public interface UserRepository extends ElasticsearchRepository<User,String> {
}
