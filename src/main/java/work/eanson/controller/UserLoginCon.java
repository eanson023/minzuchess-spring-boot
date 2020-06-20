package work.eanson.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import work.eanson.pojo.Team;
import work.eanson.pojo.UserInfo;
import work.eanson.pojo.UserLogin;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author eanson
 */
@Controller
@RequestMapping(value = "/user", method = {RequestMethod.GET, RequestMethod.POST})
@Api(tags = "用户登录管理")
public class UserLoginCon extends AbstractController {
    /**
     * 是否忽略验证码注册
     */
    @Value("${my.isIgnoreCheckCodeRegister}")
    private boolean isIgnoreCheckCodeRegister;

    /**
     * 登录
     * province_id:false 密码登录
     * true 手机号登录
     * <p>
     * <p>
     * isLocal:
     * 是否是服务端转发过来的
     *
     * @param userLogin
     * @param code
     * @param isPhoneLogin
     * @return
     */
    @RequiresGuest
    @PostMapping(value = "/login", produces = "application/json;charset=utf-8")
    @ResponseBody
    @ApiOperation("用户登录")
    public Result login(UserLogin userLogin, @RequestParam(value = "code", required = false) String code,
                        @RequestParam(value = "is_phone_login") boolean isPhoneLogin,
                        @RequestParam(value = "remember", required = false, defaultValue = "off") String remember) throws Exception {
        HttpServletRequest request = httpServletRequestThreadLocal.get();
        Context context = new Context("login_pwd");
        context.put("userLogin", userLogin);
        boolean rememberMe = "on".equals(remember);
        context.put("remember_me", rememberMe);
        context.put("host", super.getIpAddress());
        try {
            //手机号登录
            if (isPhoneLogin) {
                context.setNewServiceName("login_check_code");
                context.put("code", code);
                csgo.service(context);
            }
            //密码登录
            else {
                csgo.service(context);
            }
        }
        //用户只注册了一半
        catch (LockedAccountException e) {
            e.printStackTrace();
            return Result.fail(MsgCenter.ERROR_REGISTER_ONLY_HALF);
        }
        //验证失败(手机号 或者用户名错误)
        catch (Exception e) {
            e.printStackTrace();
            return Result.fail(MsgCenter.ERROR_CHECK);
        }
        if (!context.isSuccess()) {
            return context.getResult();
        }
        Subject subject = SecurityUtils.getSubject();
        Result result;
        //如果是管理员登录 直接就跳转到管理页面
        if (subject.hasRole("admin")) {
            result = Result.success("/admin");
        } else {
            //根据referer跳转
            String referer = request.getHeader("Referer");
            if (referer.endsWith("/login") || referer.contains("/login")) {
                referer = "/";
            }
            result = Result.success(referer);
        }
        return result;
    }

    /**
     * 注册1
     * 1.验证验证码
     * 2.注册
     *
     * @param userInfo
     * @param userLogin
     * @param response
     * @return
     */
    @PostMapping(value = "/register1", produces = "application/json;charset=utf-8")
    @ResponseBody
    @ApiOperation("用户注册")
    public Result register1(UserInfo userInfo, UserLogin userLogin, String code, HttpServletResponse response) throws Exception {
        Context context = new Context("check_code");
//        是否忽略验证码登录
        if (!isIgnoreCheckCodeRegister) {
            context.put("telephone", userLogin.getTelephone());
            context.put("code", code);
            csgo.service(context);
            //验证码不正确
            if (!context.getResult().isSuccess()) {
                return context.getResult();
            }
        }
        context.clear();
        context.setNewServiceName("user_register1");
        context.put("userInfo", userInfo);
        context.put("userLogin", userLogin);
        csgo.service(context);
        if (context.isSuccess()) {
            Cookie cookie = new Cookie("reg_token", context.remove("reg_token") + "");
            cookie.setPath("/");
            cookie.setMaxAge(30 * 60);
            response.addCookie(cookie);
            //拟定队名
            String url = "/register2?hx=" + UUID.randomUUID().toString() + "&ex=" + context.get("ex_token");
            return Result.success(url);
        }
        return context.getResult();
    }

