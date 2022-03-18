package net.zzh.dbrest.page;

import java.util.List;

public class PageResult<T>{

    public static final int DEFAULT_PAGE_SIZE = 10;
    private int page;
    private int pageSize;
    private int total;
    private List<T> datas;

    public PageResult(int page, int pageSize) {
        this.page = page <= 0 ? 0 : page;
        this.pageSize = pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
