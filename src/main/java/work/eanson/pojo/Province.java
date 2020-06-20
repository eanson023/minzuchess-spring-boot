package work.eanson.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * province
 *
 * @author
 */
@Data
@ApiModel(description = "省份")
public class Province implements Serializable {
    @ApiModelProperty("省份ID")
    private Integer proId;

    @ApiModelProperty("省份名称")
    private String name;

    private static final long serialVersionUID = 1L;
}