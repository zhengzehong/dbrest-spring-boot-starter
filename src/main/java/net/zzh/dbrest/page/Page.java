package net.zzh.dbrest.page;

import cn.hutool.core.util.PageUtil;

public class Page {

    public static final int DEFAULT_PAGE_SIZE = 10;

    private int pageNumber;
    private int pageSize;

    public Page() {
        this.pageNumber = 1;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public Page(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber < 0 ? 0 : pageNumber;
        this.pageSize = pageSize <= 0 ? 20 : pageSize;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber < 0 ? 0 : pageNumber;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? 20 : pageSize;
    }

    public int getStartPosition() {
        return this.getStartEnd()[0];
    }

    public int getEndPosition() {
        return this.getStartEnd()[1];
    }

    public int[] getStartEnd() {
        return PageUtil.transToStartEnd(this.pageNumber, this.pageSize);
    }

    public String toString() {
        return "Page [page=" + this.pageNumber + ", pageSize=" + this.pageSize + "]";
    }
}

