package com.cmcorg20230301.be.engine.sign.wx.configuration;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.other.app.listener.SysOtherAppWxWorkReceiveMessageListener;
import com.cmcorg20230301.be.engine.other.app.mapper.SysOtherAppMapper;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppWxWorkReceiveMessageDTO;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppDO;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppTypeEnum;
import com.cmcorg20230301.be.engine.other.app.model.interfaces.ISysOtherAppWxWorkReceiveMessageHandle;
import com.cmcorg20230301.be.engine.other.app.wx.util.WxUtil;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseException;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysRequestCategoryEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.cmcorg20230301.be.engine.security.util.SysUserInfoUtil;
import com.cmcorg20230301.be.engine.security.util.TryUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.util.util.CallBack;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

//@Component
@Slf4j(topic = LogTopicConstant.OTHER_APP_WX_WORK)
public class SysOtherAppWxWorkReceiveMessageHandle implements ISysOtherAppWxWorkReceiveMessageHandle {

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SysOtherAppMapper sysOtherAppMapper;

    /**
     * 处理消息
     */
    @Override
    public void handle(SysOtherAppWxWorkReceiveMessageDTO dto) {

        SysOtherAppDO sysOtherAppDO = dto.getSysOtherAppDO();

        if (sysOtherAppDO == null) {

            sysOtherAppDO = ChainWrappers.lambdaQueryChain(sysOtherAppMapper).eq(SysOtherAppDO::getOpenId, dto.getToUserName()).eq(SysOtherAppDO::getType, SysOtherAppTypeEnum.WX_WORK.getCode()).one();

            if (sysOtherAppDO == null) {
                ApiResultVO.error("该企业微信的 openId，不存在本系统，请联系管理员", dto.getToUserName());
            }

            if (BooleanUtil.isFalse(sysOtherAppDO.getEnableFlag())) {
                ApiResultVO.error("该企业微信已被禁用，请联系管理员", dto.getToUserName());
            }

            dto.setSysOtherAppDO(sysOtherAppDO); // 设置：第三方应用数据

        }

        // 先：处理微信客服事件
        boolean returnFlag = handleKfMsgOrEvent(dto);

        if (returnFlag) {
            return;
        }

        SysUserDO sysUserDO = getSysUserDO(dto);

        if (sysUserDO == null) {
            sysUserDO = signInUser(dto, sysOtherAppDO); // 新增一个用户
        }

        if (sysUserDO == null) {
            ApiResultVO.error("用户信息为空，请联系管理员", dto.getFromUserName());
        }

        // 更新：用户信息
        SysUserInfoUtil.add(sysUserDO.getId(), new Date(), null, null);

        dto.setSysUserDO(sysUserDO); // 设置：用户数据

        if (BooleanUtil.isFalse(sysUserDO.getEnableFlag())) {

            // 回复文字内容：给企业微信
            execTextSend(dto, BaseBizCodeEnum.ACCOUNT_IS_DISABLED.getMsg());
            return;

        }

        // 给 security设置用户信息，并执行方法
        UserUtil.securityContextHolderSetAuthenticationAndExecFun(() -> {

            // 执行
            doHandle(dto);

        }, sysUserDO, false);

    }

    /**
     * 新增一个用户
     */
    private SysUserDO signInUser(SysOtherAppWxWorkReceiveMessageDTO dto, SysOtherAppDO sysOtherAppDO) {

        CallBack<SysUserDO> sysUserDoCallBack = new CallBack<>();

        // 直接通过：微信 openId登录
        SignUtil.signInAccount(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getWxOpenId, dto.getFromUserName()).eq(SysUserDO::getWxAppId, sysOtherAppDO.getAppId()), BaseRedisKeyEnum.PRE_WX_OPEN_ID, dto.getFromUserName(), () -> {

            SysUserInfoDO sysUserInfoDO = SysUserInfoUtil.getWxSysUserInfoDO();

            sysUserInfoDO.setSignUpType(SysRequestCategoryEnum.WX_WORK);

            return sysUserInfoDO;

        }, sysOtherAppDO.getTenantId(), accountMap -> {

            accountMap.put(BaseRedisKeyEnum.PRE_WX_APP_ID, sysOtherAppDO.getAppId());

        }, sysUserDoCallBack);

