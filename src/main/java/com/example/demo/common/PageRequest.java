// PageRequest.java - 分页请求基类（如果不存在的话）
package com.example.demo.common;

import lombok.Data;

@Data
public class PageRequest {

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public Integer getPageNum() {
        return pageNum == null || pageNum <= 0 ? 1 : pageNum;
    }

    public Integer getPageSize() {
        return pageSize == null || pageSize <= 0 ? 10 : pageSize;
    }
}