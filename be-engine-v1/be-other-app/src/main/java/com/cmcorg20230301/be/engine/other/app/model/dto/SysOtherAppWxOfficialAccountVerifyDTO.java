package com.cmcorg20230301.be.engine.other.app.model.dto;

import lombok.Data;

@Data
public class SysOtherAppWxOfficialAccountVerifyDTO {

    private String signature;

    private String timestamp;

    private String nonce;

    private String echostr;

}
