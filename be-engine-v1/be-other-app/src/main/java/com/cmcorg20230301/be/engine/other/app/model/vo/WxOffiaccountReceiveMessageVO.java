package com.cmcorg20230301.be.engine.other.app.model.vo;

import lombok.Data;

@Data
public class WxOffiaccountReceiveMessageVO {

    private String ToUserName;

    private String FromUserName;

    private Long CreateTime;

    private String MsgType;

    private String Content;

}
