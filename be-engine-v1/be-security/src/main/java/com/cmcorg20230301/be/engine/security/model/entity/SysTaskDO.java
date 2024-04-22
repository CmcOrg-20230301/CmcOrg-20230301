package com.cmcorg20230301.be.engine.security.model.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.cmcorg20230301.be.engine.security.model.interfaces.ISysTaskType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "sys_task")
@Data
@Schema(description = "主表：任务")
public class SysTaskDO {

    @TableId(type = IdType.INPUT)
    private Long id;

    @Schema(description = "用户 id")
    private Long userId;

    @Schema(description = "租户 id")
    private Long tenantId;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;

    /**
     * {@link ISysTaskType}
     */
    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "主要 id")
    private String mainId;

    @Schema(description = "业务 id")
    private String businessId;

    @Schema(description = "是否：完成")
    private Boolean completeFlag;

    @Schema(description = "任务过期的未来时间戳")
    private Long expireTs;

    @Schema(description = "额外数据：1")
    private String str1;

    @Schema(description = "额外数据：2")
    private String str2;

}