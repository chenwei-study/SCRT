package com.example.scrt.entity;

import java.util.Collections;
import java.util.List;


public class ListPageUtil<T> {
    private List<T> data;

    /** 上一页 */
    private int lastPage;

    /** 当前页 */
    private int currentPage;

    /** 下一页 */
    private int nextPage;
//
    /** 每页条数 */
    private int pageSize;

    /** 总页数 */
    private int totalPage;

    /** 总数据条数 */
    private int totalCount;

    public ListPageUtil(List<T> data, int currentPage, int pageSize) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("data must be not empty!");
        }

        this.data = data;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.totalCount = data.size();
        this.totalPage = (totalCount + pageSize - 1) / pageSize;
        this.lastPage = currentPage-1>1? currentPage-1:1;
        this.nextPage = currentPage>=totalPage? totalPage: currentPage + 1;

    }

    /**
     * 得到分页后的数据
     * @return 分页后结果
     */
//    public List<T> getPagedLst() {
//        int fromIndex = (nowPage - 1) * pageSize;
//        if (fromIndex >= data.size()) {
//            return Collections.emptyList();//空数组
//        }
//        if(fromIndex<0){
//            return Collections.emptyList();//空数组
//        }
//        int toIndex = nowPage * pageSize;
//        if (toIndex >= data.size()) {
//            toIndex = data.size();
//        }
//        return data.subList(fromIndex, toIndex);
//    }

    public int getPageSize() {
        return pageSize;
    }

    public List<T> getData() {
        int fromIndex = (currentPage - 1) * pageSize;
        if (fromIndex >= data.size()) {
            return Collections.emptyList();//空数组
        }
        if(fromIndex<0){
            return Collections.emptyList();//空数组
        }
        int toIndex = currentPage * pageSize;
        if (toIndex >= data.size()) {
            toIndex = data.size();
        }
        return data.subList(fromIndex, toIndex);
    }
    public int getLastPage() {
        return lastPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getTotalCount() {
        return totalCount;
    }
}

