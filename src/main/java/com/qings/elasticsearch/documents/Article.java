package com.qings.elasticsearch.documents;

import com.qings.common.CommonProperties;
import org.springframework.data.elasticsearch.annotations.*;

import javax.persistence.Id;
import java.util.Date;

/**
 * Created by qings on 2017/7/16.
 */
@Document(indexName = CommonProperties.INDEX_NAME, type = CommonProperties.TYPE_ARTICLE, replicas = 0)
public class Article {

    @Id
    @Field(type = FieldType.String, index = FieldIndex.no,store = true)
    private String id;
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed,store = true)
    private String sitename;
    @Field(type = FieldType.String, index = FieldIndex.analyzed,store = true,analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.String, index = FieldIndex.analyzed,store = true)
    private String author;
    @Field(type = FieldType.Date, index = FieldIndex.no,store = true)
    private Date publish;
    @Field(type = FieldType.String, index = FieldIndex.analyzed,store = true,analyzer = "ik_max_word")
    private String introduction;
    @Field(type = FieldType.Date, index = FieldIndex.no,store = true)
    private Date created;
    @Field(type = FieldType.String, index = FieldIndex.no,store = true)
    private String url;

    public Date getPublish() {
        return publish;
    }

    public void setPublish(Date publish) {
        this.publish = publish;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
