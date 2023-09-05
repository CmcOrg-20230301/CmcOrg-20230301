package com.cmcorg20230301.be.engine.user.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserPageDTO extends MyTenantPageDTO {

    @Schema(description = "主键 id")
    private Long id;

    @Schema(description = "主键 idSet，备注：后端使用，前端不使用")
    private Set<Long> idSet;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "登录名")
    private String signInName;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "微信 openId")
    private String wxOpenId;

    @Schema(description = "是否正常")
    private Boolean enableFlag;

    @Schema(description = "是否有密码")
    private Boolean passwordFlag;

    @Schema(description = "创建开始时间")
    private Date beginCreateTime;

    @Schema(description = "创建结束时间")
    private Date endCreateTime;

    @Schema(description = "最近活跃开始时间")
    private Date beginLastActiveTime;

    @Schema(description = "最近活跃结束时间")
    private Date endLastActiveTime;

}
