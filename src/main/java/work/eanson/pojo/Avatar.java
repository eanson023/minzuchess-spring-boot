package work.eanson.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * avatar
 *
 * @author
 */
@Data
@ApiModel(description = "用户头像")
public class Avatar implements Serializable {
    @ApiModelProperty("头像流水号")
    private Integer avatarId;

    @ApiModelProperty("路径")
    private String fileName;

    @ApiModelProperty("手机号")
    private String telephone;

    private static final long serialVersionUID = 1L;

}