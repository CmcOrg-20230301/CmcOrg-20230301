package com.cmcorg20230301.be.engine.pay.base.model.dto;

import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;
import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysPayConfigurationPageDTO extends MyTenantPageDTO {

    @Schema(description = "支付类型：101 支付宝 201 微信 301 云闪付 401 谷歌")
    private SysPayTypeEnum type;

    @Schema(description = "支付名（不可重复）")
    private String name;

    @Schema(description = "支付平台，应用 id")
    private String appId;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

}
