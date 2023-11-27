package com.cmcorg20230301.be.engine.other.app.wx.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.kafka.util.KafkaUtil;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppOfficialAccountWxReceiveMessageDTO;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppOfficialAccountWxVerifyDTO;
import com.cmcorg20230301.be.engine.other.app.model.vo.WxOffiaccountReceiveMessageVO;
import com.cmcorg20230301.be.engine.other.app.properties.SysOtherAppOfficialAccountProperties;
import com.cmcorg20230301.be.engine.other.app.wx.service.SysOtherAppOfficialAccountWxService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j(topic = LogTopicConstant.OTHER_APP_OFFICIAL_ACCOUNT_WX)
public class SysOtherAppOfficialAccountWxServiceImpl implements SysOtherAppOfficialAccountWxService {

    @Resource
    SysOtherAppOfficialAccountProperties sysOtherAppOfficialAccountProperties;

    /**
     * 微信公众号 token验证
     */
    @Override
    public String verify(SysOtherAppOfficialAccountWxVerifyDTO dto) {

        List<String> list = new ArrayList<>();

        list.add(sysOtherAppOfficialAccountProperties.getToken());
        list.add(dto.getTimestamp());
        list.add(dto.getNonce());

        Collections.sort(list); // 排序

        StringBuilder stringBuilder = new StringBuilder();

        list.forEach(stringBuilder::append);

        String checkStr = stringBuilder.toString();

        String sha1Hex = DigestUtil.sha1Hex(checkStr);

        if (dto.getSignature().equals(sha1Hex)) {

            return dto.getEchostr();

        }

        return "error";

    }

    /**
     * 微信公众号：推送的消息
     */
    @SneakyThrows
    @Override
    public String receiveMessage(HttpServletRequest request) {

        Document document = XmlUtil.readXML(request.getInputStream());

        SysOtherAppOfficialAccountWxReceiveMessageDTO dto =
            XmlUtil.xmlToBean(document.getDocumentElement(), SysOtherAppOfficialAccountWxReceiveMessageDTO.class);

        String content = dto.getContent();

        if (content == null) {

            content = "";

        } else {

            content = StrUtil.trim(content);

            // 中文空格，转英文空格
            content = StrUtil.replace(content, " ", " ");

        }

        dto.setContent(content);

        log.info("收到消息：{}，dto：{}", XmlUtil.toStr(document), JSONUtil.toJsonStr(dto));

        // 发送给：kafka进行处理
        KafkaUtil.sendSysOtherAppOfficialAccountWxReceiveMessageDTO(dto);

        return "";

    }

    /**
     * 处理：返回的消息为微信格式
     */
    @NotNull
    public static String handleReturnContent(SysOtherAppOfficialAccountWxReceiveMessageDTO dto, String fromUserName,
        String returnContent) {

        WxOffiaccountReceiveMessageVO wxOffiaccountReceiveMessageVO = new WxOffiaccountReceiveMessageVO();

        wxOffiaccountReceiveMessageVO.setToUserName("<![CDATA[" + fromUserName + "]]>");
        wxOffiaccountReceiveMessageVO.setFromUserName("<![CDATA[" + dto.getToUserName() + "]]>");
        wxOffiaccountReceiveMessageVO.setCreateTime(DateUtil.currentSeconds());
        wxOffiaccountReceiveMessageVO.setMsgType("<![CDATA[text]]>");
        wxOffiaccountReceiveMessageVO.setContent("<![CDATA[" + returnContent + "]]>");

        Document xml = XmlUtil.mapToXml(BeanUtil.beanToMap(wxOffiaccountReceiveMessageVO), "xml");

        String resultStr = XmlUtil.toStr(xml);

        resultStr = StrUtil.replace(resultStr, "&lt;", "<");
        resultStr = StrUtil.replace(resultStr, "&gt;", ">");

        log.info("回复消息：{}", resultStr);

        return resultStr;

    }

}