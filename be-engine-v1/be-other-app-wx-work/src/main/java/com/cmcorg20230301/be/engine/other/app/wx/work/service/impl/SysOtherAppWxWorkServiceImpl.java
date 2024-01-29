package com.cmcorg20230301.be.engine.other.app.wx.work.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.kafka.util.KafkaUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppWxWorkReceiveMessageDTO;
import com.cmcorg20230301.be.engine.other.app.properties.SysOtherAppOfficialAccountProperties;
import com.cmcorg20230301.be.engine.other.app.wx.work.model.dto.SysOtherAppWxWorkVerifyDTO;
import com.cmcorg20230301.be.engine.other.app.wx.work.service.SysOtherAppWxWorkService;
import com.cmcorg20230301.be.engine.other.app.wx.work.util.WXBizMsgCrypt;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;

@Service
@Slf4j(topic = LogTopicConstant.OTHER_APP_WX_WORK)
public class SysOtherAppWxWorkServiceImpl implements SysOtherAppWxWorkService {

    @Resource
    SysOtherAppOfficialAccountProperties sysOtherAppOfficialAccountProperties;

    @Resource
    RedissonClient redissonClient;

    /**
     * 企业微信 token验证
     */
    @SneakyThrows
    @Override
    public String verify(SysOtherAppWxWorkVerifyDTO dto) {

        String sToken = sysOtherAppOfficialAccountProperties.getToken();
        String sEncodingAESKey = sysOtherAppOfficialAccountProperties.getEncodingAesKey();

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey);

        String sVerifyMsgSig = dto.getMsg_signature();

        String sVerifyTimeStamp = dto.getTimestamp();

        String sVerifyNonce = dto.getNonce();

        String sVerifyEchoStr = dto.getEchostr();

        // 需要返回的明文
        return wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, sVerifyEchoStr);

    }

    /**
     * 企业微信：推送的消息
     */
    @SneakyThrows
    @Override
    public void receiveMessage(HttpServletRequest request) {

        String sReqMsgSig = request.getParameter("msg_signature");
        String sReqTimeStamp = request.getParameter("timestamp");
        String sReqNonce = request.getParameter("nonce");

        String sReqData = IoUtil.readUtf8(request.getInputStream());

        String sToken = sysOtherAppOfficialAccountProperties.getToken();
        String sEncodingAESKey = sysOtherAppOfficialAccountProperties.getEncodingAesKey();

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey);

        String sMsg = wxcpt.DecryptMsg(sReqMsgSig, sReqTimeStamp, sReqNonce, sReqData);

        System.out.println("after decrypt msg: " + sMsg);

        Document document = XmlUtil.parseXml(sMsg);

        SysOtherAppWxWorkReceiveMessageDTO dto =
                XmlUtil.xmlToBean(document.getDocumentElement(), SysOtherAppWxWorkReceiveMessageDTO.class);

        String content = dto.getContent();

        if (content == null) {

            content = "";

        } else {

            content = StrUtil.trim(content);

            // 中文空格，转英文空格
            content = StrUtil.replace(content, " ", " ");

        }

        dto.setContent(content);

        String msgIdStr = dto.getMsgIdStr();

        log.info("企业微信，收到消息：{}，dto：{}，msgIdStr：{}", XmlUtil.toStr(document), JSONUtil.toJsonStr(dto), msgIdStr);

        String redisKey =
                BaseRedisKeyEnum.PRE_SYS_OTHER_APP_WX_WORK_RECEIVE_MESSAGE_ID.name() + msgIdStr;

        redissonClient.<String>getBucket(redisKey)
                .set("", Duration.ofMillis(BaseConstant.SHORT_CODE_EXPIRE_TIME));

        // 发送给：kafka进行处理
        KafkaUtil.sendSysOtherAppWxWorkReceiveMessageDTO(dto);

    }

}
