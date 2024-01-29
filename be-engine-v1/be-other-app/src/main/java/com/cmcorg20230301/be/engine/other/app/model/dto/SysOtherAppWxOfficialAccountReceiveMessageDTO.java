package com.cmcorg20230301.be.engine.other.app.model.dto;

import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import lombok.Data;

@Data
public class SysOtherAppWxOfficialAccountReceiveMessageDTO {

    // 由程序赋值的额外属性 ↓

    private SysUserDO sysUserDO;

    private SysOtherAppDO sysOtherAppDO;

    // 由程序赋值的额外属性 ↑

    /**
     * 开发者微信号，备注：这个不是 wxAppId
     */
    private String ToUserName;

    /**
     * 发送方帐号（一个OpenID）
     */
    private String FromUserName;

    private Long CreateTime;

    private String MsgType;

    private String Content;

    private Long MsgId;

    public String getMsgIdStr() {

        String msgIdStr;

        if (getMsgId() == null) {

            msgIdStr = getEventKey();

        } else {

            msgIdStr = getMsgId().toString();

        }

        return msgIdStr;

    }

    private Long MsgDataId;

    private Integer Idx;

    /**
     * 图像链接（由微信那边生成）
     */
    private String PicUrl;

    /**
     * 事件类型：subscribe 订阅 unsubscribe 取消订阅 CLICK 点击菜单事件
     */
    private String Event;

    private String EventKey;

    /**
     * 语音消息媒体id，可以调用获取临时素材接口拉取数据。
     */
    private String MediaId;

    /**
     * 语音格式，如amr，speex等
     */
    private String Format;

    /**
     * 语音识别结果，UTF8编码
     */
    private String Recognition;

}
