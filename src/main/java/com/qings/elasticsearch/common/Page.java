package com.qings.elasticsearch.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Created by qings on 2017/7/17.
 */
public class Page implements Pageable {

    private int pageNumber;
    private int pageSize;
    private int offset;
    private Sort sort;

    public Page(int pageNumber,int pageSize,int offset,Sort sort){
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.offset = offset;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return 0;
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public Sort getSort() {
        return null;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
