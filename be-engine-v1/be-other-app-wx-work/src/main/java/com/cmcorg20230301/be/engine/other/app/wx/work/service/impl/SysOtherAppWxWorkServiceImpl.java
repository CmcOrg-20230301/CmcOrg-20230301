package com.cmcorg20230301.be.engine.other.app.wx.work.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.other.app.properties.SysOtherAppOfficialAccountProperties;
import com.cmcorg20230301.be.engine.other.app.wx.work.model.dto.SysOtherAppWxWorkVerifyDTO;
import com.cmcorg20230301.be.engine.other.app.wx.work.service.SysOtherAppWxWorkService;
import com.cmcorg20230301.be.engine.other.app.wx.work.util.WXBizJsonMsgCrypt;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j(topic = LogTopicConstant.OTHER_APP_WX_WORK)
public class SysOtherAppWxWorkServiceImpl implements SysOtherAppWxWorkService {

    @Resource
    SysOtherAppOfficialAccountProperties sysOtherAppOfficialAccountProperties;

    /**
     * 企业微信 token验证
     */
    @SneakyThrows
    @Override
    public String verify(SysOtherAppWxWorkVerifyDTO dto) {

        String sToken = sysOtherAppOfficialAccountProperties.getToken();
        String sCorpID = sysOtherAppOfficialAccountProperties.getCorpId();
        String sEncodingAESKey = sysOtherAppOfficialAccountProperties.getEncodingAesKey();

        WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt(sToken, sEncodingAESKey, sCorpID);

        String sVerifyMsgSig = dto.getMsg_signature();

        String sVerifyTimeStamp = dto.getTimestamp();

        String sVerifyNonce = dto.getNonce();

        String sVerifyEchoStr = dto.getEchostr();

        // 需要返回的明文
        return wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, sVerifyEchoStr);

    }

    /**
     * 微信公众号：推送的消息
     */
    @SneakyThrows
    @Override
    public void receiveMessage(HttpServletRequest request) {

        String sReqMsgSig = request.getParameter("msg_signature");
        String sReqTimeStamp = request.getParameter("timestamp");
        String sReqNonce = request.getParameter("nonce");

        String sReqData = IoUtil.readUtf8(request.getInputStream());

        String sToken = sysOtherAppOfficialAccountProperties.getToken();
        String sCorpID = sysOtherAppOfficialAccountProperties.getCorpId();
        String sEncodingAESKey = sysOtherAppOfficialAccountProperties.getEncodingAesKey();

        WXBizJsonMsgCrypt wxcpt = new WXBizJsonMsgCrypt(sToken, sEncodingAESKey, sCorpID);

        String sMsg = wxcpt.DecryptMsg(sReqMsgSig, sReqTimeStamp, sReqNonce, sReqData);

        JSONObject jsonObject = JSONUtil.parseObj(sMsg);

        log.info("jsonObject：{}", jsonObject);

    }

}
