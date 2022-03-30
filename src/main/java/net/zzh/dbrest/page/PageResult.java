package net.zzh.dbrest.page;

import java.util.List;

/**
 * @Description: 分页结果类
 * @author Zeo Zheng
 * @date 2022/2/21 14:58
 * @version 1.0
 */
public class PageResult<T> {

    public static final int DEFAULT_PAGE_SIZE = 10;
    private int page;
    private int size;
    private int total;
    private List<T> datas;

    public PageResult(int page, int pageSize) {
        this.page = Math.max(page, 0);
        this.size = pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
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

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPage() {
        return total % size == 0 ? total / size : total / size + 1;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
