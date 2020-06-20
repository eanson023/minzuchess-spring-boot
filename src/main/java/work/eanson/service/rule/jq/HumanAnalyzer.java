package work.eanson.service.rule.jq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import work.eanson.pojo.Trick;
import work.eanson.service.rule.ChessHumanAnalyzer;
import work.eanson.util.MsgCenter;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 1维坐标转为棋普
 * 000000000000000
 * 000000z00000000
 * <p>
 * 1.所有坐标1维转2维
 * 2.除第一次外 每个坐标跟上一次比较异同
 * <p>
 * 将不同的点 记录下来
 * 记录完之后 ，将所有点与第一次点向比较，看那些点多了 那些点少了
 * <p>
 * 少：可能被移动，可能被提子，可能被跳吃
 * 多：移动，布子
 * <p>
 * 布子：仅一条数据
 * 飞子：
 *
 * @author eanson
 * @// TODO: 2020-03-13
 */

@Component
public class HumanAnalyzer implements ChessHumanAnalyzer {
    @Autowired
    private JedisPool jedisPool;

    private final static Logger logger = LoggerFactory.getLogger(HumanAnalyzer.class);

    /**
     * 我就要超过80行
     *
     * @param code
     * @return
     */
    @Override
    public Trick analyze(String code) {

        Jedis jedis = jedisPool.getResource();
        String key = "qipu_zset_" + code;
        //得到这次的所有棋谱;
        Set<String> zrange = jedis.zrange(key, 0, -1);
        //清空
        jedis.del(key);
        jedis.close();

        Trick trick = new Trick();
        char color;
        trick.setType((byte) 2);
        trick.setCbCode(code);
        trick.setIsFalse(false);

        if (zrange.size() == 1) {
            trick.setTrick("none");
            trick.setMessage(MsgCenter.HUMAN_ANALYSE_NOACTION);
            trick.setStatus((byte) 0);
            trick.setIsFalse(true);
            return trick;
        }

        //移动二维数组
        char[][][] poses = new char[zrange.size()][][];

        Set<Point> mvSet = new LinkedHashSet<>();
        LinkedList<Point> otherList = new LinkedList<>();
        LinkedList<Point> buziList = new LinkedList<>();

        /**
         *
         * 思路 移动 总数不变
         *     布子 数字+
         *     提子 数字-
         */
        int i = 0;
        for (String s : zrange) {
            if (i == 0) {
                trick.setBefore(s);
            }
            logger.info(s);
            poses[i++] = to2Dimensional(s);
        }
        for (i = 0; i < poses.length - 1; i++) {
            int sum1 = 0;
            int sum2 = 0;
            Point[] different = new Point[2];
            int l = 0;
            for (int j = 0; j < poses[0].length; j++) {
                for (int k = 0; k < poses[i][0].length; k++) {
                    if (poses[i][j][k] != '0') {
                        sum1++;
                    }
                    if (poses[i + 1][j][k] != '0') {
                        sum2++;
                    }
                    //开始比较  不同的肯定有两个
                    if (poses[i][j][k] != poses[i + 1][j][k]) {
                        Point point = new Point();
                        point.row = j;
                        point.col = k;
                        different[l++] = point;
                    }
                }
            }
            //布子
            if (sum1 < sum2) {
                buziList.add(different[0]);
            }
            //提子
            else if (sum1 > sum2) {
                otherList.add(different[0]);
            }
            //移动  考虑先后顺序问题
            else {
                //前面的变了  顺序问题 防止总是前面的被检查 && poses[i][different[1].row][different[1].col] != '0'
                //后面个往前移动
                for (int j = 0; j < 2; j++) {
                    if (poses[i][different[j].row][different[j].col] != '0') {
                        mvSet.add(different[j]);
                        mvSet.add(different[1 - j]);
                        break;
                    }
                }
            }

        }
        LinkedList<Point> mvList = new LinkedList<>(mvSet);

        //布子
        if (!buziList.isEmpty()) {
            Point pop = buziList.remove();
//            if (poses[1][pop.row][pop.col] != '0') {
            String mv0 = toXy(pop.row, pop.col);
            String[] mv = new String[1];
            mv[0] = mv0;
            color = poses[1][pop.row][pop.col];
            logger.info("布子");
            trick.setColor(color + "");
            trick.setTrick(generateQipu(mv));
            trick.setStatus((byte) 1);
            return trick;
//            }
//            //可能是错误的提交信息，有可能点错了 更新后的棋盘必须为Z或z
//            trick.setIsFalse(true);
//            trick.setMessage("解析错误，请核对");
//            return trick;
        }
        //正式开始计算
//
//         *
//         * +: 移动到目标
//         * -: 原坐标被移动，提子，跳吃
//         *
//         *
//         * 思路：先分类，然后根据最后个添加的棋子往回推
//         *              1。仅行棋，和飞子的长度 tc,fc没有，这种情况直接组装返回
//         *              2.dfs
//         *

        int posLast = poses.length - 1;
        //设置颜色
        color = poses[posLast][mvList.getLast().row][mvList.getLast().col];
        trick.setColor(color + "");

        trick.setStatus((byte) 2);
        /*
          / / / / / / / / / / / / / / / /
         / / / / /解决回路问题 / / / / / /
        / / / / / / / / / / / / / / / /

        思路 回路 1.第一个点和最后个点肯定想等
                 2.第一个点颜色和最后次颜色相同
         */
        {
            Point first = mvList.getFirst();
            if (poses[0][first.row][first.col] == poses[posLast][first.row][first.col]) {
                mvList.add(first);
                color = poses[0][first.row][first.col];
                trick.setColor(color + "");
            }
        }
        //仅移动但有提子
        if (mvList.size() == 2) {
            Point sp = mvList.get(0);
            Point tp = mvList.get(1);
            int absX = Math.abs(sp.col - tp.col);
            int absY = Math.abs(sp.row - tp.row);
            //走了一步
            boolean can = (absX == 1 && absY == 0) || (absX == 0 && absY == 1);
            if (can) {
                logger.info("仅移动但有提子");
                String qip = generateQipu(mvList.toArray(new Point[0]), null, otherList.toArray(new Point[0]));
                trick.setTrick(qip);
                return trick;
            }
        }
        /*
          / / / / / / / / / / / / / / / /
         / / / / / / / / / / / / / / / /
         */
        //跳跃开始分析路径  根据起点和终点
        PathGenerator pathGenerator = new PathGenerator(mvList.getFirst(), mvList.getLast(), poses[0], otherList.size());
        List<List<Point>> paths = pathGenerator.getPaths();
        int size = paths.size();
        //没有匹配到路径 可能是错误信息了
        if (size == 0) {
            //可能是飞子，让规则检测函数去判断
            String s = generateQipu(mvList.toArray(new Point[0]), null, otherList.toArray(new Point[0]));
            trick.setTrick(s);
            return trick;
        }
        //规定  如果有多条路径 则必须按需要规定路径走
        //有时玩家可能会走错 所以玩家的集合>系统遍历的最优路径
        // 但有时候 玩家直接起点到终点 所以玩家的集合<系统遍历的最优路径
        else if (size == 1) {
            mvList = (LinkedList<Point>) paths.get(0);
        } else {
            //多条得到最优路径
            //如果是回路 两边都通 那么还需要判断顺序问题 按照吃的顺序 获得最优路径
            int index = 0;
            for (int i1 = 0; i1 < paths.size(); i1++) {
                List<Point> points = paths.get(i1);
                //首先判断生成路径和人类路径数是否等
                if (points.size() != mvList.size()) {
                    trick.setMessage("多条路径,请逐条执行");
                    trick.setTrick("none");
                    trick.setIsFalse(true);
                    return trick;
                }
                boolean flag = false;
                for (int j = 1; j < points.size(); j++) {
                    if (!points.get(j).equals(mvList.get(j))) {
                        flag = false;
                        break;
                    }
                    flag = true;

                }
                //留下唯一值
                if (flag) {
                    index = i1;
                    break;
                }
            }
            mvList = (LinkedList<Point>) paths.get(index);
        }
        //计算跳跃  剩下的就是提子  犯规了交给裁判程序处理
        {
            List<Point> tcList = new LinkedList<>();
            List<Point> fcList;
            for (int j = 1; j < mvList.size(); j++) {
                Point sp = mvList.get(j - 1);
                Point tp = mvList.get(j);
//            4,5->2,5上
//            4,5->6,5下
//            4,5->4,3左
//            4,5->4,7右
                int jumpRow = (sp.row + tp.row) / 2;
                int jumpCol = (sp.col + tp.col) / 2;
                Point mid = new Point(jumpRow, jumpCol);
                if (otherList.contains(mid)) {
                    tcList.add(mid);
                }
            }
            //删除了tc跳跃的 剩下的吗，默认是fc
            otherList.removeAll(tcList);
            fcList = otherList;
            trick.setTrick(generateQipu(mvList.toArray(new Point[0]), tcList.toArray(new Point[0]), fcList.toArray(new Point[0])));
        }
        return trick;
    }

