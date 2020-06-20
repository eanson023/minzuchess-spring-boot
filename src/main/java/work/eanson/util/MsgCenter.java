package work.eanson.util;

/**
 * @author eanson
 */
public class MsgCenter {
    static final String OK = "OK";

    public static final String ERROR = "服务器内部错误";

    public static final String ERROR_PARAMS = "参数错误";

    public static final String ERROR_PHONE_EXIPRE = "手机号不正确或验证信息过期";

    public static final String ERROR_CHECK = "验证失败";

    public static final String ERROR_CHECK_SAME = "请勿重复获取验证码";

    public static final String ERROR_CHECK_PHONE_CODE = "手机号或验证码错误";

    public static final String ERROR_FIND_TEAM = "没有找到该队伍,请核对信息,重试吧";

    public static final String ERROR_REGISTER_ONLY_HALF = "您的账户出现问题，请重新去<a href='/register1' class='alert-link'>注册</a>吧";

    public static final String ERROR_TOKEN = "没有token";

    public static final String ERROR_SESSION_EXPIRED = "会话超时";

    public static final String NOT_FOUND = "没有找到请求的资源";

    public static final String USER_USERNAME_EXISTS = "该用户名已被注册";

    public static final String USER_EXISTS = "您已注册，直接去<a href='/login' class='alert-link'>登录</a>吧";

    public static final String USER_HAVE_LOGIN = "您已经登陆，无需再次登陆";

    public static final String USER_NOT_LOGIN = "请先登陆";

    public static final String EMPTY_USERNAME = "用户名不能为空";

    public static final String EMPTY_PASSWORD = "密码不能为空";

    public static final String ERROR_PASSWORD = "密码错误";

    public static final String ERROR_ORIGINAL_PASSWORD = "原密码不正确";

    public static final String ERROR_SAVE_PASSWORD = "修改的密码不能与上一次相同";

    public static final String ERROR_TWICE_PASSWORD = "两次密码不一致";

    public static final String EMPTY_PHONE = "手机号不能为空";

    public static final String PHONE_NOT_EXISTS = "您还不是民族棋网的用户,还是先去注册一个吧";

    public static final String PHONE_EXISTS = "该手机号已被注册";


    public static final String TEAM_NAME_EXISTS = "该队伍名已被存在，换一个试试吧";

    public static final String ERROR_PHONE = "请填入真实的手机号码";

    public static final String ERROR_USERNAME_OR_PASSWORD = "用户名或密码错误";

    public static final String ERROR_PASSWORD_FORMAT = "密码格式不对";

    public static final String USER_IS_ADD = "此用户已经被后台添加，联系管理员索要密码吧";

    public static final String LOGIN_NOT_ALLOW = "没有权限，禁止登陆";

    public static final String ERROR_FILE_TYPE = "不支持的文件类型";

    public static final String ERROR_CODE = "该棋盘码已经存在";

    public static final String ERROR_CODE_404 = "该棋盘码不存在";

    public static final String ERROR_CATEGORY = "该分类错误";

    public static final String ERROR_PARSE = "解析错误，请核对";

    public static final String CODE_EXISTS_AND_ROLLBACK = "该棋盘码已经存在，导致全部添加失败，请核对";

    public static final String CATEGORY_EXISTS = "该棋种已经存在";

    public static final String CHESS_ALREADY_EXISTS = "参数错误,该坐标已有棋子:";

    public static final String CHESS_MOVE_ERROR_TOO_SHORT = "参数错误,移动距离过短:";

    public static final String CHESS_MOVE_ERROR_POS_ERROR = "传入坐标错误";

    public static final String CHESS_MOVE_DISTANCE_TOO_LONG = "行棋犯规,移动距离过大";

    public static final String CHESS_MOVE_TC_TOO_LONG = "行棋犯规：TC跳吃过多,请核对!";

    public static final String CHESS_MOVE_NOT_MYSIDE = "行棋犯规:该坐标不是你的棋子:";

    public static final String CHESS_MOVE_NOT_RIVAL = "行棋犯规:该坐标不是对方棋子:";

    public static final String CHESS_MOVE_NOT_NULL = "行棋犯规:该坐标不为空白:";

    public static final String CHESS_MOVE_NOT_FC_OR_TC = "行棋犯规:该招法无TC或FC";

    public static final String CHESS_MOVE_NO_TC = "行棋犯规:该招法不应该有TC";

    public static final String CHESS_MOVE_TC_NUM_BUGOU = "TC跳吃数不够，请核对";

    public static final String CHESS_MOVE_TC_POS_DISABLE = "行棋犯规：该跳吃坐标不是可吃的棋子:";

    public static final String CHESS_ERROR_PARAMS_ACTION = "参数:action错误,请核对";

    public static final String CHESS_PARSE_ERROR = "招法解析错误";

    public static final String CHESS_COLOR_ERROR = "颜色错误";


    //人类
    public static final String HUMAN_ANALYSE_ERROR = "招法解析错误";

    public static final String HUMAN_ANALYSE_NOACTION = "没有招法";

    public static final String CATEGORY_404_NOT_FOUND = "该棋盘类别为找到";

    public static final String ERROR_TEAM_NAME_ALREDY_HAVE = "该名称已被占用";

    public static final String ERROR_OPTION = "错误操作";

    public static final String ERROR_TEAM_ALREADY_TEAMMATES = "你已经是该队成员了";

    public static final String ERROR_CODE_NOT_BELONG = "该棋盘码不属于您";

    public static final String ERROR_INTERNAL_NOTRICK = "该时间段没有记录";

    public static final String ERROR_NO_TEAM = "没有找到该队伍";

}
