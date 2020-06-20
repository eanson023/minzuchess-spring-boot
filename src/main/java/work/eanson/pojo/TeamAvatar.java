package work.eanson.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * team_avatar
 *
 * @author eanson
 */
@Data
@ApiModel(description = "队伍头像")
public class TeamAvatar implements Serializable {
    @ApiModelProperty("头像流水号")
    private Integer avatarId;

    @ApiModelProperty("队伍ID")
    private String teamId;

    @ApiModelProperty("路径名")
    private String fileName;

    private static final long serialVersionUID = 1L;
}