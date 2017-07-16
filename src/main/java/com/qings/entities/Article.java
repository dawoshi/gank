package com.qings.entities;

import javax.persistence.*;

/**
 * Created by qings on 2017/7/16.
 */
@Table(name = "article")
@Entity
public class Article {

    @Id
    @Column
    private String id;
    @Column
    private String sitename;
    @Column
    private String title;
    @Column
    private String author;
    @Column
    private String created;
    @Column
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
