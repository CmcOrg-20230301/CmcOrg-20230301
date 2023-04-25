package com.cmcorg20230301.engine.be.email.properties;

import cn.hutool.extra.mail.MailAccount;
import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.EMAIL)
@RefreshScope
public class EmailProperties {

    private static final MailAccount MAIL_ACCOUNT = new MailAccount();

    @PostConstruct
    public void postConstruct() {

        MAIL_ACCOUNT.setPort(getPort());
        MAIL_ACCOUNT.setFrom(getFrom());
        MAIL_ACCOUNT.setPass(getPass());
        MAIL_ACCOUNT.setStarttlsEnable(getStarttlsEnable());
        MAIL_ACCOUNT.setSslEnable(getSslEnable());

    }

    public MailAccount getMailAccount() {
        return MAIL_ACCOUNT;
    }

    @Schema(description = "端口")
    private Integer port = 465;

    @Schema(description = "发送人邮箱")
    private String from;

    @Schema(description = "发送人密码")
    private String pass;

    @Schema(description = "使用 STARTTLS安全连接，STARTTLS是对纯文本通信协议的扩展。它将纯文本连接升级为加密连接（TLS或SSL）， 而不是使用一个单独的加密通信端口。")
    private Boolean starttlsEnable = true;

    @Schema(description = "使用 SSL安全连接")
    private Boolean sslEnable = true;

}