    /**
     * 注册第二步
     * 1. 检测cookie如果没有cookie就调转到注册第一步
     * 2. 判断是加入还是自己创建
     *
     * @param userInfo
     * @return
     */
    @PostMapping(value = "/register2", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @ApiOperation("用户注册2")
    public Result register2(UserInfo userInfo, Team team, @RequestParam(value = "join", required = false) boolean isJoin,
                            @RequestParam(value = "team_name_num", required = false) String teamNameOrId,
                            HttpServletResponse response) throws Exception {
        HttpServletRequest request = httpServletRequestThreadLocal.get();
        Context context = new Context("");
//        ModelAndView modelAndView = new ModelAndView("register1");
        //1.判断cookie
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Result.fail(MsgCenter.ERROR_PARAMS, "/register1");
        }
        String reqToken = null;
        for (Cookie cookie : cookies) {
            if ("reg_token".equals(cookie.getName())) {
                reqToken = cookie.getValue();
            }
        }
        //没有token 未通过
        if (reqToken == null) {
            return Result.fail(MsgCenter.ERROR_PARAMS, "/register1");
        }
        //将用户手机号token放入容器
        context.put("req_token", reqToken);
        //加入队伍
        try {
            if (isJoin) {
                //查找队伍
                context.setNewServiceName("find_team");
                context.put("team_name_num", teamNameOrId);
                csgo.service(context);
                if (!context.isSuccess()) {
                    return context.getResult();
                }
                //有这个编号或名称的队伍 加入它
                context.setNewServiceName("join_team");
                //加入团队service
                csgo.service(context);
            }
            //创建队伍
            else {
                context.put("userInfo", userInfo);
                context.put("team", team);
                context.setNewServiceName("create_team");
                csgo.service(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(MsgCenter.ERROR, "/index");
        } finally {
            //删除token
            Cookie cookie = new Cookie("reg_token", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        //添加棋盘
        context.setNewServiceName("finish_register");
        csgo.service(context);
        //取得用户信息 去登陆
        UserLogin userLogin = (UserLogin) context.get("userLogin");
        String uri = "/login?tel=" + userLogin.getTelephone() + "&username=" + userLogin.getUsername();
        return Result.success(uri);
    }

    /**
     * 退出登录
     */
    @RequiresAuthentication
    @RequestMapping("/logout")
    @ApiOperation("退出登录")
    public String logout(ModelAndView mv) throws Exception {
        mv.addObject("user", null);
        Context context = new Context("logout");
        csgo.service(context);
        HttpServletRequest request = httpServletRequestThreadLocal.get();
        return "redirect:" + request.getHeader("Referer");
    }

    /**
     * 忘记密码1
     */
    @PostMapping("forget1")
    @ResponseBody
    @ApiOperation("忘记密码1")
    public Result forget1(String telephone, String code, HttpServletResponse response) throws Exception {
        Context context = new Context("check_code");
        context.put("telephone", telephone);
        context.put("code", code);
        csgo.service(context);
        //验证码不正确
        if (!context.getResult().isSuccess()) {
            return context.getResult();
        }
        context.setNewServiceName("forget_pwd");
        context.put("telephone", telephone);
        csgo.service(context);
        if (context.isSuccess()) {
            String token = String.valueOf(context.getResult().getData());
            Cookie cookie = new Cookie("for_token", token);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 30);
            response.addCookie(cookie);
            String uri = "/forget2";
            return Result.success(uri);
        }
        return context.getResult();
    }

    /**
     * 忘记密码2
     *
     * @param password
     * @param repassword
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("forget2")
    @ResponseBody
    @ApiOperation("忘记密码2")
    public Result forget2(String password, String repassword, HttpServletResponse response) throws Exception {
        String forToken = super.getTokenCookie("for_token");
        if (forToken == null) {
            return null;
        }
        Context context = new Context("get_for_token");
        context.put("for_token", forToken);
        csgo.service(context);
        if (!context.isSuccess()) {
            return context.getResult();
        }
        UserLogin userLogin = (UserLogin) context.getResult().getData();
        context.put("telephone", userLogin.getTelephone());
        context.put("username", userLogin.getUsername());
        context.put("password", password);
        context.put("repassword", repassword);
        context.setNewServiceName("update_pwd2");
        csgo.service(context);
        if (context.isSuccess()) {
            Cookie cookie = new Cookie("for_token", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            String uri = "/login?tel=" + userLogin.getTelephone() + "&username=" + userLogin.getUsername();
            return Result.success(uri);
        }
        return context.getResult();

    }
}
