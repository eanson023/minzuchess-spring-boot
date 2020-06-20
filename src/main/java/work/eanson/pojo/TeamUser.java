package work.eanson.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * team_user
 *
 * @author
 */
@Data
@ApiModel(description = "队伍-用户-多对多")
public class TeamUser implements Serializable {


    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("队伍ID")
    private String teamId;

    /**
     * 是不是队长
     */
    @ApiModelProperty("是否是对长")
    private Boolean isLeader;

    /**
     * 是否已加入队伍
     */
    @ApiModelProperty("是否已入队")
    private Boolean isJoin;

    @ApiModelProperty("加入时间")
    private Date joinTime;


    /**
     * 是否被拒绝
     */
    @ApiModelProperty("是否被拒绝")
    private Boolean isRefuse;

    private List<UserInfo> userInfos;

    private List<Team> teams;

    private UserInfo userInfo;

    private Team team;

    @ApiModelProperty(value = "人数数量", notes = "与team表numbers属性产生歧义,不满足数据库范式")
    private int count;

    /*
    相差天数
     */
    private int dateDiff;
}