package work.eanson.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import work.eanson.pojo.UserLogin;
import work.eanson.util.Context;
import work.eanson.util.Result;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


/**
 * @author eanson
 */
@Controller
@Api(tags = "通用控制器")
@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
public class CommonController extends AbstractController {

    @RequestMapping("/haha")
    @ResponseBody
    public Result test() {
        Context context = new Context("test");
        try {
            csgo.service(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(context.getResult().getMsg());
        return context.getResult();
    }

    @GetMapping({"/", "/index"})
    @ApiOperation("打开主页")
    public ModelAndView toIndex(ModelAndView mv) {
        mv.setViewName("index");
        return mv;
    }

    @GetMapping("/team")
    @ApiOperation("打开队伍页")
    public ModelAndView toTeam(ModelAndView mv) throws Exception {
        Context context = new Context("all_team");
        csgo.service(context);
        mv.addObject("teamPages", context.get("teamPages"));
        mv.setViewName("team");
        return mv;
    }

    @GetMapping("/public_record")
    @ApiOperation("打开公共棋盘页")
    public ModelAndView toPublicRecord(ModelAndView mv) throws Exception {
        mv.setViewName("public_record");
        Context context = new Context("public_record");
        csgo.service(context);
        mv.addObject("cheepInfo", context.getResult().getData());
        return mv;
    }

    @GetMapping("/about")
    @ApiOperation("打开关于页")
    public ModelAndView toAbout(ModelAndView mv) {
        mv.setViewName("about");
        return mv;
    }


    /**
     * 必须是游客登录
     *
     * @return
     */
    @RequiresGuest
    @GetMapping("/login")
    @ApiOperation("登录")
    public String login() {
        return "login";
    }

    @RequestMapping("/register1")
    @ApiOperation("打开注册页1")
    public String toRegister() {
        return "register1";
    }

    /**
     * 不能直接进入第二步register2
     *
     * @param request
     * @return
     */
    @RequestMapping("/register2")
    @ApiOperation("打开注册页2")
    public ModelAndView toRegister2(String ex, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("redirect:register1");
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return modelAndView;
        }
        for (Cookie cookie : cookies) {
            if ("reg_token".equals(cookie.getName())) {
                modelAndView.setViewName("register2");
                return modelAndView;
            }
        }
        return modelAndView;
    }

    @RequestMapping("/forget1")
    @ApiOperation("忘记密码1")
    public String forget1() {
        return "forget1";
    }

    /**
     * 不能直接进入第二步
     *
     * @param mv
     * @return
     * @throws Exception
     */
    @RequestMapping("forget2")
    @ApiOperation("忘记密码2")
    public ModelAndView forget2(ModelAndView mv) throws Exception {
        String forToken = super.getTokenCookie("for_token");
        if (forToken == null) {
            mv.setViewName("redirect:/forget1");
            return mv;
        }
        Context context = new Context("get_for_token");
        context.put("for_token", forToken);
        csgo.service(context);
        if (!context.isSuccess()) {
            mv.setViewName("redirect:/forget1");
            return mv;
        }
        UserLogin userLogin = (UserLogin) context.getResult().getData();
        mv.addObject("telephone", userLogin.getTelephone());
        mv.addObject("username", userLogin.getUsername());
        mv.setViewName("forget2");
        return mv;
    }

    @PostMapping("/get_code")
    @ResponseBody
    @ApiOperation("获取验证码")
    public Result getCode(String telephone, @RequestParam("now_time") String nowTime, @RequestParam("is_new") boolean isNew) {
        Context context = new Context("get_code");
        context.put("telephone", telephone);
        context.put("now_time", nowTime);
        context.put("is_new", isNew);
        try {
            csgo.service(context);
            if (!context.isSuccess()) {
                return context.getResult();
            }
            context.setNewServiceName("save_code");
            //保存验证码值redis
            csgo.service(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context.getResult();
    }

    @RequestMapping("/get_province")
    @ResponseBody
    @ApiOperation("获取省份信息")
    public Result getAllProvince() throws Exception {
        Context context = new Context("get_province");
        csgo.service(context);
        return context.getResult();
    }

    @RequestMapping("/get_school")
    @ResponseBody
    @ApiOperation("获取学校信息")
    public Result getSchool(@RequestParam("pro_id") Integer proId) throws Exception {
        Context context = new Context("get_school");
        context.put("province_id", proId);
        csgo.service(context);
        return context.getResult();
    }

}