    private char[][] to2Dimensional(String pos) {
        int width = (int) Math.sqrt(pos.length());
        char[][] xy = new char[width][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                int index = i * width + j;
                xy[i][j] = pos.charAt(index);
            }
        }
        return xy;
    }

    private String toXy(int row, int col) {
        char nrow = (char) ('A' + row);
        return nrow + "" + col;
    }

    private String toXy(Point point) {
        return toXy(point.row, point.col);
    }

    private String[] toXys(Point[] point) {
        String[] s = new String[point.length];
        for (int i = 0; i < point.length; i++) {
            s[i] = toXy(point[i]);
        }
        return s;
    }


    private String generateQipu(Point[] mv) {
        return generateQipu(mv, null, null);
    }

    private String generateQipu(String[] mv) {
        return generateQipu(mv, null, null);
    }


    private String generateQipu(Point[] mv, Point[] tc, Point[] fc) {
        String[] mvs = new String[mv.length];
        for (int i = 0; i < mv.length; i++) {
            mvs[i] = toXy(mv[i]);
        }
        String[] tcs = null;
        if (tc != null && tc.length > 0) {
            tcs = new String[tc.length];
            for (int i = 0; i < tc.length; i++) {
                tcs[i] = toXy(tc[i]);
            }
        }
        String[] fcs = null;
        if (fc != null && fc.length > 0) {
            fcs = new String[fc.length];
            for (int i = 0; i < fc.length; i++) {
                fcs[i] = toXy(fc[i]);
            }
        }
        return generateQipu(mvs, tcs, fcs);
    }

    private String generateQipu(String[] mv, String[] tc, String[] fc) {
        StringBuilder sb = new StringBuilder();
        sb.append(mv[0]);
        if (mv.length == 1) {
            return sb.toString();
        }
        for (int i = 1; i < mv.length; i++) {
            sb.append(',');
            sb.append(mv[i]);
        }
        //tc
        if (tc != null) {
            sb.append(" TC-").append(tc[0]);
            for (int i = 1; i < tc.length; i++) {
                sb.append(',');
                sb.append(tc[i]);
            }
        }
        //fc
        if (fc != null) {
            sb.append(',');
            sb.append(" FC-").append(fc[0]);
            for (int i = 1; i < fc.length; i++) {
                sb.append(',').append(fc[1]);
            }
        }
        return sb.toString();
    }
}
