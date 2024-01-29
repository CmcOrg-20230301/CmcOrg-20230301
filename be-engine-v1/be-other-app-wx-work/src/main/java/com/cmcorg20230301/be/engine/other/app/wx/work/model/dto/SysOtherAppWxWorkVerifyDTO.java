package com.cmcorg20230301.be.engine.other.app.wx.work.model.dto;

import lombok.Data;

@Data
public class SysOtherAppWxWorkVerifyDTO {

    private String msg_signature;

    private String timestamp;

    private String nonce;

    private String echostr;

}
