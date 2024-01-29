package com.cmcorg20230301.be.engine.other.app.model.dto;

import cn.hutool.json.JSONObject;
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

            msgIdStr = getToUserName() + getEvent() + getCreateTime();

        } else {

            msgIdStr = getMsgId().toString();

        }

        return msgIdStr;

    }

    private String Event;

    /**
     * 企业应用的id，整型。可在应用的设置页面查看
     */
    private Integer AgentID;

    // 微信客服的参数 ↓

    /**
     * 调用拉取消息接口时，需要传此token，用于校验请求的合法性
     */
    private String Token;

    /**
     * 有新消息的客服账号。可通过sync_msg接口指定open_kfid获取此客服账号的消息
     */
    private String OpenKfId;

    /**
     * 由程序进行赋值：微信客服消息对象
     */
    private JSONObject wxKfMsgJsonObject;

    // 微信客服的参数 ↑

}
