package com.cmcorg20230301.be.engine.other.app.model.dto;

import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import lombok.Data;

@Data
public class SysOtherAppWxWorkReceiveMessageDTO {

    // 由程序赋值的额外属性 ↓

    private SysUserDO sysUserDO;

    private SysOtherAppDO sysOtherAppDO;

    // 由程序赋值的额外属性 ↑

    /**
     * 企业微信CorpID
     */
    private String ToUserName;

    /**
     * 成员UserID
     */
    private String FromUserName;

    /**
     * 消息创建时间（整型）
     */
    private Integer CreateTime;

    /**
     * 消息类型，此时固定为：text
     */
    private String MsgType;

    /**
     * 文本消息内容
     */
    private String Content;

    /**
     * 消息id，64位整型
     */
    private Long MsgId;

    public String getMsgIdStr() {

        String msgIdStr;

        if (getMsgId() == null) {

            msgIdStr = getFromUserName() + getCreateTime();

        } else {

            msgIdStr = getMsgId().toString();

        }

        return msgIdStr;

    }

    /**
     * 企业应用的id，整型。可在应用的设置页面查看
     */
    private Integer AgentID;

}
