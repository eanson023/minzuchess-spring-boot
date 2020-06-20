package work.eanson.service.rule;

import work.eanson.pojo.Trick;

/**
 * Long live freedom and fraternity, No 996
 * <pre>
 * 棋类游戏规则检测统一接口
 * </pre>
 *
 * @author eanson
 * @date 2020/6/14
 */
public interface ChessAIAnalyzer {
    /**
     * 当返回的trick中isFalse是true时 表示招法有错
     *
     * @param trick
     */
    void analyze(Trick trick);
}
