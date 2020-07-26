package work.eanson.service.rule.jq;

import work.eanson.pojo.Trick;
import work.eanson.service.rule.ChessAIAnalyzer;
import work.eanson.util.MsgCenter;

import java.util.ArrayList;
import java.util.List;


/**
 * 久棋14路裁判程序
 * <p>
 * trick.setStatus()==4表示提子 用于返回更新棋谱
 *
 * @author eanson
 */
public class JQ14TrickAIAnalyzer implements ChessAIAnalyzer {

    /**
     * 我就要超过80行
     *
     * @param trick
     */
    @Override
    public void analyze(Trick trick) {
        trick.setIsFalse(true);
        String action = trick.getTrick();
        if (action == null) {
            trick.setMessage(MsgCenter.HUMAN_ANALYSE_NOACTION);
            return;
        }
        action = action.trim();
        String before = trick.getBefore();
        String color = trick.getColor();
        int width = getPosWidth(before);

        //临时变量
        StringBuilder sb = new StringBuilder(before);

        //先检测颜色是否正确
        if (color == null || !"z".equals(color.toLowerCase())) {
            trick.setMessage(MsgCenter.CHESS_COLOR_ERROR);
            return;
        }
            /*
                字符串分割成3步
             */
        try {
            /*
            ////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////        布子        ////////////////////////////////////
            ///////////////////////////////////////////////////////////////////////////////////
             */
            //布子
            if (action.length() <= 3 && action.length() > 1) {
                trick.setStatus((byte) 1);
                action = action.trim();
                //获取招法位置的一维坐标
                int res = getTargetIndex(action, width);
                if (res == -1) {
                    trick.setMessage(MsgCenter.ERROR_PARAMS + ":trick");
                    return;
                }
                //判断该坐标下是否有棋子\
                if (!isPosOccupied(before, res, "0")) {
                    trick.setMessage(MsgCenter.CHESS_ALREADY_EXISTS + action);
                    return;
                }
                //最后做更新棋子坐标操作
                sb.replace(res, res + 1, color);
                //检测棋盘是否满了
                if (isCbFull(sb.toString())) {
                    //提子
                    sb = new StringBuilder(getAfterFcCenterChess(sb.toString()));
                    //重置平台让黑子行棋
                    trick.setStatus((byte) 4);
                }
                trick.setIsFalse(false);
                trick.setBefore(sb.toString());
                return;
            }

            /*
            ////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////        行棋 飞子    ////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////
             */

            trick.setStatus((byte) 2);

            int tmp, tmp2 = 0;
            //记录终点 判断棋门用
            int targetPos = 0, qimennum = 0;
            //判断坐标是否单纯移动
            boolean pureMove = false;
            boolean flag;
            //跳吃的坐标
            List<Integer> jumpList = new ArrayList<>();
            //对手颜色
            String rival = "z".equals(color) ? "Z" : "z";
            //分割 H1,H2 TC-L1 FC-L2,L3
            String[] actions = action.split(" ");
            String move = actions[0];
            if (move.length() < 5) {
                //行棋过短
                trick.setMessage(MsgCenter.CHESS_MOVE_ERROR_TOO_SHORT + actions[0]);
                trick.setIsFalse(true);
                return;
            }
            //判断是否是飞子阶段
            boolean isFly = this.checkIsFlyStatus(before, color.charAt(0), width);
            boolean isRealFly = false;
            if (isFly) {
                if (actions.length == 1) {
                    isRealFly = true;
                }
                //判断在飞子时的行棋
                if (actions.length > 1 && actions[1].charAt(0) != 'T') {
                    trick.setStatus((byte) 3);
                    isRealFly = true;
                }
            }

            //H1,H3,H5,H7,H9
                /*
                 行棋判断 ，首坐标 开始应为原颜色，之后坐标都为0
                    最后将首坐标变为0 为坐标变为原颜色
                 */
            String[] movePoses = move.split(",");
            //检测移动距离是否合理

            for (int i = 0; i < movePoses.length - 1; i++) {
                tmp = this.getTargetIndex(movePoses[i], width);
                tmp2 = this.getTargetIndex(movePoses[i + 1], width);
                //判断是否出错
                if (tmp == -1 | tmp2 == -1) {
                    //传入坐标错误
                    trick.setMessage(MsgCenter.CHESS_MOVE_ERROR_POS_ERROR);
                    return;
                }
                //坐标正确的坐标相减，始终为width*2 或2
                int abs = Math.abs(tmp - tmp2);
                if (abs == width | abs == 1) {
                    pureMove = true;
                } else if (!(abs == width * 2 | abs == 2)) {
                    if (!isFly) {
                        //行棋犯规,坐标移动距离过大
                        trick.setMessage(MsgCenter.CHESS_MOVE_DISTANCE_TOO_LONG);
                        return;
                    }
                }
            }
            //棋子移动
            for (int i = 0; i < movePoses.length; i++) {
                tmp = this.getTargetIndex(movePoses[i], width);
                //首坐标
                if (i == 0) {
                    //判断首坐标下是否是当前颜色
                    if (isPosOccupied(before, tmp, color)) {
                        //记录首坐标位置
                        tmp2 = tmp;
                        sb.replace(tmp, tmp + 1, "0");
                    } else {
                        //不是我方棋子
                        trick.setMessage(MsgCenter.CHESS_MOVE_NOT_MYSIDE + movePoses[i]);
                        return;
                    }
                }
                //剩余坐标
                else {
                    //判断最后坐标是不是自己开始的首坐标 因为可能会形成一个回路
                    if (tmp != tmp2) {
                        if (!isPosOccupied(before, tmp, "0")) {
                            //不为空
                            trick.setMessage(MsgCenter.CHESS_MOVE_NOT_NULL + movePoses[i]);
                            return;
                        }
                    }
                    //最后替换尾坐标
                    if (i == movePoses.length - 1) {
                        sb.replace(tmp, tmp + 1, color);
                        targetPos = tmp;
                    }
                }
            }
            //检测移动过程中 越过的棋子是否为对手的棋子
            if (!isRealFly && !pureMove) {
                int midPos;
                for (int i = 0; i < movePoses.length - 1; i++) {
                    //前
                    tmp = this.getTargetIndex(movePoses[i], width);
                    //后
                    tmp2 = this.getTargetIndex(movePoses[i + 1], width);
                    int pos = tmp - tmp2;
                    //上下
                    if (Math.abs(pos) == width * 2) {
                        //pos>0向上移动  pos<0向下
                        midPos = pos > 0 ? tmp - width : tmp + width;
                    }
                    //左右
                    else {
                        //pos>0向左 pos<0向右
                        midPos = pos > 0 ? tmp - 1 : tmp + 1;
                    }
                    if (!isPosOccupied(before, midPos, rival)) {
                        //不为对手棋子
                        trick.setMessage(MsgCenter.CHESS_MOVE_NOT_RIVAL + get2Pos(midPos, width));
                        return;
                    } else {
                        //添加跳吃坐标
                        jumpList.add(midPos);
                    }
                }
            }

            //检测移动后的棋门数
            {

                String chessPos = sb.toString();
                //去除没用的东西
                chessPos = chessPos.substring(0, chessPos.length() - width);
                int row = targetPos / width;
                int col = targetPos % width;
                int minrow = row - 1 < 0 ? row : row - 1;
                int mincol = col - 1 < 0 ? col : col - 1;
                int maxrow = row + 1 < width ? row + 1 : row;
                int maxcol = col + 1 < width ? col + 1 : col;
                for (int i = minrow; i < maxrow; i++) {
                    for (int j = mincol; j < maxcol; j++) {
                        int pos = i * width + j;
                        if (chessPos.charAt(pos) == chessPos.charAt(pos + 1) && chessPos.charAt(pos) == chessPos.charAt(pos + width) && chessPos.charAt(pos) == chessPos.charAt(pos + width + 1)) {
                            qimennum++;
                        }
                    }
                }

            }
            //检测TC和FC数是否与检测的相等
            //移动后可以TC和FC去玩没有招法
            if ((jumpList.size() > 0 | qimennum > 0) & actions.length < 2) {
                trick.setMessage(MsgCenter.CHESS_MOVE_NOT_FC_OR_TC);
                return;
            }
                /*
                    FC-提子:
                        提子必须最多两个
                    TC-跳吃:
                        数量应为行棋坐标数量-1
                            当行棋坐标数字为2时可能为单纯移动或者飞子
                            所以为2时TC可有可无
                    TC-H7,H8
                    TC-H1,H2,H3 FC-H5,H6
                    FC-H2,H3
                 */
            //2：FC或TC中有一个 3:FC和TC都有
            int tcnum = 0, fcnum = 0;
            if (actions.length > 1 && actions.length <= 3) {
                for (int i = 1; i < actions.length; i++) {
                    String xc = actions[i];
                    //TC-H7,H8
                    switch (xc.substring(0, 3)) {
                        case "FC-":
                            //避免 FC-H1 FC-T3这种bug出现
                            if (++fcnum > 1) {
                                trick.setMessage("FC过多");
                                return;
                            }
                            String[] fcArrays = this.splitTcOrFc(xc);
                            if (fcArrays.length != qimennum) {
                                trick.setMessage("行棋犯规：FC提子数量与" + movePoses[0] + "到" + movePoses[movePoses.length - 1] + "后棋门数不相等,请核对!");
                                return;
                            } else {
                                for (String fcArray : fcArrays) {
                                    int targetIndex = this.getTargetIndex(fcArray, width);
                                    if (targetIndex == -1) {
                                        //传入坐标错误
                                        trick.setMessage(MsgCenter.CHESS_MOVE_ERROR_POS_ERROR);
                                        return;
                                    } else {
                                        //该检测是否是对手的棋
                                        if (isPosOccupied(sb.toString(), targetIndex, rival)) {
                                            sb.replace(targetIndex, targetIndex + 1, "0");
                                            //减少棋门数 结束之后看剩下的气门数 是否大于0 如果还大于0那么就是没有提子
                                            qimennum--;
                                        } else {
                                            //提子不是对方棋子
                                            trick.setMessage(MsgCenter.CHESS_MOVE_NOT_RIVAL + fcArray);
                                            return;
                                        }
                                    }
                                }
                            }
                            break;
                        case "TC-":
                            if (pureMove) {
                                //该招法不应该有TC
                                trick.setMessage(MsgCenter.CHESS_MOVE_NO_TC);
                                return;
                            }
                            if (++tcnum > 1) {
                                trick.setMessage("TC过多");
                                return;
                            }
                            String[] tcArrays = this.splitTcOrFc(xc);
                            if (tcArrays.length != jumpList.size()) {
                                //TC跳吃数不够
                                trick.setMessage(MsgCenter.CHESS_MOVE_TC_NUM_BUGOU);
                                return;
                            }
                            int tcLength = tcArrays.length;
                            int mvLength = movePoses.length;
                            //根据移动数量判断TC数量 只移动一位值 tc可有可无
                            boolean a = mvLength == 2 & (tcLength <= 1);
                            boolean b = tcLength == mvLength - 1;
                            if (a | b) {
                                for (String tcArray : tcArrays) {
                                    int targetIndex = this.getTargetIndex(tcArray, width);
                                    if (targetIndex == -1) {
                                        trick.setMessage(MsgCenter.CHESS_MOVE_ERROR_POS_ERROR);
                                        return;
                                    } else {
                                        //检测跳吃的坐标是否和检测到的跳吃坐标相等
                                        flag = false;
                                        tmp = jumpList.remove(0);
                                        if (targetIndex == tmp) {
                                            flag = true;
                                        }
                                        if (flag) {
                                            sb.replace(targetIndex, targetIndex + 1, "0");
                                        } else {
                                            //context.put("msg", "行棋犯规：跳吃坐标:" + tcArray + "不是可吃的棋子");
                                            trick.setMessage(MsgCenter.CHESS_MOVE_TC_POS_DISABLE + tcArray);
                                            return;
                                        }
                                    }
                                }
                            }
                            //不满足需求
                            else {
                                trick.setMessage(MsgCenter.CHESS_MOVE_TC_TOO_LONG);
                                return;
                            }
                            break;
                        default:
                            trick.setMessage(MsgCenter.CHESS_ERROR_PARAMS_ACTION);
                            return;
                    }
                }
                //如果棋门数还大于0 那么就是没有提子操作
                if (qimennum > 0) {
                    trick.setMessage(MsgCenter.CHESS_MOVE_NOT_FC_OR_TC);
                    return;
                }
            }
            //action参数过多
            else {
                if (actions.length != 1) {
                    trick.setMessage("传入招法过多");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            trick.setMessage(MsgCenter.ERROR_PARSE);
            return;
        }
        trick.setIsFalse(false);
        //这是更改后的坐标
        trick.setBefore(sb.toString());
    }

    /**
     * 计算一维坐标转为二维坐标
     *
     * @param index
     * @return
     */
    private String get2Pos(int index, int width) {
        int row = index / width;
        char rowStr = (char) (row + 'A');
        int col = index % width;
        return String.valueOf(rowStr) + col;
    }

    /**
     * 分割TC-T1,T2  或 FC-TC,U2
     *
     * @param str
     * @return
     */
    private String[] splitTcOrFc(String str) {
        return str.substring(str.indexOf("-") + 1).split(",");
    }

    /**
     * 检测当前阶段是否是飞子阶段
     *
     * @param str 棋盘状态
     * @return list.get(0) -->是否为飞子阶段
     * list.get(1) -->是 则有 弱势方棋子颜色 不是 则没有
     */
    private boolean checkIsFlyStatus(String str, char color, int width) {
        int num = 0;
        for (int i = 0; i < str.length() - width; i++) {
            if (str.charAt(i) == color) {
                num++;
            }
            if (num >= width) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算二维坐标转换为一维坐标 H1--->XX
     *
     * @param first
     * @return 棋子坐标index
     */
    private int getTargetIndex(String first, int width) {
        char rowschar = first.charAt(0);
        //判断其是否为大写字母
        if (!Character.isUpperCase(rowschar)) {
            throw new IllegalArgumentException(MsgCenter.CHESS_MOVE_ERROR_POS_ERROR);
        }
        //取后坐標
        String colStr = first.substring(1);
        int row = rowschar - 'A';
        int col;
        try {
            //可能解析错误
            col = Integer.parseInt(colStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
        int pos = row * width + col;
        if (pos > width * width | pos < 0) {
            throw new IllegalArgumentException(MsgCenter.CHESS_MOVE_ERROR_POS_ERROR);
        }
        return pos;
    }


    /**
     * 某一位置坐标是否被某一颜色占用
     *
     * @param pos   一维数组坐标
     * @param index 下标
     * @return 是否被占用
     */
    private boolean isPosOccupied(String pos, int index, String color) {
        return pos.charAt(index) == color.charAt(0);
    }

    /**
     * 检测棋盘是否已满
     *
     * @param pos
     * @return
     */
    public boolean isCbFull(String pos) {
        int width = getPosWidth(pos);
        for (int i = 0; i < pos.length() - width; i++) {
            if (pos.charAt(i) == '0') {
                return false;
            }
        }
        return true;
    }

    /**
     * 提子
     *
     * @param pos
     * @return
     */
    public String getAfterFcCenterChess(String pos) {
        int width = getPosWidth(pos);
        StringBuilder sb = new StringBuilder(pos);
        int pos1, pos2;
        pos1 = (width - 1) / 2 * width + (width - 1) / 2;
        pos2 = width / 2 * width + width / 2;
        //提子
        sb.replace(pos1, pos1 + 1, "0");
        sb.replace(pos2, pos2 + 1, "0");
        return sb.toString();
    }

    /**
     * 获取棋盘宽度
     *
     * @param pos
     * @return
     */
    private int getPosWidth(String pos) {
        return (int) Math.sqrt(pos.length());
    }

}
