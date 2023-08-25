package com.cmcorg20230301.be.engine.request.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyPageDTO;
import com.cmcorg20230301.be.engine.security.model.enums.SysRequestCategoryEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysRequestSelfLoginRecordPageDTO extends MyPageDTO {

    @Schema(description = "请求类别")
    private SysRequestCategoryEnum category;

    @Schema(description = "ip")
    private String ip;

    @Schema(description = "Ip2RegionUtil.getRegion() 获取到的 ip所处区域")
    private String region;

}
