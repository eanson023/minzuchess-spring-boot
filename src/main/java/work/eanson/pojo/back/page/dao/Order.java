package work.eanson.pojo.back.page.dao;

import java.util.Objects;

/**
 * @author eanson
 * @create 2020-03-28 21:16
 */
public class Order {
    private String columnName;
    private String orderBy;

    public Order() {
    }

    public Order(String columnName, String orderBy) {
        this.columnName = columnName;
        this.orderBy = orderBy;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(columnName, order.columnName) &&
                Objects.equals(orderBy, order.orderBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName, orderBy);
    }
}
