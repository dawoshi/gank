package com.qings.elasticsearch.documents;

import com.qings.elasticsearch.common.CommonProperties;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by qings on 2017/7/16.
 */
@Entity
@Document(indexName = CommonProperties.INDEX_NAME, type = CommonProperties.TYPE_17173)
public class Article {

    @Id
    @Field(type = FieldType.String, index = FieldIndex.no,store = true)
    private String id;
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed,store = true)
    private String sitename;
    @Field(type = FieldType.String, index = FieldIndex.analyzed,store = true)
    private String title;
    @Field(type = FieldType.String, index = FieldIndex.analyzed,store = true)
    private String author;
    @Field(type = FieldType.String, index = FieldIndex.no,store = true)
    private String created;
    @Field(type = FieldType.String, index = FieldIndex.no,store = true)
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSitename() {
        return sitename;
    }

    public void setSitename(String sitename) {
        this.sitename = sitename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
