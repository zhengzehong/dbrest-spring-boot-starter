package net.zzh.dbrest.page;

import cn.hutool.core.util.PageUtil;

public class Page {

    public static final int DEFAULT_PAGE_SIZE = 10;

    private int page;
    private int size;

    public Page() {
        this.page = 1;
        this.size = DEFAULT_PAGE_SIZE;
    }

    public Page(int pageNumber, int pageSize) {
        this.page = pageNumber < 0 ? 0 : pageNumber;
        this.size = pageSize <= 0 ? 20 : pageSize;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page < 0 ? 0 : page;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size <= 0 ? 20 : size;
    }

    public int getStartPosition() {
        return this.getStartEnd()[0];
    }

    public int getEndPosition() {
        return this.getStartEnd()[1];
    }

    public int[] getStartEnd() {
        return PageUtil.transToStartEnd(this.page, this.size);
    }

    public String toString() {
        return "Page [page=" + this.page + ", pageSize=" + this.size + "]";
    }
}

