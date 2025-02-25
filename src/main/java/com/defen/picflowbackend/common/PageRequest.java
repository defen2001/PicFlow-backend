package com.defen.picflowbackend.common;

import lombok.Data;

/**
 * 通用的分页请求类
 */
@Data
public class PageRequest {

    /**
     * 当前页号，默认从第1页开始
     */
    private int current = 1;

    /**
     * 每页条数，默认10条数据
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}
