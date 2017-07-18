package com.qings.elasticsearch.documents;

import com.qings.elasticsearch.common.CommonProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.util.Date;

/**
 * Created by qings on 2017/7/17.
 */
@Document(indexName = CommonProperties.INDEX_NAME, type = CommonProperties.TYPE_USER, replicas = 0)
public class User {

    @Id
    @Field(type = FieldType.String, index = FieldIndex.no,store = true)
    private String id;
    @Field(type = FieldType.String, index = FieldIndex.no,store = true)
    private String mailAddr;            //邮件地址
    @Field(type = FieldType.Boolean, index = FieldIndex.no,store = true)
    private Boolean isSubscribe;        //是否已订阅
    @Field(type = FieldType.Boolean, index = FieldIndex.no,store = true)
    private Boolean isValid;            //邮箱地址是否有效
    @Field(type = FieldType.Integer, index = FieldIndex.no,store = true)
    private Integer cycle;              //多久提醒一次
    @Field(type = FieldType.String, index = FieldIndex.no,store = true)
    private String heros;               //订阅的英雄咨询，以逗号分隔
    @Field(type = FieldType.Date, index = FieldIndex.no,store = true)
    private Date lastRemind;            //上一次提醒时间

    public User(String id){
        this.id = id;
    }

    public Date getLastRemind() {
        return lastRemind;
    }

    public void setLastRemind(Date lastRemind) {
        this.lastRemind = lastRemind;
    }

    public Integer getCycle() {
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
    }

    public String getHeros() {
        return heros;
    }

    public void setHeros(String heros) {
        this.heros = heros;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMailAddr() {
        return mailAddr;
    }

    public void setMailAddr(String mailAddr) {
        this.mailAddr = mailAddr;
    }

    public Boolean getSubscribe() {
        return isSubscribe;
    }

    public void setSubscribe(Boolean subscribe) {
        isSubscribe = subscribe;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }
}
