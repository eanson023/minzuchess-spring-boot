package work.eanson.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * cheep
 *
 * @author
 */
@ApiModel(description = "棋谱")
@Data
public class Cheep implements Serializable {
    @ApiModelProperty("棋谱id")
    private String cheepId;

    /**
     * 棋谱名称
     */
    @ApiModelProperty("棋谱名称")
    private String name;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private Date from;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private Date to;

    /**
     * 路径名
     */
    @ApiModelProperty("路径名")
    private String path;

    /**
     * 文件名
     */
    @ApiModelProperty("文件名")
    private String realName;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createDate;

    @ApiModelProperty("棋盘码")
    private String code;

    private static final long serialVersionUID = 1L;
}