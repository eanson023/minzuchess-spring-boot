package work.eanson.service.rule.jq.test;

import work.eanson.pojo.Trick;
import work.eanson.service.rule.jq.JQ14TrickAIAnalyzer;

/**
 * Long live freedom and fraternity, No 996
 * <pre>
 *
 * </pre>
 *
 * @author eanson
 * @date 2020/8/9
 */
public class JQ14TrickAIAnalyzerTest {

    public static void main(String[] args) {
        String pos = "zZzzzzZzzzzzZZZZZzZzZZzzzZZzZzzzzZZZzzZzZZzZzzZZzZzzZZzZzzZZZZZ0ZZZ0ZZzZ00z0Z0zzz0zZZZZZZZ0Z0Z00zzzZzZzZZZZ000zzZZZzZZZZZ0z00zzZzzZZzZZZzZzzZZZZzZzZZzZZzzzzZZZzZZzZzZzzzZzzZZzZzzzzzzZzzzzZZzZzzZzz000zzzzZZZZ000";
        System.out.print("  ");
        for (int i = 0; i < 14; i++) {
            System.out.printf("%d ", i);
        }
        System.out.println();
        for (int i = 0; i < 14; i++) {
            System.out.printf("%c:", 'A' + i);
            for (int j = 0; j < 14; j++) {
                System.out.printf("%c ", pos.charAt(i * 14 + j));
            }
            System.out.println();
        }
        System.out.println("====================================");
        JQ14TrickAIAnalyzer jq14TrickAIAnalyzer = new JQ14TrickAIAnalyzer();
        Trick trick = new Trick();
        trick.setBefore(pos);
        trick.setTrick("J12,I12 FC-G5");
        trick.setColor("z");
        jq14TrickAIAnalyzer.analyze(trick);
        pos = trick.getBefore();
        System.out.print("  ");
        for (int i = 0; i < 14; i++) {
            System.out.printf("%d ", i);
        }
        System.out.println();
        for (int i = 0; i < 14; i++) {
            System.out.printf("%c:", 'A' + i);
            for (int j = 0; j < 14; j++) {
                System.out.printf("%c ", pos.charAt(i * 14 + j));
            }
            System.out.println();
        }
        String pos2 = "zZzzzzZzzzzzZZZZZzZzZZzzzZZzZzzzzZZZzzZzZZzZzzZZzZzzZZzZzzZZZZZ0ZZZ0ZZzZ00z0Z0zzz0zZZZZZZ00Z0Z00zzzZzZzZZZZ000zzZZZzZZZZZ0z0zzzZzzZZzZZZzZ0zZZZZzZzZZzZZzzzzZZZzZZzZzZzzzZzzZZzZzzzzzzZzzzzZZzZzzZzz000zzzzZZZZ000";
        System.out.println(pos.compareTo(pos2));
        System.out.println(pos.equals(pos2));
    }
}
