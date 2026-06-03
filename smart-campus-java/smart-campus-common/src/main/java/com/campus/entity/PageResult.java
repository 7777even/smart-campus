package com.campus.entity;

import java.util.List;

/**
 * 分页结果包装
 */
public class PageResult<T> {

    private long totalCount;
    private int pageSize;
    private int pageNo;
    private int pageTotal;
    private List<T> list;

    public PageResult() {}

    public PageResult(long totalCount, int pageSize, int pageNo, List<T> list) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.pageTotal = (int) Math.ceil((double) totalCount / pageSize);
        this.list = list;
    }

    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public int getPageNo() { return pageNo; }
    public void setPageNo(int pageNo) { this.pageNo = pageNo; }
    public int getPageTotal() { return pageTotal; }
    public void setPageTotal(int pageTotal) { this.pageTotal = pageTotal; }
    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
}
