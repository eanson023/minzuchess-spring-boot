package work.eanson.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * trick
 *
 * @author
 */
@Data
@ApiModel(description = "招法")
public class Trick implements Serializable {

    @ApiModelProperty("日志id 流水号")
    private String logId;

    /**
     * 招法
     */
    @ApiModelProperty("具体招法")
    private String trick;


    @ApiModelProperty("颜色")
    private String color;

    /**
     * 插入时间
     */
    @ApiModelProperty("执行时间")
    private Date exuteTime;

    /**
     * 是否错误提交
     */
    @ApiModelProperty("是否出现错误")
    private Boolean isFalse;

    /**
     * 表示当前招数是谁行棋 1.AI 2.人类
     */
    @ApiModelProperty("行棋方")
    private Byte type;

    /**
     * 0.错误 1.布子,2.行棋3.飞子 4.提子
     */
    @ApiModelProperty("状态")
    private Byte status;

    /**
     * 错误信息
     */
    @ApiModelProperty("错误信息")
    private String message;

    /**
     * 该步骤之前的棋盘
     */
    @ApiModelProperty("之前的坐标信息")
    private String before;

    /**
     * 棋盘id
     */
    @ApiModelProperty("棋盘码")
    private String cbCode;

    private static final long serialVersionUID = 1L;
}