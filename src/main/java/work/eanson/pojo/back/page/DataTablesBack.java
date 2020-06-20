package work.eanson.pojo.back.page;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author eanson
 * @create 2020-03-28 16:10
 * https://datatables.net/manual/server-side
 * 分页返回数据pojo
 */
public class DataTablesBack<T> {
    private Integer draw;
    private Integer recordsTotal;
    private Integer recordsFiltered;
    private List<T> data;
    private String error;

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public Integer getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Integer recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public Integer getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(Integer recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public DataTablesBack() {
    }

    /**
     * //如果没有过滤的话 直接组装PageInfo消息
     * @param data
     */
    public DataTablesBack(List<T> data) {
        this.data = data;
        PageInfo<T> tPageInfo = new PageInfo<>(data);
        this.recordsFiltered = Math.toIntExact(tPageInfo.getTotal());
        this.recordsTotal = Math.toIntExact(tPageInfo.getTotal());
    }
}
