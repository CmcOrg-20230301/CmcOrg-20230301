package com.cmcorg20230301.be.engine.email.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailException;
import cn.hutool.extra.mail.MailUtil;
import com.cmcorg20230301.be.engine.email.enums.EmailMessageEnum;
import com.cmcorg20230301.be.engine.email.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.email.properties.EmailProperties;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.properties.CommonProperties;
import org.springframework.stereotype.Component;

/**
 * 邮箱工具类
 */
@Component
public class MyEmailUtil {

    private static String platformName;
    private static EmailProperties emailProperties;

    public MyEmailUtil(CommonProperties commonProperties, EmailProperties emailProperties) {

        MyEmailUtil.platformName = "【" + commonProperties.getPlatformName() + "】";
        MyEmailUtil.emailProperties = emailProperties;

    }

    /**
     * 发送邮件
     */
    public static void send(String to, EmailMessageEnum emailMessageEnum, String content, boolean isHtml) {

        if (StrUtil.isBlank(to)) {
            ApiResultVO.sysError(); // 因为这里 to字段都是由程序来赋值的，所以基本不会为空
        }

        // 消息内容，加上统一的前缀
        content = platformName + StrUtil.format(emailMessageEnum.getContentTemp(), content);

        String finalContent = content;

        try {

            MailUtil.send(emailProperties.getMailAccount(), to, emailMessageEnum.getSubject(), finalContent, isHtml);

        } catch (MailException e) {

            if (e.getMessage() != null && e.getMessage().contains("Invalid Addresses")) {

                ApiResultVO.error(BizCodeEnum.EMAIL_DOES_NOT_EXIST_PLEASE_RE_ENTER);

            } else {

                throw e;

            }

        }

    }

}
