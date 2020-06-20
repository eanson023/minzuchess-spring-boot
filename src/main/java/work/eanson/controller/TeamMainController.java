package work.eanson.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

/**
 * @author eanson
 * 队伍主页
 */
@Controller
@RequestMapping(value = "team_main", method = {RequestMethod.GET, RequestMethod.POST})
@Api(tags = "队伍主页管理")
public class TeamMainController extends AbstractController {
    @RequestMapping("{teamId}")
    @ApiOperation("去我的队伍主页")
    public ModelAndView goToMyTeam(@PathVariable("teamId") String teamId, ModelAndView mv) throws Exception {
        Context context = new Context("get_team_main");
        context.put("team_id", teamId);
        csgo.service(context);
        mv.addObject("team_main", context.get("team_main"));
        mv.addObject("page_info", context.get("page_info"));
        mv.setViewName("team_info");
        return mv;
    }

    @RequestMapping("get_team_user/{teamId}")
    @ResponseBody
    @ApiOperation("去别人的队伍主页")
    public Result getTeamInfo(@PathVariable(name = "teamId") String teamId, Integer pageNum, Integer size) {
        Context context = new Context("get_team_user");
        context.put("team_id", teamId);
        context.put("page_num", pageNum);
        context.put("size", size);
        try {
            csgo.service(context);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(MsgCenter.ERROR);
        }
        return context.getResult();
    }
}
