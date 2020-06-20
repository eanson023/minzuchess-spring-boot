package work.eanson.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * user_info
 *
 * @author
 */
@Data
@ApiModel(description = "用户信息")
public class UserInfo implements Serializable {
    @Pattern(regexp = "^[1](([3|5|8][\\d])|([4][4,5,6,7,8,9])|([6][2,5,6,7])|([7][^9])|([9][1,8,9]))[\\d]{8}$")
    @ApiModelProperty("手机号")
    private String telephone;

    @ApiModelProperty("真实姓名")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5.·\\u36c3\\u4DAE]{2,20}$")
    private String realName;

    /**
     * 用户类别:1为个人用户 2为社会团体  3为学校团体
     */
    @ApiModelProperty(value = "用户类型", notes = "1为个人用户 2为社会团体  3为学校团体")
    private Byte stat;

    @ApiModelProperty("创建时间")
    private Date joinTime;

    /**
     * 是否是后台添加
     */
    @ApiModelProperty("是否是后台添加")
    private Boolean isAdd;

    /**
     * 是否是管理员
     */
    @ApiModelProperty("是否是管理员")
    private Boolean isAdmin;

    /**
     * UUID
     */
    @ApiModelProperty("uuid")
    private String uuid;


    private static final long serialVersionUID = 1L;
}