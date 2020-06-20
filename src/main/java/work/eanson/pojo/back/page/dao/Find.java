package work.eanson.pojo.back.page.dao;

import java.util.Set;

/**
 * @author eanson
 * @create 2020-03-28 21:12
 */
public class Find {
    private Set<Order> orders;
    private Search search;
    private String cbCode;

    public Find(Set<Order> orders, Search search) {
        this.orders = orders;
        this.search = search;
    }

    public String getCbCode() {
        return cbCode;
    }

    public void setCbCode(String cbCode) {
        this.cbCode = cbCode;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Find(Set<Order> orders, Search search, String cbCode) {
        this.search = search;
        this.orders = orders;
        this.cbCode = cbCode;
    }

    public Find() {
    }
}
