package work.eanson.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * school
 *
 * @author
 */
@Data
@ApiModel(description = "学校")
public class School implements Serializable {
    @ApiModelProperty("学校Id")
    private Integer schId;

    @ApiModelProperty("学校名称")
    private String name;

    @ApiModelProperty(value = "水平", notes = "专本")
    private String level;

    @ApiModelProperty("对应的省份ID")
    private Integer proId;

    private static final long serialVersionUID = 1L;
}