        return sysUserDoCallBack.getValue(); // 返回：回调对象

    }

    /**
     * 获取：用户数据
     */
    private SysUserDO getSysUserDO(SysOtherAppWxWorkReceiveMessageDTO dto) {

        return ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getWxOpenId, dto.getFromUserName()).eq(SysUserDO::getWxAppId, dto.getSysOtherAppDO().getAppId()).one();

    }

    /**
     * 处理微信客服事件
     *
     * @return true 则表示不需要后续的处理
     */
    private boolean handleKfMsgOrEvent(SysOtherAppWxWorkReceiveMessageDTO dto) {

        // 由于下面会对该字段进行赋值，所以这里判断会避免无限循环
        if (StrUtil.isNotBlank(dto.getFromUserName())) {
            return false;
        }

        if (!"event".equals(dto.getMsgType())) {
            return false;
        }

        if (!"kf_msg_or_event".equals(dto.getEvent())) {
            return false;
        }

        // 如果是：微信客服消息
        // 先：复制一份
        SysOtherAppWxWorkReceiveMessageDTO copyDtoTemp = BeanUtil.copyProperties(dto, SysOtherAppWxWorkReceiveMessageDTO.class);

        String accessToken = getAccessToken(dto);

        // 获取：最新消息
        List<JSONObject> jsonObjectList = WxUtil.syncMsg(accessToken, dto.getSysOtherAppDO().getTenantId(), dto.getToken(), dto.getOpenKfId(), dto.getSysOtherAppDO().getAppId());

        if (CollUtil.isEmpty(jsonObjectList)) {
            return true;
        }

        JSONObject jsonObject = jsonObjectList.remove(0); // 先处理：第一个消息

        dto.setFromUserName(jsonObject.getStr("external_userid"));

        dto.setWxKfMsgJsonObject(jsonObject);

        if (CollUtil.isNotEmpty(SysOtherAppWxWorkReceiveMessageListener.iSysOtherAppWxWorkReceiveMessageHandleList)) {

            for (JSONObject item : jsonObjectList) {

                // 如果是：事件类型，则暂不进行处理
                if ("event".equals(item.getStr("msgtype"))) {
                    continue;
                }

                MyThreadUtil.execute(() -> {

                    TryUtil.tryCatch(() -> {

                        for (ISysOtherAppWxWorkReceiveMessageHandle subItem : SysOtherAppWxWorkReceiveMessageListener.iSysOtherAppWxWorkReceiveMessageHandleList) {

                            SysOtherAppWxWorkReceiveMessageDTO copyDto = BeanUtil.copyProperties(copyDtoTemp, SysOtherAppWxWorkReceiveMessageDTO.class);

                            copyDto.setFromUserName(item.getStr("external_userid"));

                            copyDto.setWxKfMsgJsonObject(item);

                            // 处理消息
                            subItem.handle(copyDto);

                        }

                    });

                });

            }

        }

        // 如果是：事件类型，则暂不进行处理
        if ("event".equals(jsonObject.getStr("msgtype"))) {
            return true;
        }

        return false;

    }

    /**
     * 执行
     */
    private void doHandle(SysOtherAppWxWorkReceiveMessageDTO dto) {

        if ("event".equals(dto.getMsgType())) {

            if ("kf_msg_or_event".equals(dto.getEvent())) { // 如果是：微信客服消息

                doHandleKfMsgOrEvent(dto); // 处理

            }

        } else {

            // 回复文字内容：给企业微信
            execTextSend(dto, "暂未支持，请联系管理员");

        }

    }

    /**
     * 处理：微信客服消息
     */
    private void doHandleKfMsgOrEvent(SysOtherAppWxWorkReceiveMessageDTO dto) {

        JSONObject jsonObject = dto.getWxKfMsgJsonObject();

        String msgtype = jsonObject.getStr("msgtype");

        if ("text".equals(msgtype)) {

            JSONObject text = jsonObject.getJSONObject("text");

            String content = text.getStr("content");

            dto.setContent(content);

            handleTextMsg(dto); // 处理：文字消息

        } else if ("image".equals(msgtype)) {

            JSONObject image = jsonObject.getJSONObject("image");

            String mediaId = image.getStr("media_id");

            String accessToken = getAccessToken(dto);

            String mediaUrl = WxUtil.getMediaUrlForWork(accessToken, mediaId);

            if (StrUtil.isNotBlank(dto.getSysOtherAppDO().getImageReplyContent())) {

                // 回复文字内容
                execTextSend(dto, dto.getSysOtherAppDO().getImageReplyContent());

            }

        } else {

            execTextSend(dto, "暂时只支持：文字、图片消息");

        }

    }

    /**
     * 处理：文字消息
     */
    private void handleTextMsg(SysOtherAppWxWorkReceiveMessageDTO dto) {

        if (StrUtil.isNotBlank(dto.getSysOtherAppDO().getTextReplyContent())) {

            MyThreadUtil.execute(() -> {

                // 回复文字内容
                execTextSend(dto, dto.getSysOtherAppDO().getTextReplyContent());

            });

        }

        TryUtil.tryCatch(() -> {

        }, e -> {

            if (e instanceof BaseException) {

                // 回复文字内容
                execTextSend(dto, ((BaseException) e).getApiResultVO().getMsg());

            } else {

                // 回复文字内容
                execTextSend(dto, BaseBizCodeEnum.API_RESULT_SYS_ERROR.getMsg());

            }

        });

    }

    /**
     * 回复文字内容：给企业微信/微信客服
     */
    public static void execTextSend(SysOtherAppWxWorkReceiveMessageDTO dto, String content) {

        String accessToken = getAccessToken(dto);

        if (dto.getWxKfMsgJsonObject() == null) {

            // 执行：发送
            WxUtil.doTextSendForWork(dto.getFromUserName(), accessToken, content, dto.getAgentID());

        } else {

            // 执行：发送
            WxUtil.doTextSendForWorkKf(dto.getFromUserName(), accessToken, content, dto.getOpenKfId());

        }

    }

    /**
     * 获取：微信 accessToken
     */
    @NotNull
    private static String getAccessToken(SysOtherAppWxWorkReceiveMessageDTO dto) {

        Long tenantId = dto.getSysOtherAppDO().getTenantId();

        return WxUtil.getAccessTokenForWork(tenantId, dto.getSysOtherAppDO().getAppId());

    }

}
