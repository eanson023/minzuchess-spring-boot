package work.eanson.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import work.eanson.util.Context;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author eanson
 */
@Controller
@RequestMapping(value = "/jsp", method = RequestMethod.GET)
@Api(tags = "html页面控制器")
public class ThymeleafController extends AbstractController {
    /**
     * 一系列的零碎页面
     *
     * @return
     */
    @GetMapping("/login_pwd")
    public ModelAndView toLoginPwdPage(ModelAndView mv, HttpServletRequest request) {
        mv.setViewName("form/login_pwd");
        String username = getReferer("username", request);
        if (username == null) {
            return mv;
        }
        mv.addObject("username", username);
        return mv;
    }

    @GetMapping("/login_phone")
    public ModelAndView toLoginPhone(ModelAndView mv, HttpServletRequest request) {
        mv.setViewName("form/login_phone");
        String telephone = getReferer("tel", request);
        if (telephone == null) {
            return mv;
        }
        mv.addObject("telephone", telephone);
        return mv;
    }

    @GetMapping("/search_team")
    public String toSchoolPage() {
        return "form/search_team";
    }


    @GetMapping("/create_team")
    public ModelAndView toCreateTeam(ModelAndView mv, HttpServletRequest request) {
        mv.setViewName("form/create_team");
        //从header中取出token
        String exToken = getReferer("ex", request);
        if (exToken == null) {
            return mv;
        }
        Context context = new Context("get_example");
        context.put("ex", exToken);
        try {
            csgo.service(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mv.addObject("example_team_name", context.remove("example_team_name"));
        return mv;
    }

    @GetMapping("/personal2")
    public ModelAndView toPersonal2Page(ModelAndView mv) {
        mv.setViewName("form/personal2");
        return mv;
    }

    @GetMapping("folk2")
    public ModelAndView getFolk2Jsp(ModelAndView mv) {
        mv.setViewName("form/folk2");
        return mv;
    }

    @GetMapping("school2")
    public ModelAndView getSchool2Jsp(ModelAndView mv) {
        mv.setViewName("form/school2");
        return mv;
    }

    @GetMapping("/log")
    public ModelAndView toLog(ModelAndView mv, String code, String alias) {
        Context context = new Context("get_chess_log");
        context.put("code", code);
        context.put("alias", alias);
        try {
            csgo.service(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mv.addObject("log", context.getResult().getData());
        mv.addObject("code", context.get("code"));
        mv.addObject("alias", context.get("alias"));
        mv.addObject("isChess", context.get("is_chess"));
        mv.setViewName("log/log");
        return mv;
    }

    @GetMapping("replay")
    public ModelAndView toReplay(ModelAndView mv) {
        mv.setViewName("log/replay");
        return mv;
    }

    /**
     * 获取之前跳转路径的参数
     *
     * @param param
     * @param request
     * @return
     */
    private String getReferer(String param, HttpServletRequest request) {
        Map<String, String> params = new HashMap<>(5);
        try {
            String referer = request.getHeader("Referer");
            referer = referer.substring(referer.lastIndexOf('?') + 1);
            String[] split = referer.split("&");
            for (String s : split) {
                String key = s.substring(0, s.indexOf('='));
                String value = s.substring(s.indexOf('=') + 1);
                params.put(key, value);
            }
        } catch (Exception e) {
            return null;
        }
        return params.get(param);
    }
}
