package net.zzh.dbrest.page;

import cn.hutool.core.util.PageUtil;

/**
 * @Description: 分页对象1
 * @author Zeo Zheng
 * @date 2022/2/21 14:58
 * @version 1.0
 */
public class Page {

    public static final int DEFAULT_PAGE_SIZE = 10;
    private int page;
    private int size;

    public Page() {
        this.page = 1;
        this.size = DEFAULT_PAGE_SIZE;
    }

    public Page(int page, int size) {
        this.page = Math.max(page, 0);
        this.size = size <= 0 ? DEFAULT_PAGE_SIZE : size;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = Math.max(page, 0);
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

