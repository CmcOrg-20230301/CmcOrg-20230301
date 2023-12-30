package com.cmcorg20230301.be.engine.sms.base.model.bo;

import com.cmcorg20230301.be.engine.sms.base.model.entity.SysSmsConfigurationDO;
import com.cmcorg20230301.be.engine.sms.base.model.interfaces.ISysSmsType;
import lombok.Data;

@Data
public class SysSmsSendBO {

    /**
     * 租户主键 id
     */
    private Long tenantId;

    /**
     * 是否使用：上级租户的短信方式
     */
    private Boolean useParentTenantSmsFlag;

    /**
     * 需要发送的内容
     */
    private String sendContent;

    /**
     * 使用的配置文件
     */
    private SysSmsConfigurationDO sysSmsConfigurationDO;

    /**
     * 模版 id
     */
    private String templateId;

    /**
     * 模版参数 set
     */
    private String[] templateParamSet;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 短信类型：101 阿里 201 腾讯
     * <p>
     * {@link ISysSmsType}
     */
    private Integer smsType;

}
