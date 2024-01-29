package com.cmcorg20230301.be.engine.other.app.wx.work.model.dto;

import lombok.Data;

@Data
public class SysOtherAppWxWorkReceiveMessageDTO {

    /**
     * 企业微信 CorpID
     */
    private String tousername;

    private String encrypt;

    private String agentid;

}
