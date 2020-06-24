package work.eanson.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import work.eanson.util.Context;
import work.eanson.util.Result;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author eanson
 * 棋盘操作
 */
@Controller
@RequestMapping(value = "chess", produces = "text/plain;charset=utf-8", method = {RequestMethod.GET, RequestMethod.POST})
@Api(tags = "棋谱管理")
@Validated
public class ChessController extends AbstractController {

    /**
     * 前提:非公共棋盘:必须登录并且棋盘是自己的
     *
     * @param code
     * @return
     * @throws Exception
     */
    @GetMapping
    @ApiOperation("打开棋盘页")
    public String gotoChess(String code) throws Exception {
        //回到主页
        if (code == null) {
            return "redirect:/index";
        }
        Context context = new Context("get_page");
        context.put("code", code);
        csgo.service(context);
        if (!context.getResult().isSuccess()) {
//            没有该棋盘码则重定向到主页
            return "redirect:/index";
        } else {
            //转发
            return String.valueOf(context.getResult().getData());
        }
    }

    /**
     * 观战
     *
     * @param alias
     * @return
     */
    @GetMapping("/watch")
    @ApiOperation("观战")
    public String goWatch(@Min(value = 0, message = "参数必须为数字") String alias) throws Exception {
        if (alias == null) {
            return "redirect:/index";
        }
        Context context = new Context("get_page");
        context.put("alias", alias);
        csgo.service(context);
        if (!context.getResult().isSuccess()) {
            return "/index";
        }
        //转发
        return String.valueOf(context.getResult().getData());
    }

    @GetMapping("go_chess_page")
    @ApiOperation("打开棋类介绍界面")
    public String goChessPage() {
        return "chess";
    }

    /**
     * 获取全局状态
     *
     * @param code
     * @param html
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "{html}/global")
    @ResponseBody
    @ApiOperation("获取全局状态")
    public String getStatus(String code, @PathVariable("html") String html) throws Exception {
        Context context = new Context("get_set_chess");
        context.put("html", html);
        context.put("code", code);
        csgo.service(context);
        return (String) context.getResult().getData();
    }

    @RequestMapping(value = "{html}/clock")
    @ResponseBody
    @ApiOperation("设置棋钟")
    public void setClock(String code, @PathVariable("html") String html, String i) throws Exception {
        Context context = new Context("set_clock");
        context.put("html", html);
        context.put("code", code);
        context.put("clock", i);
        csgo.service(context);
    }

    /**
     * 获取日志
     *
     * @param code
     * @param num
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "log", produces = "application/json;charset=utf-8")
    @ResponseBody
    @ApiOperation("获取棋盘日志")
    public List getLog(String code, Integer num, String alias) throws Exception {
        Context context = new Context("get_chess_log");
        context.put("code", code);
        context.put("num", num);
        context.put("alias", alias);
        csgo.service(context);
        return (List) context.getResult().getData();
    }

    @PostMapping(value = "reset")
    @ResponseBody
    @ApiOperation("重置棋盘")
    public void resetChess(String code) throws Exception {
        Context context = new Context("reset_chess");
        context.put("code", code);
        csgo.service(context);
    }

    @PostMapping(value = "clear_cache")
    @ResponseBody
    @ApiOperation("清除缓存")
    public void clearCache(String code) throws Exception {
        Context context = new Context("clear_cache");
        context.put("code", code);
        csgo.service(context);
    }

    @PostMapping(value = "/go_back/{code}/{logId}")
    @ResponseBody
    @ApiOperation("悔棋")
    public void goBack(@NotBlank(message = "棋盘码不能为空") @PathVariable("code") String code, @NotBlank @PathVariable("logId") String logId) throws Exception {
        Context context = new Context("go_back");
        context.put("code", code);
        context.put("log_id", logId);
        csgo.service(context);
    }

    @PostMapping(value = "/go/chess", produces = "application/json;charset=utf-8")
    @ResponseBody
    @ApiOperation("获取棋盘具体URL")
    public Result goChess(@NotBlank(message = "棋盘码或者棋盘编号不能为空") @RequestParam("code_or_alias") String aliasOrCode) {
        Context context = new Context("go_chess");
        context.put("code_or_alias", aliasOrCode);
        try {
            csgo.service(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context.getResult();
    }
}
