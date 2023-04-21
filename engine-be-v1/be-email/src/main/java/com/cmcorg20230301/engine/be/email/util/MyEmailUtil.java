package com.cmcorg20230301.engine.be.email.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailException;
import cn.hutool.extra.mail.MailUtil;
import com.cmcorg20230301.engine.be.email.enums.EmailMessageEnum;
import com.cmcorg20230301.engine.be.email.exception.BizCodeEnum;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.properties.CommonProperties;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 邮箱工具类
 */
@Component
public class MyEmailUtil {

    private static String platformName;
    private static TaskExecutor taskExecutor;

    public MyEmailUtil(CommonProperties commonProperties, TaskExecutor taskExecutor) {

        MyEmailUtil.platformName = "【" + commonProperties.getPlatformName() + "】";
        MyEmailUtil.taskExecutor = taskExecutor;

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

        taskExecutor.execute(() -> {

            try {

                MailUtil.send(to, emailMessageEnum.getSubject(), finalContent, isHtml);

            } catch (MailException e) {

                if (e.getMessage() != null && e.getMessage().contains("Invalid Addresses")) {
                    ApiResultVO.error(BizCodeEnum.EMAIL_DOES_NOT_EXIST_PLEASE_RE_ENTER);
                } else {
                    e.printStackTrace();
                }

            }

        });

    }

}
