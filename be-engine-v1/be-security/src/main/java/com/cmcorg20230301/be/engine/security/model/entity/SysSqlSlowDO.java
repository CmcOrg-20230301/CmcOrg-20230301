package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_sql_slow")
@Data
@Schema(description = "主表：慢sql日志")
public class SysSqlSlowDO extends BaseEntityNoIdSuper {

    @Schema(description = "这里是自定义的主键 id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "sqlId，如：com.cmcorg20230301.be.engine.security.mapper.SysUserMapper.insert")
    private String name;

    @Schema(description = "sql语句类型：SELECT、DELETE、INSERT、UPDATE")
    private String type;

    @Schema(description = "耗时（字符串）")
    private String costMsStr;

    @Schema(description = "耗时（毫秒）")
    private Long costMs;

    @Schema(description = "sql内容")
    private String sql;

}