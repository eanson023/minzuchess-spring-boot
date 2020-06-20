package work.eanson.service.rule.jq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 生成路径
 * <p>
 * <p>
 * https://leetcode-cn.com/problems/unique-paths-iii/solution/bu-tong-lu-jing-iii-by-leetcode/
 * <p>
 * <p>
 * 1.将提子数和跳吃数先全部看为跳吃数，也就是等于遍历的深度
 * 2.dfs递归： 递归前跟新父节点，递归后返回父节点，以便下次遍历
 *
 * @author eanson
 * @// TODO: 2020-03-13
 */
public class PathGenerator {
    private char[][] grid;
    private int targetRow, targetCol;
    private int sourceRow, sourceCol;
    private int W, H;
    /**
     * 跳
     */
    private int[] xinc = {2, 0, -2, 0};
    private int[] yinc = {0, -2, 0, 2};
    /**
     * 越
     */
    private int[] xmid = {1, 0, -1, 0};
    private int[] ymid = {0, -1, 0, 1};
    private char river;
    private Point target;
    /**
     * 父节点
     */
    private MuxTree<Point> parent;
    /**
     * 目标节点集合
     */
    private List<MuxTree<Point>> targetList = new LinkedList<>();
    /**
     * 路径
     */
    private List<Point> path;
    /**
     * 路径集合
     */
    private List<List<Point>> paths = new ArrayList<>();
    private boolean doFirst = false;

    private int otherNum;

    private final static Logger logger = LoggerFactory.getLogger(PathGenerator.class);

    /**
     * @param sp       出发点
     * @param tp       目标点
     * @param grid     图
     * @param otherNum 吃子和提子的总数量
     */
    public PathGenerator(Point sp, Point tp, char[][] grid, int otherNum) {
        this.grid = grid;
        this.target = tp;
        this.targetRow = tp.row;
        this.targetCol = tp.col;
        this.sourceRow = sp.row;
        this.sourceCol = sp.col;
        this.H = grid.length;
        this.W = grid[0].length;
        this.otherNum = otherNum;
        char myside = grid[sourceRow][sourceCol];
        //初始化根节点
        parent = new MuxTree<>(sp);
        river = myside == 'z' ? 'Z' : 'z';
    }

    public List<List<Point>> getPaths() {
        //首尾相同
        if (sourceRow == targetRow && sourceCol == targetCol) {
            calcLoopPath();
        } else {
            calcCommonPath();
        }
        return paths;
    }

    private void calcLoopPath() {
        dfs2(sourceRow, sourceCol, otherNum);
        getTargets(parent);
        generateLoopPaths();
    }

    private void calcCommonPath() {
        dfs(sourceRow, sourceCol, otherNum);
        getTargets(parent);
        generatePaths();
    }

    /**
     * 深度优先递归
     *
     * @param r    row
     * @param c    col
     * @param deep 深度
     */
    private void dfs(int r, int c, int deep) {
        deep--;
        if (deep < 0) {
            return;
        }
        if (r == targetRow && c == targetCol) {
            logger.info("达到目标");
            return;
        }
        //将走过的路径阻碍
        grid[r][c] = 'X';
        for (int i = 0; i < 4; i++) {
            int row = xinc[i] + r;
            int col = yinc[i] + c;
            int rowMid = xmid[i] + r;
            int colMid = ymid[i] + c;
            if (0 <= row && row < H && 0 <= col && col < W) {
                //越过的点必须为对手的棋 目标点必须为空白
                if (grid[row][col] == '0' && grid[rowMid][colMid] == river) {
                    //插入后更新父节点
                    parent = parent.insert(new Point(row, col));
                    dfs(row, col, deep);
                    //重置到上一个节点
                    parent = parent.getParent();
                }
            }
        }
        //重置图
        grid[r][c] = '0';
    }

    /**
     * 回路
     */
    private void dfs2(int r, int c, int deep) {
        deep--;
        if (deep < 0) {
            return;
        }
        if (doFirst) {
            if (r == targetRow && c == targetCol) {
                logger.info("达到目标");
                return;
            }
        }
        doFirst = true;
        //将走过的路径阻碍 排除自身
        if (sourceRow == r && sourceCol == c) {
            grid[r][c] = '0';
        } else {
            grid[r][c] = 'X';
        }
        for (int i = 0; i < 4; i++) {
            int row = xinc[i] + r;
            int col = yinc[i] + c;
            int rowMid = xmid[i] + r;
            int colMid = ymid[i] + c;
            if (0 <= row && row < H && 0 <= col && col < W) {
                //越过的点必须为对手的棋 目标点必须为空白
                if (grid[row][col] == '0' && grid[rowMid][colMid] == river) {
                    //插入后更新父节点
                    parent = parent.insert(new Point(row, col));
                    dfs2(row, col, deep);
                    //重置到上一个节点
                    parent = parent.getParent();
                }
            }
        }
        //重置图
        grid[r][c] = '0';
    }


    private void generatePaths() {
        for (MuxTree<Point> targetPoint : targetList) {
            path = new LinkedList<>();
            getParent(targetPoint);
            paths.add(path);
        }
    }

    private void generateLoopPaths() {
        for (MuxTree<Point> targetPoint : targetList) {
            path = new LinkedList<>();
            getParent(targetPoint);
            paths.add(path);
        }

        List<List<Point>> newPaths = new ArrayList<>(paths);
        for (List<Point> points : paths) {
            if (points.size() != otherNum + 1) {
                newPaths.remove(points);
            }
        }
        paths = newPaths;
    }

    private void getParent(MuxTree<Point> node) {
        if (!node.hasParent()) {
            path.add(node.getData());
            return;
        }
        getParent(node.getParent());
        path.add(node.getData());
    }

    private void getTargets(MuxTree<Point> node) {
        if (!node.hasChildren()) {
            return;
        }
        for (MuxTree<Point> child : node.getChildren()) {
            getTargets(child);
            if (target.equals(child.getData())) {
                targetList.add(child);
            }
        }

    }


}
