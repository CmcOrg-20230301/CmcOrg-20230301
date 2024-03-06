package com.cmcorg20230301.be.engine.other.app.wx.util;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.FileTempPathConstant;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppDO;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppTypeEnum;
import com.cmcorg20230301.be.engine.other.app.service.SysOtherAppService;
import com.cmcorg20230301.be.engine.other.app.wx.model.enums.WxMediaUploadTypeEnum;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxAccessTokenVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxBaseVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxOpenIdVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxPhoneByCodeVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxServiceStateVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxServicerListVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxSyncMsgVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxUnionIdInfoVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxUserInfoVO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxWorkOpenIdVO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.interfaces.ISysQrCodeSceneType;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.util.util.MyStrUtil;
import com.cmcorg20230301.be.engine.util.util.RetryUtil;
import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import javax.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = LogTopicConstant.OTHER_APP_WX)
public class WxUtil {

    private static SysOtherAppService sysOtherAppService;

    public WxUtil(SysOtherAppService sysOtherAppService) {

        WxUtil.sysOtherAppService = sysOtherAppService;

    }

    private static RedissonClient redissonClient;

    @Resource
    public void setRedissonClient(RedissonClient redissonClient) {
        WxUtil.redissonClient = redissonClient;
    }

    /**
     * 通过微信小程序的 code，获取微信的 openId信息
     */
    @NotNull
    public static WxOpenIdVO getWxMiniProgramOpenIdVoByCode(@Nullable Long tenantId, String code,
        @Nullable String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery()
            .eq(BaseEntityNoIdSuper::getTenantId, tenantId)
            .eq(StrUtil.isNotBlank(appId), SysOtherAppDO::getAppId, appId)
            .eq(BaseEntityNoId::getEnableFlag, true)
            .eq(SysOtherAppDO::getType, SysOtherAppTypeEnum.WX_MINI_PROGRAM)
            .select(SysOtherAppDO::getSecret, SysOtherAppDO::getAppId)
            .one();

        String errorMessageStr = "miniProgramOpenId";

        if (sysOtherAppDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg(), errorMessageStr);
        }

        String jsonStr = HttpUtil.get(
            "https://api.weixin.qq.com/sns/jscode2session?appid=" + sysOtherAppDO.getAppId()
                + "&secret=" + sysOtherAppDO.getSecret()
                + "&js_code=" + code + "&grant_type=authorization_code");

        log.info("微信小程序登录：{}", jsonStr);

        WxOpenIdVO wxOpenIdVO = JSONUtil.toBean(jsonStr, WxOpenIdVO.class);

        checkWxVO(wxOpenIdVO, errorMessageStr, tenantId, sysOtherAppDO.getAppId()); // 检查：微信回调 vo对象

        wxOpenIdVO.setAppId(sysOtherAppDO.getAppId());

