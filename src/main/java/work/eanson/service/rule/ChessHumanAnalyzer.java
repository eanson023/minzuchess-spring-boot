package work.eanson.service.rule;

import org.springframework.stereotype.Component;
import work.eanson.pojo.Trick;

/**
 * Long live freedom and fraternity, No 996
 * <pre>
 * 统一分析接口
 * </pre>
 *
 * @author eanson
 * @date 2020/6/14
 */
@Component
public interface ChessHumanAnalyzer {
    /**
     * 根据棋盘码找到 具体棋盘 并进行分析
     *
     * @param code
     * @return
     */
    Trick analyze(String code);
}
