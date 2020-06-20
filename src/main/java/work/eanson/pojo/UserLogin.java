package work.eanson.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * user_login
 *
 * @author
 */
@Data
@ApiModel(description = "用户登录")
public class UserLogin implements Serializable {
    @Pattern(regexp = "^[1](([3|5|8][\\d])|([4][4,5,6,7,8,9])|([6][2,5,6,7])|([7][^9])|([9][1,8,9]))[\\d]{8}$")
    @ApiModelProperty("手机号")
    private String telephone;

    @ApiModelProperty(value = "用户名", notes = "后台生成")
    private String username;

    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$")
    @ApiModelProperty("密码")
    private String password;

    /**
     * 是否只注册了一半
     */
    @ApiModelProperty(value = "是否只注册了一半", notes = "登录分两个界面")
    private Boolean isRegHalf;

    /**
     * 是否删除 0为 1删除
     */
    @ApiModelProperty("是否被删除")
    private Boolean isDelete;

    private static final long serialVersionUID = 3207880482640325843L;
}