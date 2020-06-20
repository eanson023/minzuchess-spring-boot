package work.eanson.service.team;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import work.eanson.dao.TeamAvatarDao;
import work.eanson.dao.TeamDao;
import work.eanson.pojo.Team;
import work.eanson.pojo.TeamAvatar;
import work.eanson.pojo.back.UserSession;
import work.eanson.service.base.BaseService;
import work.eanson.service.base.GlobalService;
import work.eanson.service.base.Input;
import work.eanson.util.Context;
import work.eanson.util.MsgCenter;
import work.eanson.util.Result;

import java.io.File;
import java.util.UUID;
import java.util.zip.DataFormatException;

/**
 * 更新队伍信息 队伍头像
 *
 * @author eanson
 */
@Service("update_team")
public class UpdateTeamServiceImpl extends BaseService implements GlobalService {
    @Autowired
    private TeamDao teamDao;
    @Autowired
    private TeamAvatarDao teamAvatarDao;

    @Transactional(rollbackFor = Exception.class)
    @Input(required = {"team", "file"}, optional = "fileName")
    @Override
    public void service(Context context) throws Exception {
        Team team = (Team) context.get("team");
        UserSession principal = (UserSession) SecurityUtils.getSubject().getPrincipal();
        String telephone = principal.getTelephone();
        String teamId = team.getTeamId();
        //检查是不是队长
        int i1 = teamDao.checkIsLeaderJoinTeamUserByPrimaryKey(teamId, telephone);
        context.setResult(Result.fail(MsgCenter.ERROR_OPTION));
        if (i1 == 0) {
            return;
        }
//        if (!common.isTeamName(team.getTeamName()) || !common.isIntroduction(team.getIntroduction())) {
//            return;
//        }
        teamDao.updateByPrimaryKeySelective(team);
        MultipartFile multipartFile = (MultipartFile) context.get("file");
        if (multipartFile.getSize() > 2 * 1024 * 1024) {
            throw new DataFormatException("文件过大");
        }
        //上传的位置
        String absolutePath = "/data/minzuchess/static/img/avatar";
        //原始名
        String originalFilename = multipartFile.getOriginalFilename();
        //前缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //新名字
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        //准备传输的文件
        File prepareFile = new File(absolutePath, fileName);
        if (!prepareFile.getParentFile().exists()) {
            prepareFile.mkdirs();
        }
        context.clear();
        TeamAvatar teamAvatar = teamAvatarDao.selectByForeignKey(teamId);
        if (teamAvatar != null) {
            String oldFileName = teamAvatar.getFileName();
            teamAvatar.setFileName(fileName);
            teamAvatarDao.updateByPrimaryKeySelective(teamAvatar);
            //传输文件
            multipartFile.transferTo(prepareFile);
            File oldFile = new File(absolutePath, oldFileName);
            if (oldFile.exists()) {
                oldFile.delete();
            }

        } else {
            teamAvatar = new TeamAvatar();
            teamAvatar.setTeamId(teamId);
            teamAvatar.setFileName(fileName);
            teamAvatarDao.insert(teamAvatar);
            //传输文件
            multipartFile.transferTo(prepareFile);
        }
        context.put("msg", "更新队伍信息");
        context.setResult(Result.success());
    }
}
