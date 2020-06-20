package work.eanson.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * category
 *
 * @author
 */
@ApiModel(description = "棋盘分类")
@Data
public class Category implements Serializable {
    /**
     * 索引值
     */
    @ApiModelProperty("索引值")
    private Byte key;

    /**
     * 具体名称
     */
    @ApiModelProperty("具体名称")
    private String value;

    /**
     * 棋盘英文名__与html对应
     */
    @ApiModelProperty("棋盘英文名__与html对应")
    private String html;

    /**
     * 棋盘初始化坐标
     */
    @ApiModelProperty("棋盘初始化坐标")
    private String initPos;

    /**
     * 图片资源
     */
    @ApiModelProperty("图片资源")
    private String icon;

    private static final long serialVersionUID = 1L;

}