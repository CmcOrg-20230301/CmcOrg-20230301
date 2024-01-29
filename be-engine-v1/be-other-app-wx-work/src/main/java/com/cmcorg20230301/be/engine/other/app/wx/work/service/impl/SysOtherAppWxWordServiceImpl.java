package com.cmcorg20230301.be.engine.other.app.wx.work.service.impl;

import cn.hutool.json.JSONObject;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.other.app.wx.work.service.SysOtherAppWxWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j(topic = LogTopicConstant.OTHER_APP_WX_WORK)
public class SysOtherAppWxWordServiceImpl implements SysOtherAppWxWordService {

    /**
     * 企业微信 token验证
     */
    @Override
    public String verify(JSONObject dto) {

        log.info("企业微信 token验证：{}", dto.toString());

        return null;

    }

    /**
     * 微信公众号：推送的消息
     */
    @Override
    public String receiveMessage(HttpServletRequest request) {

        return null;

    }

}
