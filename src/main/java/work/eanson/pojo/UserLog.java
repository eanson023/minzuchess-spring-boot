package work.eanson.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * user_log
 *
 * @author
 */
@Data
@ApiModel(description = "用户日志")
public class UserLog implements Serializable {

    @ApiModelProperty("流水号")
    private Integer id;

    @ApiModelProperty("IP")
    private String ip;

    @ApiModelProperty("具体信息")
    private String message;

    @ApiModelProperty("执行时间")
    private Date executeTime;

    /**
     * 默认为匿名用户
     */
    @ApiModelProperty("手机号")
    private String userId;

    private static final long serialVersionUID = 1L;
}