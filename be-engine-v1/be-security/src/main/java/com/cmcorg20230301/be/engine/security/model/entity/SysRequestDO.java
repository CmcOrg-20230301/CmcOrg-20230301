package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.security.model.enums.SysRequestCategoryEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
