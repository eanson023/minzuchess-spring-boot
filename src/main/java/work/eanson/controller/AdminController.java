package work.eanson.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import work.eanson.pojo.UserInfo;
import work.eanson.pojo.UserLogin;
import work.eanson.pojo.back.page.DataTablesBack;
import work.eanson.pojo.back.page.DataTablesIn;
import work.eanson.util.Context;

/**
 * @author eanson
 * @create 2020-03-28 15:00
 */
@Controller
@RequestMapping("admin")
@Api(tags = "管理员管理")
public class AdminController extends AbstractController {
    private static final Logger logger = LogManager.getLogger(AdminController.class);

    @RequiresRoles("admin")
    @RequestMapping(value = {"", "index"})
    @ApiOperation("打开管理员页面")
    public ModelAndView index(ModelAndView mv) throws Exception {
        mv.addObject("original", "/admin/index");
        mv.setViewName("admin/index");
        Context context = new Context("get_admin_index");
        csgo.service(context);
        mv.addObject("code", context.get("code"));
        mv.addObject("categories", context.get("categories"));
        return mv;
    }

    /**
     * original用于导航栏active使用
     *
     * @param mv
     * @return
     */
    @RequiresRoles("admin")
    @RequestMapping("user/user_log")
    @ApiOperation("打开用户日志页面")
    public ModelAndView userLog(ModelAndView mv) {
        mv.setViewName("admin/user_log");
        mv.addObject("original", "/admin/user/user_log");
        return mv;
    }

    @RequiresRoles("admin")
    @RequestMapping("user/user_info")
    @ApiOperation("打开用户信息页面")
    public ModelAndView userInfo(ModelAndView mv) {
        mv.setViewName("admin/user_info");
        mv.addObject("original", "/admin/user/user_info");
        return mv;
    }

    @RequiresRoles("admin")
    @RequestMapping("cheep/cheep_info")
    @ApiOperation("打开棋谱信息页面")
    public ModelAndView trickInfo(ModelAndView mv) {
        mv.setViewName("admin/cheep_info");
        mv.addObject("original", "/admin/cheep/cheep_info");
        return mv;
    }

    @RequiresRoles("admin")
    @RequestMapping("chess/chess_info")
    @ApiOperation("打开棋盘信息页面")
    public ModelAndView chessInfo(ModelAndView mv) {
        mv.setViewName("admin/chess_info");
        mv.addObject("original", "/admin/chess/chess_info");
        return mv;
    }

    @RequiresRoles("admin")
    @PostMapping("/user/user_add")
    @ApiOperation("打开添加用户页面")
    public ModelAndView addUser(ModelAndView mv, UserInfo userInfo, UserLogin userLogin) throws Exception {
        Context context = new Context("admin_add_user");
        context.put("user_info", userInfo);
        context.put("user_login", userLogin);
        csgo.service(context);
        mv.addObject("msg", context.get("msg"));
        mv.setViewName("forward:/admin");
        return mv;
    }

    @RequiresRoles("admin")
    @PostMapping("/chess/cb_add")
    @ApiOperation("打开棋盘添加页面")
    public ModelAndView addCb(ModelAndView mv, String code, @RequestParam("category") Byte categoryKey) throws Exception {
        Context context = new Context("admin_add_cb");
        context.put("code", code);
        context.put("category_key", categoryKey);
        csgo.service(context);
        mv.addObject("msg", context.get("msg"));
        mv.setViewName("forward:/admin");
        return mv;
    }

    @RequiresRoles("admin")
    @PostMapping("/user/user_log.json")
    @ResponseBody
    @ApiOperation("根据筛选条件获取用户日志")
    public DataTablesBack getUserLog(@RequestBody DataTablesIn dti) throws Exception {
        Context context = new Context("get_user_log");
        context.put("data_tables_in", dti);
        csgo.service(context);
        return (DataTablesBack) context.getResult().getData();
    }

    @RequiresRoles("admin")
    @PostMapping("/user/user_info.json")
    @ResponseBody
    @ApiOperation("根据筛选条件获取用户信息")
    public DataTablesBack getUserInfo(@RequestBody DataTablesIn dti) {
        Context context = new Context("get_user_info");
        context.put("data_tables_in", dti);
        try {
            csgo.service(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (DataTablesBack) context.getResult().getData();
    }

    @RequiresRoles("admin")
    @PostMapping("/cheep/cheep_info.json")
    @ResponseBody
    @ApiOperation("根据筛选条件获取棋谱信息")
    public DataTablesBack getCheepInfo(@RequestBody DataTablesIn dti) throws Exception {
        Context context = new Context("get_cheep_info");
        context.put("data_tables_in", dti);
        csgo.service(context);
        return (DataTablesBack) context.getResult().getData();
    }

    @RequiresRoles("admin")
    @PostMapping("/chess/chess_info.json")
    @ResponseBody
    @ApiOperation("根据筛选条件获取棋盘信息")
    public DataTablesBack getChessInfo(@RequestBody DataTablesIn dti) throws Exception {
        Context context = new Context("get_chess_info");
        context.put("data_tables_in", dti);
        csgo.service(context);
        return (DataTablesBack) context.getResult().getData();
    }


}
