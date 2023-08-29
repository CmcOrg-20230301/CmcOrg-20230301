package com.cmcorg20230301.be.engine.request.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import com.cmcorg20230301.be.engine.security.model.enums.SysRequestCategoryEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysRequestPageDTO extends MyTenantPageDTO {

    @Schema(description = "请求的uri")
    private String uri;

    @Schema(description = "耗时开始（毫秒）")
    private Long beginCostMs;

    @Schema(description = "耗时结束（毫秒）")
    private Long endCostMs;

    @Schema(description = "接口名（备用）")
    private String name;

    @Schema(description = "起始时间：创建时间")
    private Date ctBeginTime;

    @Schema(description = "结束时间：创建时间")
    private Date ctEndTime;

    @Schema(description = "创建人id")
    private Long createId;

    @Schema(description = "请求类别")
    private SysRequestCategoryEnum category;

    @Schema(description = "ip")
    private String ip;

    @Schema(description = "Ip2RegionUtil.getRegion() 获取到的 ip所处区域")
    private String region;

    @Schema(description = "请求是否成功")
    private Boolean successFlag;

    @Schema(description = "请求类型")
    private String type;

}
