package work.eanson.pojo.back.page.dao;

import java.util.Set;

/**
 * @author eanson
 * @create 2020-03-28 21:22
 */
public class Search {
    private Set<String> columnNames;
    private String value;

    public Set<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(Set<String> columnNames) {
        this.columnNames = columnNames;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
