package com.cmcorg20230301.be.engine.security.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.dto.SysLogPushDTO;
import com.cmcorg20230301.be.engine.security.properties.CommonProperties;
import com.cmcorg20230301.be.engine.security.service.SysLogService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = LogTopicConstant.LOG_SERVICE)
public class SysLogServiceImpl implements SysLogService {

    @Resource
    CommonProperties commonProperties;

    /**
     * 新增：日志记录
     */
    @Override
    public String push(SysLogPushDTO dto) {

        String logPushUrl = commonProperties.getLogPushUrl();

        if (StrUtil.isBlank(logPushUrl)) {
            return BaseBizCodeEnum.OK;
        }

        String msStr = new DateTime().toMsStr();

        String message = "【" + msStr + "】" + dto.getLog();

        if ("localhost".equals(logPushUrl)) {

            log.info("日志记录：{}", message);

        } else {

            HttpUtil.post(logPushUrl, JSONUtil.createObj().set("message", message).toString());

        }

        return BaseBizCodeEnum.OK;

    }

}