        return wxOpenIdVO;

    }

    /**
     * code换取用户手机号信息，每个code只能使用一次，code的有效期为5min
     */
    @NotNull
    public static WxPhoneByCodeVO.WxPhoneInfoVO getWxMiniProgramPhoneInfoVoByCode(
        @Nullable Long tenantId, String code,
        String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery()
            .eq(BaseEntityNoIdSuper::getTenantId, tenantId)
            .eq(SysOtherAppDO::getAppId, appId).eq(BaseEntityNoId::getEnableFlag, true)
            .eq(SysOtherAppDO::getType, SysOtherAppTypeEnum.WX_MINI_PROGRAM)
            .select(SysOtherAppDO::getSecret)
            .one();

        String errorMessageStr = "用户手机号";

        if (sysOtherAppDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg(), errorMessageStr);
        }

        // 获取：accessToken
        String accessToken = getAccessToken(tenantId, appId);

        JSONObject formJson = JSONUtil.createObj().set("code", code);

        String postStr = HttpUtil
            .post("https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token="
                    + accessToken,
                formJson.toString());

        WxPhoneByCodeVO wxPhoneByCodeVO = JSONUtil.toBean(postStr, WxPhoneByCodeVO.class);

        checkWxVO(wxPhoneByCodeVO, errorMessageStr, tenantId, appId); // 检查：微信回调 vo对象

        return wxPhoneByCodeVO.getPhone_info();

    }

    /**
     * 通过微信浏览器的 code，获取微信的 openId信息：微信公众号
     */
    @NotNull
    public static WxOpenIdVO getWxBrowserOpenIdVoByCode(@Nullable Long tenantId, String code,
        String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery()
            .eq(BaseEntityNoIdSuper::getTenantId, tenantId)
            .eq(SysOtherAppDO::getAppId, appId).eq(BaseEntityNoId::getEnableFlag, true)
            .eq(SysOtherAppDO::getType, SysOtherAppTypeEnum.WX_OFFICIAL_ACCOUNT)
            .select(SysOtherAppDO::getSecret)
            .one();

        String errorMessageStr = "browserOpenId";

        if (sysOtherAppDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg(), errorMessageStr);
        }

        String jsonStr = HttpUtil.get(
            "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret="
                + sysOtherAppDO.getSecret()
                + "&code=" + code + "&grant_type=authorization_code");

        WxOpenIdVO wxOpenIdVO = JSONUtil.toBean(jsonStr, WxOpenIdVO.class);

        checkWxVO(wxOpenIdVO, errorMessageStr, tenantId, appId); // 检查：微信回调 vo对象

        return wxOpenIdVO;

    }

    /**
     * 通过微信浏览器的 access_token，获取：微信里该用户的 unionId
     */
    @NotNull
    public static WxUnionIdInfoVO getWxUnionIdByBrowserAccessToken(String accessToken,
        String openId,
        Long tenantId, String appId) {

        String jsonStr = HttpUtil.get(
            "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + accessToken + "&openid="
                + openId);

        WxUnionIdInfoVO wxUnionIdInfoVO = JSONUtil.toBean(jsonStr, WxUnionIdInfoVO.class);

        checkWxVO(wxUnionIdInfoVO, "微信 unionId信息", tenantId, appId); // 检查：微信回调 vo对象

        return wxUnionIdInfoVO;

    }

    /**
     * 通过企业微信浏览器的 code，获取企业微信的 openId信息
     */
    @NotNull
    public static WxWorkOpenIdVO getWxWorkBrowserOpenIdVoByCode(@Nullable Long tenantId,
        String code, String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        String accessToken = WxUtil.getAccessTokenForWork(tenantId, appId);

        String jsonStr = HttpUtil.get(
            "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token=" + accessToken
                + "&code=" + code);

        WxWorkOpenIdVO wxWorkOpenIdVO = JSONUtil.toBean(jsonStr, WxWorkOpenIdVO.class);

        checkWxVO(wxWorkOpenIdVO, "browserOpenIdForWork", tenantId, appId); // 检查：微信回调 vo对象

        return wxWorkOpenIdVO;

    }

    /**
     * 通过微信浏览器的 access_token，获取：微信里该用户信息
     */
    @NotNull
    public static WxUserInfoVO getWxUserInfoByBrowserAccessToken(String accessToken, String openId,
        Long tenantId,
        String appId) {

        return getWxUserInfoByBrowserAccessToken(accessToken, openId, "zh_CN", tenantId, appId);

    }

    /**
     * 通过微信浏览器的 access_token，获取：微信里该用户信息
     *
     * @param lang zh_CN 简体，zh_TW 繁体，en 英语
     */
    @NotNull
    public static WxUserInfoVO getWxUserInfoByBrowserAccessToken(String accessToken, String openId,
        String lang,
        Long tenantId, String appId) {

        String jsonStr = HttpUtil.get(
            "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid="
                + openId + "&lang="
                + lang);

        WxUserInfoVO wxUserInfoVO = JSONUtil.toBean(jsonStr, WxUserInfoVO.class);

        checkWxVO(wxUserInfoVO, "微信用户信息", tenantId, appId); // 检查：微信回调 vo对象

        return wxUserInfoVO;

    }

    /**
     * 获取：微信公众号全局唯一后台接口调用凭据
     */
    @NotNull
    public static String getAccessToken(@Nullable Long tenantId, String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        SysOtherAppTypeEnum sysOtherAppTypeEnum = SysOtherAppTypeEnum.WX_OFFICIAL_ACCOUNT;

        String sufKey = tenantId + ":" + sysOtherAppTypeEnum + ":" + appId;

        String accessToken = MyCacheUtil.onlyGet(BaseRedisKeyEnum.WX_ACCESS_TOKEN_CACHE, sufKey);

        if (StrUtil.isNotBlank(accessToken)) {
            return accessToken;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery()
            .eq(BaseEntityNoIdSuper::getTenantId, tenantId)
            .eq(SysOtherAppDO::getAppId, appId).eq(SysOtherAppDO::getType, sysOtherAppTypeEnum)
            .eq(BaseEntityNoId::getEnableFlag, true).select(SysOtherAppDO::getSecret)
            .one();

        String errorMessageStr = "accessToken";

        if (sysOtherAppDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg(), errorMessageStr);
        }

        String jsonStr = HttpUtil.get(
            "https://api.weixin.qq.com/cgi-bin/token?appid=" + appId + "&secret="
                + sysOtherAppDO.getSecret()
                + "&grant_type=client_credential");

        WxAccessTokenVO wxAccessTokenVO = JSONUtil.toBean(jsonStr, WxAccessTokenVO.class);

        // 检查：微信回调 vo对象
        checkWxVO(wxAccessTokenVO, errorMessageStr, tenantId, appId);

        CacheRedisKafkaLocalUtil
            .put(BaseRedisKeyEnum.WX_ACCESS_TOKEN_CACHE, sufKey, null,
                wxAccessTokenVO.getExpiresIn() * 1000,
                wxAccessTokenVO::getAccessToken);

        return wxAccessTokenVO.getAccessToken();

    }

    /**
     * 获取：企业微信全局唯一后台接口调用凭据
     */
    @NotNull
    public static String getAccessTokenForWork(@Nullable Long tenantId, String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        SysOtherAppTypeEnum sysOtherAppTypeEnum = SysOtherAppTypeEnum.WX_WORK;

        String sufKey = tenantId + ":" + sysOtherAppTypeEnum + ":" + appId;

        String accessToken = MyCacheUtil.onlyGet(BaseRedisKeyEnum.WX_WORK_ACCESS_TOKEN_CACHE,
            sufKey);

        if (StrUtil.isNotBlank(accessToken)) {
            return accessToken;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery()
            .eq(BaseEntityNoIdSuper::getTenantId, tenantId)
            .eq(SysOtherAppDO::getAppId, appId).eq(BaseEntityNoId::getEnableFlag, true)
            .eq(SysOtherAppDO::getType, sysOtherAppTypeEnum).select(SysOtherAppDO::getSecret)
            .one();

        String errorMessageStr = "accessTokenForWork";

        if (sysOtherAppDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg(), errorMessageStr);
        }

        String jsonStr = HttpUtil.get(
            "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + appId + "&corpsecret="
                + sysOtherAppDO.getSecret());

        WxAccessTokenVO wxAccessTokenVO = JSONUtil.toBean(jsonStr, WxAccessTokenVO.class);

        // 检查：微信回调 vo对象
        checkWxVO(wxAccessTokenVO, errorMessageStr, tenantId, appId);

        CacheRedisKafkaLocalUtil
            .put(BaseRedisKeyEnum.WX_WORK_ACCESS_TOKEN_CACHE, sufKey, null,
                wxAccessTokenVO.getExpiresIn() * 1000,
                wxAccessTokenVO::getAccessToken);

        return wxAccessTokenVO.getAccessToken();

    }

    /**
     * 获取：可以点击的标签
     */
    public static String getMsgmenucontentA(String pre, String sendValue, String showValue) {

        return getMsgmenucontentA(pre, sendValue, showValue, -1);

    }

    /**
     * 获取：可以点击的标签
     */
    public static String getMsgmenucontentA(String pre, String sendValue, String showValue,
        int index) {

        return pre + "<a href=\"weixin://bizmsgmenu?msgmenucontent=" + sendValue + "&msgmenuid="
            + index + "\">"
            + showValue + "</a>";

    }

    /**
     * 获取：临时二维码的url地址 注意：要保证 sceneStr + data的全局唯一性
     */
    @SneakyThrows
    public static String getQrCodeUrl(String accessToken, ISysQrCodeSceneType iSysQrCodeSceneType,
        @Nullable String data) {

        boolean foreverFlag = false;

        if (iSysQrCodeSceneType.getExpireSecond() <= 0) {

            foreverFlag = true;

        }

        String sceneStr = iSysQrCodeSceneType.getSceneStr();

        if (StrUtil.isNotBlank(data)) {

            sceneStr = sceneStr + ISysQrCodeSceneType.SEPARATOR + data;

        }

        // 执行
        return getQrCodeUrl(accessToken, sceneStr, foreverFlag,
            iSysQrCodeSceneType.getExpireSecond());

    }

    /**
     * 获取：二维码的url地址
     *
     * @param sceneStr     场景值，字符串类型，长度限制为1到64，一般为；用户的 wxOpenId
     * @param foreverFlag  是否是永久饿二维码地址，注意：永久二维码，是无过期时间的，但数量较少（目前为最多10万个），临时二维码，是有过期时间的，最长可以设置为在二维码生成后的30天（即2592000秒）后过期，但能够生成较多数量
     * @param expireSecond 该二维码有效时间，以秒为单位。 最大不超过2592000（即 30天），此字段如果不填，则默认有效期为 60秒。
     */
    @SneakyThrows
    public static String getQrCodeUrl(String accessToken, String sceneStr, boolean foreverFlag,
        @Nullable Integer expireSecond) {

        JSONObject jsonObject = JSONUtil.createObj()
            .set("action_name", foreverFlag ? "QR_LIMIT_STR_SCENE" : "QR_STR_SCENE")
            .set("action_info",
                JSONUtil.createObj().set("scene", JSONUtil.createObj().set("scene_str", sceneStr)));

        if (!foreverFlag && expireSecond != null) {

            jsonObject.set("expire_seconds", expireSecond);

        }

        String result =
            HttpUtil.post(
                "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken,
                jsonObject.toString());

        log.info("wx-qrcodeCreate-result：{}", result);

        String ticket = JSONUtil.parseObj(result).getStr("ticket");

        return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URLEncoder.encode(ticket,
            "UTF-8");

    }

    /**
     * 执行：发送文字消息
     */
    public static void doTextSend(String wxOpenId, String accessToken, String content) {

        MyStrUtil.subWithMaxByteLengthAndConsumer(content, 2048, subContent -> {

            // 执行
            execDoTextSend(wxOpenId, accessToken, subContent);

        });

    }

    /**
     * 执行：发送文字消息 注意：content的长度不要超过 600，这是微信官方那边的限制，不然会请求出错的 建议使用：doTextSend，方法，因为该方法会裁减
     */
    public static void execDoTextSend(String wxOpenId, String accessToken, String content) {

        if (StrUtil.isBlank(content)) {
            return;
        }

        String bodyJsonStr = JSONUtil.createObj().set("touser", wxOpenId).set("msgtype", "text")
            .set("text", JSONUtil.createObj().set("content", content)).toString();

        String sendResultStr = HttpUtil
            .post(
                "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken,
                bodyJsonStr);

        log.info("wx-sendResult-text：{}，touser：{}，content：{}", sendResultStr, wxOpenId, content);

    }

    /**
     * 执行：发送文字消息：企业微信
     */
    public static void doTextSendForWork(String wxOpenId, String accessToken, String content,
        Integer agentId) {

        MyStrUtil.subWithMaxByteLengthAndConsumer(content, 2048, subContent -> {

            // 执行
            execDoTextSendForWork(wxOpenId, accessToken, subContent, agentId);

        });

    }

    /**
     * 执行：发送文字消息 注意：content的长度不要超过 600，这是微信官方那边的限制，不然会请求出错的 建议使用：doTextSend，方法，因为该方法会裁减
     */
    public static void execDoTextSendForWork(String wxOpenId, String accessToken, String content,
        Integer agentId) {

        if (StrUtil.isBlank(content)) {
            return;
        }

        String bodyJsonStr = JSONUtil.createObj().set("touser", wxOpenId).set("msgtype", "text")
            .set("content", content).set("agentid", agentId).toString();

        String sendResultStr = HttpUtil
            .post("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + accessToken,
                bodyJsonStr);

        log.info("wxWork-sendResult-text：{}，touser：{}，content：{}", sendResultStr, wxOpenId,
            content);

    }

    /**
     * 执行：发送文字消息：企业微信客服
     */
    public static void doTextSendForWorkKf(String wxOpenId, String accessToken, String content,
        String openKfId) {

        MyStrUtil.subWithMaxByteLengthAndConsumer(content, 2048, subContent -> {

            // 执行
            execDoTextSendForWorkKf(wxOpenId, accessToken, subContent, openKfId);

        });

    }

    /**
     * 执行：发送文字消息 注意：content的长度不要超过 600，这是微信官方那边的限制，不然会请求出错的 建议使用：doTextSend，方法，因为该方法会裁减
     */
    public static void execDoTextSendForWorkKf(String wxOpenId, String accessToken, String content,
        String openKfId) {

        if (StrUtil.isBlank(content)) {
            return;
        }

        String bodyJsonStr = JSONUtil.createObj().set("touser", wxOpenId).set("open_kfid", openKfId)
            .set("msgtype", "text")
            .set("text", JSONUtil.createObj().set("content", content)).toString();

        String sendResultStr = HttpUtil
            .post("https://qyapi.weixin.qq.com/cgi-bin/kf/send_msg?access_token=" + accessToken,
                bodyJsonStr);

        log.info("wxWork-kf-sendResult-text：{}，touser：{}，content：{}", sendResultStr, wxOpenId,
            content);

    }

    /**
     * 企业微信：读取消息：最近 1000条
     */
    @NotNull
    public static List<JSONObject> syncMsg(String accessToken, Long tenantId, String token,
        String openKfId, String appId) {

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_SYS_WX_WORK_SYNC_MSG.name() + tenantId,
            () -> {

                RMap<Long, String> rMap = redissonClient.<Long, String>getMap(
                    BaseRedisKeyEnum.PRE_SYS_WX_WORK_SYNC_MSG.name());

                // 上一次调用时返回的 next_cursor，第一次拉取可以不填。若不填，从3天内最早的消息开始返回。
                String cursor = rMap.get(tenantId);

                JSONObject jsonObject = JSONUtil.createObj();

                if (StrUtil.isNotBlank(cursor)) {
                    jsonObject.set("cursor", cursor);
                }

                String bodyJsonStr = jsonObject.set("token", token).set("limit", 1000)
                    .set("open_kfid", openKfId).toString();

                String sendResultStr = HttpUtil
                    .post("https://qyapi.weixin.qq.com/cgi-bin/kf/sync_msg?access_token="
                        + accessToken, bodyJsonStr);

                log.info("wxWork-syncMsg：{}，tenantId：{}，openKfId：{}", sendResultStr, tenantId,
                    openKfId);

                WxSyncMsgVO wxSyncMsgVO = JSONUtil.toBean(sendResultStr, WxSyncMsgVO.class);

                // 检查：微信回调 vo对象
                checkWxVO(wxSyncMsgVO, "syncMsg", tenantId, appId);

                rMap.put(tenantId, wxSyncMsgVO.getNextCursor()); // 设置：下一次的游标值

                return wxSyncMsgVO.getMsgList();

            });

    }

    /**
     * 执行：上传图片
     */
    public static JSONObject uploadImageUrl(String accessToken, String url) {

        File file = null;

        try {

            // 获取：流
            InputStream inputStream = RetryUtil.execHttpRequestInputStream(HttpRequest.get(url));

            file = FileUtil.touch(
                FileTempPathConstant.WX_MEDIA_UPLOAD_TEMP_PATH + IdUtil.simpleUUID() + ".jpg");

            // 图片格式转换为：jpg格式
            ImgUtil.convert(inputStream, "JPG", FileUtil.getOutputStream(file));

            // 执行上传
            return upload(accessToken, file, WxMediaUploadTypeEnum.IMAGE);

        } finally {

            FileUtil.del(file); // 删除：文件

        }

    }

    /**
     * 执行：上传
     *
     * @return {"media_id": ""}
     */
    public static JSONObject upload(String accessToken, File file,
        WxMediaUploadTypeEnum wxMediaUploadTypeEnum) {

        String resultStr = HttpRequest.post(
            "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=" + accessToken + "&type="
                + wxMediaUploadTypeEnum.getName()).form("media", file).execute().body();

        log.info("wx-mediaUpload，result：{}", resultStr);

        return JSONUtil.parseObj(resultStr);

    }

    /**
     * 执行：发送图像消息
     */
    public static void doImageSend(String wxOpenId, String accessToken, String mediaId) {

        if (StrUtil.isBlank(mediaId)) {
            return;
        }

        String bodyJsonStr = JSONUtil.createObj().set("touser", wxOpenId).set("msgtype", "image")
            .set("image", JSONUtil.createObj().set("media_id", mediaId)).toString();

        String sendResultStr = HttpUtil
            .post(
                "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken,
                bodyJsonStr);

        log.info("wx-sendResult-image：{}，touser：{}", sendResultStr, wxOpenId);

    }

    /**
     * 执行：上传图片
     */
    public static JSONObject uploadImageUrlForWork(String accessToken, String url) {

        File file = null;

        try {

            // 获取：流
            InputStream inputStream = RetryUtil.execHttpRequestInputStream(HttpRequest.get(url));

            file = FileUtil.touch(
                FileTempPathConstant.WX_MEDIA_UPLOAD_TEMP_PATH + IdUtil.simpleUUID() + ".jpg");

            // 图片格式转换为：jpg格式
            ImgUtil.convert(inputStream, "JPG", FileUtil.getOutputStream(file));

            // 执行上传
            return uploadForWork(accessToken, file, WxMediaUploadTypeEnum.IMAGE);

        } finally {

            FileUtil.del(file); // 删除：文件

        }

    }

    /**
     * 执行：上传文件
     */
    public static JSONObject uploadFileUrlForWork(String accessToken, InputStream inputStream,
        String fileType) {

        File file = null;

        try {

            file = FileUtil.touch(
                FileTempPathConstant.WX_MEDIA_UPLOAD_TEMP_PATH + IdUtil.simpleUUID() + "."
                    + fileType);

            // 写入文件
            FileUtil.writeFromStream(inputStream, file);

            // 执行上传
            return uploadForWork(accessToken, file, WxMediaUploadTypeEnum.FILE);

        } finally {

            FileUtil.del(file); // 删除：文件

        }

    }

    /**
     * 执行：上传
     *
     * @return {"media_id": ""}
     */
    public static JSONObject uploadForWork(String accessToken, File file,
        WxMediaUploadTypeEnum wxMediaUploadTypeEnum) {

        String resultStr = HttpRequest.post(
            "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=" + accessToken
                + "&type="
                + wxMediaUploadTypeEnum.getName()).form("media", file).execute().body();

        log.info("wxWork-MediaUpload，result：{}", resultStr);

        return JSONUtil.parseObj(resultStr);

    }

    /**
     * 执行：发送图像消息
     */
    public static void doImageSendForWorkKf(String wxOpenId, String accessToken, String mediaId,
        String openKfId) {

        if (StrUtil.isBlank(mediaId)) {
            return;
        }

        String bodyJsonStr = JSONUtil.createObj().set("touser", wxOpenId).set("open_kfid", openKfId)
            .set("msgtype", "image")
            .set("image", JSONUtil.createObj().set("media_id", mediaId)).toString();

        String sendResultStr = HttpUtil
            .post("https://qyapi.weixin.qq.com/cgi-bin/kf/send_msg?access_token=" + accessToken,
                bodyJsonStr);

        log.info("wxWork-sendResult-image：{}，touser：{}", sendResultStr, wxOpenId);

    }

    /**
     * 执行：发送文件消息
     */
    public static void doFileSendForWorkKf(String wxOpenId, String accessToken, String mediaId,
        String openKfId) {

        if (StrUtil.isBlank(mediaId)) {
            return;
        }

        String bodyJsonStr = JSONUtil.createObj().set("touser", wxOpenId).set("open_kfid", openKfId)
            .set("msgtype", "file")
            .set("file", JSONUtil.createObj().set("media_id", mediaId)).toString();

        String sendResultStr = HttpUtil
            .post("https://qyapi.weixin.qq.com/cgi-bin/kf/send_msg?access_token=" + accessToken,
                bodyJsonStr);

        log.info("wxWork-sendResult-file：{}，touser：{}", sendResultStr, wxOpenId);

    }

    /**
     * 获取临时素材：url
     */
    public static String getMediaUrlForWork(String accessToken, String mediaId) {

        if (StrUtil.isBlank(mediaId)) {
            return null;
        }

        return "https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token=" + accessToken
            + "&media_id=" + mediaId;

    }

    /**
     * 执行：发送语音消息
     */
    public static void doVoiceSend(String wxOpenId, String accessToken, String mediaId) {

        if (StrUtil.isBlank(mediaId)) {
            return;
        }

        String bodyJsonStr = JSONUtil.createObj().set("touser", wxOpenId).set("msgtype", "voice")
            .set("voice", JSONUtil.createObj().set("media_id", mediaId)).toString();

        String sendResultStr = HttpUtil
            .post(
                "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken,
                bodyJsonStr);

        log.info("wx-sendResult-voice：{}，touser：{}", sendResultStr, wxOpenId);

    }

    /**
     * 执行：转人工客服
     */
    public static void toCustomerService(String openKfId, String accessToken,
        String externalUserId) {

        toCustomerService(openKfId, accessToken, externalUserId, 2, null);

    }

    /**
     * 执行：转人工客服
     *
     * @param serviceState   0 未处理 1 由智能助手接待 2 待接入池排队中 3 由人工接待 4 已结束/未开始
     * @param servicerUserId 接待人员的userid。第三方应用填密文userid，即open_userid。当state=3时要求必填，接待人员须处于“正在接待”中。
     */
    public static void toCustomerService(String openKfId, String accessToken, String externalUserId,
        int serviceState, @Nullable String servicerUserId) {

        if (StrUtil.isBlank(externalUserId)) {
            return;
        }

        JSONObject jsonObject = JSONUtil.createObj();

        if (StrUtil.isNotBlank(servicerUserId)) {

            jsonObject.set("servicer_userid", servicerUserId);

        }

        String bodyJsonStr = jsonObject.set("open_kfid", openKfId)
            .set("external_userid", externalUserId)
            .set("service_state", serviceState).toString();

        String sendResultStr = HttpUtil
            .post("https://qyapi.weixin.qq.com/cgi-bin/kf/service_state/trans?access_token="
                + accessToken, bodyJsonStr);

        log.info("wxWork-toCustomerServiceResult：{}，touser：{}，serviceState：{}", sendResultStr,
            externalUserId, serviceState);

    }

    /**
     * 获取接待人员列表
     */
    public static WxServicerListVO kfList(String openKfId, String accessToken, Long tenantId,
        String appId) {

        String sendResultStr = HttpUtil
            .get("https://qyapi.weixin.qq.com/cgi-bin/kf/servicer/list?access_token=" + accessToken
                + "&open_kfid=" + openKfId);

        log.info("wxWork-kfListResult：{}，openKfId：{}", sendResultStr, openKfId);

        WxServicerListVO wxServicerListVO = JSONUtil.toBean(sendResultStr, WxServicerListVO.class);

        // 检查：微信回调 vo对象
        checkWxVO(wxServicerListVO, "kfList", tenantId, appId);

        return wxServicerListVO;

    }

    /**
     * 获取会话状态：企业微信客服
     */
    @Nullable
    public static WxServiceStateVO serviceStateForWorkKf(String openKfId, String accessToken,
        String externalUserId, Long tenantId, String appId) {

        if (StrUtil.isBlank(externalUserId)) {
            return null;
        }

        String bodyJsonStr = JSONUtil.createObj().set("open_kfid", openKfId)
            .set("external_userid", externalUserId).toString();

        String sendResultStr = HttpUtil
            .post("https://qyapi.weixin.qq.com/cgi-bin/kf/service_state/get?access_token="
                + accessToken, bodyJsonStr);

        log.info("wxWork-serviceStateResult：{}，touser：{}", sendResultStr, externalUserId);

        WxServiceStateVO wxServiceStateVO = JSONUtil.toBean(sendResultStr, WxServiceStateVO.class);

        // 检查：微信回调 vo对象
        checkWxVO(wxServiceStateVO, "serviceState", tenantId, appId);

        return wxServiceStateVO;

    }

    /**
     * 执行：发送模板消息
     */
    public static void doTemplateMessageSend(String wxOpenId, String accessToken, String templateId,
        JSONObject data,
        String url) {

        if (StrUtil.isBlank(templateId)) {
            return;
        }

        JSONObject jsonObject =
            JSONUtil.createObj().set("touser", wxOpenId).set("template_id", templateId)
                .set("data", data);

        if (StrUtil.isNotBlank(url)) {
            jsonObject.set("url", url);
        }

        String bodyJsonStr = jsonObject.toString();

        String sendResultStr = HttpUtil
            .post("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="
                + accessToken, bodyJsonStr);

        log.info("wx-sendResult-templateMessage：{}，touser：{}", sendResultStr, wxOpenId);

    }

    /**
     * 发送模板消息时，需要用 value来包装
     */
    public static JSONObject getDoTemplateMessageSendValue(String value) {

        return JSONUtil.createObj().set("value", value);

    }

    /**
     * 检查：微信回调 vo对象
     */
    public static void checkWxVO(WxBaseVO wxBaseVO, String msg, long tenantId, String appId) {

        if (!checkWxVO(wxBaseVO)) {

            throw new RuntimeException(StrUtil
                .format("微信：获取【{}】失败，errcode：【{}】，errmsg：【{}】，tenantId：【{}】，appId：【{}】", msg,
                    wxBaseVO.getErrcode(),
                    wxBaseVO.getErrmsg(), tenantId, appId));

        }

    }

    /**
     * 检查：微信回调 vo对象
     *
     * @return 没有报错则返回：true，报错了则返回：false
     */
    public static boolean checkWxVO(WxBaseVO wxBaseVO) {

        if (wxBaseVO.getErrcode() != null && wxBaseVO.getErrcode() != 0) {

            return false;

        }

        return true;

    }

}
