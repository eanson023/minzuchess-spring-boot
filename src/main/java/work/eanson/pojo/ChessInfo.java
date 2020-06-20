package work.eanson.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * chess_info
 *
 * @author
 */
@Data
@ApiModel(description = "棋盘信息")
public class ChessInfo implements Serializable {
    /**
     * 随机码
     */
    @ApiModelProperty(value = "棋盘码", notes = "用户注册自动生成")
    private String code;

    @ApiModelProperty(value = "棋钟")
    private String clock;

    /**
     * 别名
     */
    @ApiModelProperty("别名")
    private Long alias;

    /**
     * 棋盘是否开放为公共棋盘
     */
    @ApiModelProperty("棋盘是否开放为公共棋盘")
    private Boolean isPublic;

    /**
     * 1:久棋 2:蒙古棋
     */
    @ApiModelProperty(value = "分类", notes = "1:久棋 2:蒙古棋")
    private Byte category;

    @ApiModelProperty("手机号")
    private String userId;

    @ApiModelProperty("棋盘坐标记录")
    private String pos;


    private static final long serialVersionUID = 1L;
}