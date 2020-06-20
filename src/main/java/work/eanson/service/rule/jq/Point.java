package work.eanson.service.rule.jq;

import java.util.Objects;

/**
 * @author eanson
 * <p>
 * 重写hashcode和toString方法用于比较节点是否相同
 */
public class Point {
    int row;
    int col;

    Point() {
    }

    Point(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Point point = (Point) o;
        return row == point.row &&
                col == point.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "Point{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
