package work.eanson.pojo.extend;

import work.eanson.pojo.Category;
import work.eanson.pojo.ChessInfo;

/**
 * @author yans
 */
public class ChessInfoExtend2 extends ChessInfo {
    private Category categoryObj;

    public Category getCategoryObj() {
        return categoryObj;
    }

    public void setCategoryObj(Category categoryObj) {
        this.categoryObj = categoryObj;
    }

    @Override
    public String toString() {
        return "ChessInfoExtend2{" +
                "chessInfo" + super.toString() +
                "categoryObj=" + categoryObj +
                '}';
    }
}
