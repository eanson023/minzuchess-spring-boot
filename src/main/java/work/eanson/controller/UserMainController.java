package work.eanson.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import work.eanson.util.Context;

/**
 * @author eanson
 */
@Controller
@RequestMapping(value = "user_main", method = {RequestMethod.GET, RequestMethod.POST})
@Api(tags = "用户主要信息管理")
public class UserMainController extends AbstractController {
    /**
     * 去我的主页
     *
     * @param mv
     * @return
     */
    @RequiresAuthentication
    @RequestMapping("home")
    @ApiOperation("去用户主页")
    public ModelAndView toMyUserInfo(ModelAndView mv) {
        Context context = new Context("get_user_main");
        context.put("is_me", true);
        //没有
        try {
            csgo.service(context);
        } catch (Exception e) {
            e.printStackTrace();
            String referer = httpServletRequestThreadLocal.get().getHeader("Referer");
            mv.setViewName("redirect:" + referer);
            return mv;
        }
        mv.setViewName("user_info");
        //人称
        mv.addObject("person", "我");
        mv.addObject("who", "me");
        //数据
        mv.addObject("user_main", context.getResult().getData());
        return mv;
    }

    /**
     * 去别人的主页
     *
     * @param mv
     * @param username 用户名
     * @return
     */
    @RequestMapping("others/{username}")
    @ApiOperation("去别人主页")
    public ModelAndView toOtherUserInfo(ModelAndView mv, @PathVariable("username") String username) {
        mv.setViewName("user_info");
        Context context = new Context("get_user_main");
        context.put("is_me", false);
        context.put("username", username);
        try {
            csgo.service(context);
        } catch (Exception e) {
            e.printStackTrace();
            String referer = httpServletRequestThreadLocal.get().getHeader("Referer");
            if (referer == null) {
                referer = "/";
            }
            mv.setViewName("redirect:" + referer);
            return mv;
        }
        //人称
        mv.addObject("person", "他");
        mv.addObject("who", "others");
        //数据
        mv.addObject("user_main", context.getResult().getData());
        return mv;
    }

    /**
     * 公开棋盘 或者取消公开
     */
    @PostMapping("public_chess/{code}")
    @RequiresAuthentication
    @ResponseBody
    @ApiOperation("公开棋盘码")
    public void publicOrCancelChessBoard(@PathVariable("code") String code) throws Exception {
        Context context = new Context("public_or_cancel_chess_board");
        context.put("code", code);
        csgo.service(context);
    }


}
