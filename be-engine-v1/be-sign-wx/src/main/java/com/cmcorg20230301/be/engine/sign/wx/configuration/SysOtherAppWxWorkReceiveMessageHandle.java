package com.cmcorg20230301.be.engine.sign.wx.configuration;

import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.other.app.mapper.SysOtherAppMapper;
import com.cmcorg20230301.be.engine.other.app.mapper.SysOtherAppOfficialAccountMenuMapper;
import com.cmcorg20230301.be.engine.other.app.model.dto.SysOtherAppWxWorkReceiveMessageDTO;
import com.cmcorg20230301.be.engine.other.app.model.interfaces.ISysOtherAppWxWorkReceiveMessageHandle;
import com.cmcorg20230301.be.engine.other.app.wx.util.WxUtil;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j(topic = LogTopicConstant.OTHER_APP_WX_WORK)
public class SysOtherAppWxWorkReceiveMessageHandle implements ISysOtherAppWxWorkReceiveMessageHandle {

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SysOtherAppMapper sysOtherAppMapper;

    @Resource
    SysOtherAppOfficialAccountMenuMapper sysOtherAppOfficialAccountMenuMapper;

    @Resource
    RedissonClient redissonClient;

    /**
     * 处理消息
     */
    @Override
    public void handle(SysOtherAppWxWorkReceiveMessageDTO dto) {

//        SysOtherAppDO sysOtherAppDO = ChainWrappers.lambdaQueryChain(sysOtherAppMapper).eq(SysOtherAppDO::getOpenId, dto.getToUserName()).eq(SysOtherAppDO::getType, SysOtherAppTypeEnum.WX_OFFICIAL_ACCOUNT.getCode()).one();
//
//        if (sysOtherAppDO == null) {
//            ApiResultVO.error("该微信公众号的 openId，不存在本系统，请联系管理员", dto.getToUserName());
//        }
//
//        if (BooleanUtil.isFalse(sysOtherAppDO.getEnableFlag())) {
//            ApiResultVO.error("该微信公众号已被禁用，请联系管理员", dto.getToUserName());
//        }
//
//        dto.setSysOtherAppDO(sysOtherAppDO); // 设置：第三方应用数据
//
//        SysUserDO sysUserDO = getSysUserDO(dto);
//
//        // 处理：扫码二维码不自动注册的操作
//        sysUserDO = handleQrCodeSceneNotAutoSignUp(dto, sysUserDO, sysOtherAppDO);
//
//        if (sysUserDO == null) {
//            sysUserDO = signInUser(dto, sysOtherAppDO); // 新增一个用户
//        }
//
//        if (sysUserDO == null) {
//            ApiResultVO.error("用户信息为空，请联系管理员", dto.getFromUserName());
//        }
//
//        // 更新：用户信息
//        SysUserInfoUtil.add(sysUserDO.getId(), new Date(), null, null);
//
//        dto.setSysUserDO(sysUserDO); // 设置：用户数据
//
//        if (BooleanUtil.isFalse(sysUserDO.getEnableFlag())) {
//
//            // 回复文字内容：给微信公众号
//            execTextSend(dto, BaseBizCodeEnum.ACCOUNT_IS_DISABLED.getMsg());
//            return;
//
//        }
//
//        // 给 security设置用户信息，并执行方法
//        UserUtil.securityContextHolderSetAuthenticationAndExecFun(() -> {
//
//            // 执行
//            doHandle(dto);
//
//        }, sysUserDO, false);

    }

//    /**
//     * 新增一个用户
//     */
//    private SysUserDO signInUser(SysOtherAppWxWorkReceiveMessageDTO dto, SysOtherAppDO sysOtherAppDO) {
//
//        CallBack<SysUserDO> sysUserDoCallBack = new CallBack<>();
//
//        // 直接通过：微信 openId登录
//        SignUtil.signInAccount(ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getWxOpenId, dto.getFromUserName()).eq(SysUserDO::getWxAppId, sysOtherAppDO.getAppId()), BaseRedisKeyEnum.PRE_WX_OPEN_ID, dto.getFromUserName(), () -> {
//
//            SysUserInfoDO sysUserInfoDO = SignWxServiceImpl.getWxSysUserInfoDO();
//
//            sysUserInfoDO.setSignUpType(SysRequestCategoryEnum.WX_OFFICIAL_ACCOUNT);
//
//            return sysUserInfoDO;
//
//        }, sysOtherAppDO.getTenantId(), accountMap -> {
//
//            accountMap.put(BaseRedisKeyEnum.PRE_WX_APP_ID, sysOtherAppDO.getAppId());
//
//        }, null);
//
//        return sysUserDoCallBack.getValue(); // 返回：回调对象
//
//    }

//    /**
//     * 处理：扫码二维码不自动注册的操作
//     */
//    @Nullable
//    private SysUserDO handleQrCodeSceneNotAutoSignUp(SysOtherAppWxWorkReceiveMessageDTO dto, @Nullable SysUserDO sysUserDO, SysOtherAppDO sysOtherAppDO) {
//
////        if (!"event".equals(dto.getMsgType())) {
////            return sysUserDO;
////        }
////
////        String qrCodeSceneValue = null;
////
////        if ("subscribe".equals(dto.getEvent())) {
////
////            String eventKey = dto.getEventKey();
////
////            if (StrUtil.isNotBlank(eventKey)) { // 如果是：扫描二维码关注的
////
////                // 二维码上携带的数据
////                qrCodeSceneValue = StrUtil.subAfter(eventKey, "qrscene_", false);
////
////            }
////
////        } else if ("SCAN".equals(dto.getEvent())) {
////
////            qrCodeSceneValue = dto.getEventKey(); // 二维码上携带的数据
////
////        }
////
////        if (StrUtil.isBlank(qrCodeSceneValue)) {
////            return sysUserDO;
////        }
////
////        // 用户是否不存在
////        if (sysUserDO == null) {
////
////            sysUserDO = new SysUserDO();
////
////            sysUserDO.setWxOpenId(dto.getFromUserName());
////            sysUserDO.setWxAppId(sysOtherAppDO.getAppId());
////            sysUserDO.setTenantId(sysOtherAppDO.getTenantId());
////
////        }
////
////        dto.setSysUserDO(sysUserDO);
////
////        // 处理：二维码上携带的数据
////        handleQrCodeSceneValue(dto, qrCodeSceneValue, SysQrCodeSceneTypeEnum.NOT_AUTO_SIGN_UP_MAP);
////
////        return sysUserDO;
//
//    }

//    /**
//     * 获取：用户数据
//     */
//    private SysUserDO getSysUserDO(SysOtherAppWxWorkReceiveMessageDTO dto) {
//
//        return ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getWxOpenId, dto.getFromUserName()).eq(SysUserDO::getWxAppId, dto.getSysOtherAppDO().getAppId()).one();
//
//    }

//    /**
//     * 执行
//     */
//    private void doHandle(SysOtherAppWxWorkReceiveMessageDTO dto) {
//
////        if ("event".equals(dto.getMsgType())) {
////
////            if ("subscribe".equals(dto.getEvent())) {
////
////                handleEventSubscribe(dto); // 处理：订阅消息
////
////            } else if ("CLICK".equals(dto.getEvent())) {
////
////                handleEventClick(dto); // 处理：点击菜单事件
////
////            } else if ("SCAN".equals(dto.getEvent())) {
////
////                handleQrCodeSceneValue(dto, dto.getEventKey(), SysQrCodeSceneTypeEnum.AUTO_SIGN_UP_MAP); // 处理：扫码二维码事件
////
////            }
////
////        } else if ("image".equals(dto.getMsgType())) { // 处理：图片消息
////
////            if (StrUtil.isNotBlank(dto.getSysOtherAppDO().getImageReplyContent())) {
////
////                // 回复文字内容：给微信公众号
////                execTextSend(dto, dto.getSysOtherAppDO().getImageReplyContent());
////
////            }
////
////        } else if ("text".equals(dto.getMsgType())) {
////
////            handleTextMsg(dto); // 处理：文字消息
////
////        } else if ("voice".equals(dto.getMsgType())) { // 处理：语音消息
////
////            String recognition = dto.getRecognition();
////
////            dto.setContent(recognition);
////
////            handleTextMsg(dto); // 处理：文字消息
////
////        } else {
////
////            // 回复文字内容：给微信公众号
////            execTextSend(dto, "暂时只支持：文字、图片、语音消息");
////
////        }
//
//    }

//    /**
//     * 处理：订阅消息
//     */
//    private void handleEventSubscribe(SysOtherAppWxWorkReceiveMessageDTO dto) {
//
////        // 回复文字内容：给微信公众号
////        execTextSend(dto, dto.getSysOtherAppDO().getSubscribeReplyContent());
////
////        String eventKey = dto.getEventKey();
////
////        if (StrUtil.isBlank(eventKey)) { // 如果：不是扫描二维码关注的
////
////        } else { // 如果是：扫描二维码关注的
////
////            // 二维码上携带的数据
////            String qrCodeSceneValue = StrUtil.subAfter(eventKey, "qrscene_", false);
////
////            // 处理：二维码上携带的数据
////            handleQrCodeSceneValue(dto, qrCodeSceneValue, SysQrCodeSceneTypeEnum.AUTO_SIGN_UP_MAP);
////
////        }
//
//    }

//    /**
//     * 处理：二维码上携带的数据
//     */
//    private boolean handleQrCodeSceneValue(SysOtherAppWxWorkReceiveMessageDTO dto, String qrCodeSceneValue, Map<String, ISysQrCodeSceneType> map) {
//
//        boolean handleFlag = false; // 是否：处理
//
//        List<String> splitList = StrUtil.splitTrim(qrCodeSceneValue, ISysQrCodeSceneType.SEPARATOR);
//
//        if (CollUtil.isNotEmpty(splitList) && splitList.size() == 2) {
//
//            ISysQrCodeSceneType iSysQrCodeSceneType = map.get(ISysQrCodeSceneType.SEPARATOR + splitList.get(0));
//
//            if (iSysQrCodeSceneType != null) {
//
//                VoidFunc3<String, RedissonClient, SysUserDO> qrSceneValueConsumer = iSysQrCodeSceneType.getQrSceneValueConsumer();
//
//                if (qrSceneValueConsumer != null) {
//
//                    qrSceneValueConsumer.call(splitList.get(1), redissonClient, dto.getSysUserDO()); // 处理：二维码上面的值
//
//                    handleFlag = true;
//
//                }
//
//            }
//
//        }
//
//        return handleFlag;
//
//    }

//    /**
//     * 处理：点击菜单事件
//     */
//    private void handleEventClick(SysOtherAppWxWorkReceiveMessageDTO dto) {
//
////        SysOtherAppOfficialAccountMenuDO sysOtherAppOfficialAccountMenuDO = ChainWrappers.lambdaQueryChain(sysOtherAppOfficialAccountMenuMapper).eq(SysOtherAppOfficialAccountMenuDO::getOtherAppId, dto.getSysOtherAppDO().getId()).eq(BaseEntityNoIdSuper::getTenantId, dto.getSysOtherAppDO().getTenantId()).eq(BaseEntityNoId::getEnableFlag, true) //
////                .eq(SysOtherAppOfficialAccountMenuDO::getType, SysOtherAppOfficialAccountMenuTypeEnum.WX_OFFICIAL_ACCOUNT) //
////                .eq(SysOtherAppOfficialAccountMenuDO::getButtonType, SysOtherAppOfficialAccountMenuButtonTypeEnum.CLICK).eq(SysOtherAppOfficialAccountMenuDO::getValue, dto.getEventKey()).select(SysOtherAppOfficialAccountMenuDO::getReplyContent).one();
////
////        if (sysOtherAppOfficialAccountMenuDO != null) {
////
////            // 回复文字内容：给微信公众号
////            execTextSend(dto, sysOtherAppOfficialAccountMenuDO.getReplyContent());
////
////        }
//
//    }

//    /**
//     * 处理：文字消息
//     */
//    private void handleTextMsg(SysOtherAppWxWorkReceiveMessageDTO dto) {
//
//        if (StrUtil.isNotBlank(dto.getSysOtherAppDO().getTextReplyContent())) {
//
//            MyThreadUtil.execute(() -> {
//
//                // 回复文字内容：给微信公众号
//                execTextSend(dto, dto.getSysOtherAppDO().getTextReplyContent());
//
//            });
//
//        }
//
//        TryUtil.tryCatch(() -> {
//
//        }, e -> {
//
//            if (e instanceof BaseException) {
//
//                // 回复文字内容：给微信公众号
//                execTextSend(dto, ((BaseException) e).getApiResultVO().getMsg());
//
//            } else {
//
//                // 回复文字内容：给微信公众号
//                execTextSend(dto, BaseBizCodeEnum.API_RESULT_SYS_ERROR.getMsg());
//
//            }
//
//        });
//
//    }

    /**
     * 回复文字内容：给微信公众号
     */
    public static void execTextSend(SysOtherAppWxWorkReceiveMessageDTO dto, String content) {

        String accessToken = getAccessToken(dto);

        // 执行：发送
        WxUtil.doTextSendForWork(dto.getFromUserName(), accessToken, content, dto.getAgentID());

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
