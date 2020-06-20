package work.eanson.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;


/**
 * @author eanson
 */
@Controller
@RequestMapping(value = "/user_info", method = {RequestMethod.GET, RequestMethod.POST})
@Api(tags = "用户信息管理")
public class UserInfoController extends AbstractController {
    /**
     * 头像上传
     * <p>
     * 换至service层存储
     *
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping("upload")
    @RequiresAuthentication
    @ResponseBody
    @ApiOperation("头像上传")
    public Result uploadAvatar(@RequestParam("avatar") MultipartFile file) throws Exception {
        Context context = new Context("avatar_upload");
        context.put("avatar", file);
        try {
            csgo.service(context);
        } catch (IllegalArgumentException e) {
            return Result.fail(MsgCenter.ERROR);
        }
        return Result.success();
    }

    @RequestMapping("update_pwd")
    @RequiresAuthentication
    @ResponseBody
    @ApiOperation("更新密码")
    public Result updatePwd(String original, String password) {
        Context context = new Context("update_pwd");
        context.put("original", original);
        context.put("password", password);
        try {
            csgo.service(context);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(MsgCenter.ERROR_SESSION_EXPIRED);
        }
        return context.getResult();
    }

}
