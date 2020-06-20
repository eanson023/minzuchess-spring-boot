package work.eanson.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * team
 *
 * @author
 */
@Data
@ApiModel(description = "团队")
public class Team implements Serializable {
    @ApiModelProperty("团队ID,流水号")
    private String teamId;

    @ApiModelProperty("队名")
    private String teamName;

    @ApiModelProperty("队伍规模")
    private Integer numbers;

    /**
     * 简介
     */
    @Size(min = 4, max = 200)
    @ApiModelProperty("队伍简介")
    private String introduction;

    @ApiModelProperty("创建日期")
    private Date createTime;

    /**
     * 身份主键
     */
    @ApiModelProperty("省份ID")
    private Integer provinceId;

    /**
     * 学校id 不太满足范式
     */
    @ApiModelProperty("学校ID")
    private Integer schoolId;
    /**
     * 一对一映射
     */
    private Province province;

    private School school;

    private String joinDate;

    private TeamAvatar teamAvatar;
}