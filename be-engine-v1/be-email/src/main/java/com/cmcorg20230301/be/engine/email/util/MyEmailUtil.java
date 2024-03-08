package com.cmcorg20230301.be.engine.email.util;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.email.enums.EmailMessageEnum;
import com.cmcorg20230301.be.engine.email.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.email.model.entity.SysEmailConfigurationDO;
import com.cmcorg20230301.be.engine.email.service.SysEmailConfigurationService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailException;
import cn.hutool.extra.mail.MailUtil;

/**
 * 邮箱工具类
 */
@Component
public class MyEmailUtil {

    private static SysEmailConfigurationService sysEmailConfigurationService;

    public MyEmailUtil(SysEmailConfigurationService sysEmailConfigurationService) {

        MyEmailUtil.sysEmailConfigurationService = sysEmailConfigurationService;

    }

    /**
     * 发送邮件
     */
    public static void send(String to, EmailMessageEnum emailMessageEnum, String content, @Nullable Long tenantId) {

        send(to, emailMessageEnum, content, false, tenantId);

    }

    /**
     * 发送邮件
     */
    public static void send(String to, EmailMessageEnum emailMessageEnum, String content, boolean isHtml,
        @Nullable Long tenantId) {

        if (StrUtil.isBlank(to)) {

            ApiResultVO.error(BaseBizCodeEnum.THIS_OPERATION_CANNOT_BE_PERFORMED_WITHOUT_BINDING_AN_EMAIL_ADDRESS);

        }

        tenantId = SysTenantUtil.getTenantId(tenantId);

        SysEmailConfigurationDO sysEmailConfigurationDO =
            sysEmailConfigurationService.lambdaQuery().eq(SysEmailConfigurationDO::getId, tenantId).one();

        if (sysEmailConfigurationDO == null) {
            ApiResultVO.error("操作失败：未配置邮箱参数，请联系管理员", tenantId);
        }

        // 消息内容，加上统一的前缀
        content = "【" + sysEmailConfigurationDO.getContentPre() + "】"
            + StrUtil.format(emailMessageEnum.getContentTemp(), content);

        String finalContent = content;

        MailAccount mailAccount = new MailAccount();

        mailAccount.setPort(sysEmailConfigurationDO.getPort());
        mailAccount.setFrom(sysEmailConfigurationDO.getFromEmail());
        mailAccount.setPass(sysEmailConfigurationDO.getPass());

        if (BooleanUtil.isTrue(sysEmailConfigurationDO.getSslFlag())) {

            mailAccount.setStarttlsEnable(true);
            mailAccount.setSslEnable(true);

        }

        try {

            MailUtil.send(mailAccount, to, emailMessageEnum.getSubject(), finalContent, isHtml);

        } catch (MailException e) {

            if (e.getMessage() != null && e.getMessage().contains("Invalid Addresses")) {

                ApiResultVO.error(BizCodeEnum.EMAIL_DOES_NOT_EXIST_PLEASE_RE_ENTER);

            } else {

                throw e;

            }

        }

    }

}
