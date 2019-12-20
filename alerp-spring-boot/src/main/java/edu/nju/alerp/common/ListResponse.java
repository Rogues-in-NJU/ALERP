package edu.nju.alerp.common;

import lombok.Data;

/**
 * @Description: 包装分页的父类
 * @Author: yangguan
 * @CreateDate: 2019-12-20 14:06
 */
@Data
public class ListResponse {

    protected int totalPages;

    protected int pageIndex;

    protected int pageSize;

    protected Object result;
}
