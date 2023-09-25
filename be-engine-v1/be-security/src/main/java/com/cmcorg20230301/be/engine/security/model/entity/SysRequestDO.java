package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.security.model.enums.SysRequestCategoryEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 新增/修改字段，示例：
 * <p>
 * ALTER TABLE be_doris_v1.sys_request ADD COLUMN `aaa` BIGINT NOT NULL DEFAULT "0" COMMENT "bbb" AFTER `ccc`;
 * <p>
 * ALTER TABLE be_doris_v1.sys_request MODIFY COLUMN `aaa` STRING NOT NULL COMMENT "bbb";
 */

/**
 * CREATE DATABASE IF NOT EXISTS `be_doris_v1`;
 * CREATE TABLE
 * IF
 * NOT EXISTS be_doris_v1.sys_request (
 * `id` BIGINT NOT NULL COMMENT "主键 id",
 * `tenant_id` BIGINT NOT NULL COMMENT "租户 id",
 * `create_id` BIGINT NOT NULL COMMENT "创建人id",
 * `create_time` DATETIME NOT NULL COMMENT "创建时间",
 * `update_id` BIGINT NOT NULL COMMENT "修改人id",
 * `update_time` DATETIME NOT NULL COMMENT "修改时间",
 * `enable_flag` BOOLEAN NOT NULL COMMENT "是否启用",
 * `version` INT NOT NULL COMMENT "乐观锁",
 * `del_flag` BOOLEAN NOT NULL COMMENT "是否逻辑删除",
 * `remark` VARCHAR ( 300 ) NOT NULL COMMENT "备注",
 * `uri` VARCHAR ( 100 ) NOT NULL COMMENT "请求的uri",
 * `name` VARCHAR ( 200 ) NOT NULL COMMENT "接口名（备用）",
 * `cost_ms_str` VARCHAR ( 100 ) NOT NULL COMMENT "耗时（字符串）",
 * `cost_ms` BIGINT NOT NULL COMMENT "耗时（毫秒）",
 * `category` INT NOT NULL COMMENT "请求类别",
 * `ip` VARCHAR ( 200 ) NOT NULL COMMENT "ip",
 * `region` VARCHAR ( 200 ) NOT NULL COMMENT "Ip2RegionUtil.getRegion() 获取到的 ip所处区域",
 * `success_flag` BOOLEAN NOT NULL COMMENT "请求是否成功",
 * `error_msg` STRING NOT NULL COMMENT "失败信息",
 * `request_param` STRING NOT NULL COMMENT "请求的参数",
 * `type` VARCHAR ( 100 ) NOT NULL COMMENT "请求类型",
 * `response_value` STRING NOT NULL COMMENT "请求返回的值",
 * ) UNIQUE KEY ( `id` ) DISTRIBUTED BY HASH ( `id` ) BUCKETS 1 PROPERTIES ( "replication_allocation" = "tag.location.default: 1" );
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_request")
@Data
@Schema(description = "主表：请求")
public class SysRequestDO extends BaseEntity {

    @Schema(description = "请求的 uri")
    private String uri;

    @Schema(description = "耗时（字符串）")
    private String costMsStr;

    @Schema(description = "耗时（毫秒）")
    private Long costMs;

    @Schema(description = "接口名（备用）")
    private String name;

    @Schema(description = "请求类别")
    private SysRequestCategoryEnum category;

    @Schema(description = "ip")
    private String ip;

    @Schema(description = "Ip2RegionUtil.getRegion() 获取到的 ip所处区域")
    private String region;

    @Schema(description = "请求是否成功")
    private Boolean successFlag;

    @Schema(description = "失败信息")
    private String errorMsg;

    @Schema(description = "请求的参数")
    private String requestParam;

    @Schema(description = "请求类型")
    private String type;

    @Schema(description = "请求返回的值")
    private String responseValue;

}
