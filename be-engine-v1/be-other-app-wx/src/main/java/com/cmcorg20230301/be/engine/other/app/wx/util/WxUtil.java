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
import com.cmcorg20230301.be.engine.other.app.service.SysOtherAppService;
import com.cmcorg20230301.be.engine.other.app.wx.model.enums.WxMediaUploadTypeEnum;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.*;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.interfaces.ISysQrCodeSceneType;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.util.util.MyStrUtil;
import com.cmcorg20230301.be.engine.util.util.RetryUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;

@Component
@Slf4j(topic = LogTopicConstant.OTHER_APP_WX)
public class WxUtil {

    private static SysOtherAppService sysOtherAppService;

    public WxUtil(SysOtherAppService sysOtherAppService) {

        WxUtil.sysOtherAppService = sysOtherAppService;

    }

    /**
     * 通过微信小程序的 code，获取微信的 openId信息
     */
    @NotNull
    public static WxOpenIdVO getWxMiniProgramOpenIdVoByCode(@Nullable Long tenantId, String code, String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
                .eq(SysOtherAppDO::getAppId, appId).eq(BaseEntityNoId::getEnableFlag, true).select(SysOtherAppDO::getSecret)
                .one();

        String errorMessageStr = "miniProgramOpenId";

        if (sysOtherAppDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg(), errorMessageStr);
        }

        String jsonStr = HttpUtil.get(
                "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + sysOtherAppDO.getSecret()
                        + "&js_code=" + code + "&grant_type=authorization_code");

        WxOpenIdVO wxOpenIdVO = JSONUtil.toBean(jsonStr, WxOpenIdVO.class);

        checkWxVO(wxOpenIdVO, errorMessageStr, tenantId, appId); // 检查：微信回调 vo对象

        return wxOpenIdVO;

    }

    /**
     * code换取用户手机号信息，每个code只能使用一次，code的有效期为5min
     */
    @NotNull
    public static WxPhoneByCodeVO.WxPhoneInfoVO getWxMiniProgramPhoneInfoVoByCode(@Nullable Long tenantId, String code,
                                                                                  String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
                .eq(SysOtherAppDO::getAppId, appId).eq(BaseEntityNoId::getEnableFlag, true).select(SysOtherAppDO::getSecret)
                .one();

        String errorMessageStr = "用户手机号";

        if (sysOtherAppDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg(), errorMessageStr);
        }

        // 获取：accessToken
        String accessToken = getAccessToken(tenantId, appId);

        JSONObject formJson = JSONUtil.createObj().set("code", code);

        String postStr = HttpUtil
                .post("https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken,
                        formJson.toString());

        WxPhoneByCodeVO wxPhoneByCodeVO = JSONUtil.toBean(postStr, WxPhoneByCodeVO.class);

        checkWxVO(wxPhoneByCodeVO, errorMessageStr, tenantId, appId); // 检查：微信回调 vo对象

        return wxPhoneByCodeVO.getPhone_info();

    }

    /**
     * 通过微信浏览器的 code，获取微信的 openId信息
     */
    @NotNull
    public static WxOpenIdVO getWxBrowserOpenIdVoByCode(@Nullable Long tenantId, String code, String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
                .eq(SysOtherAppDO::getAppId, appId).eq(BaseEntityNoId::getEnableFlag, true).select(SysOtherAppDO::getSecret)
                .one();

        String errorMessageStr = "browserOpenId";

        if (sysOtherAppDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg(), errorMessageStr);
        }

        String jsonStr = HttpUtil.get(
                "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + sysOtherAppDO.getSecret()
                        + "&code=" + code + "&grant_type=authorization_code");

        WxOpenIdVO wxOpenIdVO = JSONUtil.toBean(jsonStr, WxOpenIdVO.class);

        checkWxVO(wxOpenIdVO, errorMessageStr, tenantId, appId); // 检查：微信回调 vo对象

        return wxOpenIdVO;

    }

    /**
     * 通过微信浏览器的 access_token，获取：微信里该用户信息
     */
    @NotNull
    public static WxUserInfoVO getWxUserInfoByBrowserAccessToken(String accessToken, String openId, Long tenantId,
                                                                 String appId) {

        return getWxUserInfoByBrowserAccessToken(accessToken, openId, "zh_CN", tenantId, appId);

    }

    /**
     * 通过微信浏览器的 access_token，获取：微信里该用户信息
     *
     * @param lang zh_CN 简体，zh_TW 繁体，en 英语
     */
    @NotNull
    public static WxUserInfoVO getWxUserInfoByBrowserAccessToken(String accessToken, String openId, String lang,
                                                                 Long tenantId, String appId) {

        String jsonStr = HttpUtil.get(
                "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId + "&lang="
                        + lang);

        WxUserInfoVO wxUserInfoVO = JSONUtil.toBean(jsonStr, WxUserInfoVO.class);

        checkWxVO(wxUserInfoVO, "微信用户信息", tenantId, appId); // 检查：微信回调 vo对象

        return wxUserInfoVO;

    }

    /**
     * 获取：微信小程序全局唯一后台接口调用凭据
     */
    @NotNull
    public static String getAccessToken(@Nullable Long tenantId, String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        String sufKey = tenantId + ":" + appId;

        String accessToken = MyCacheUtil.onlyGet(BaseRedisKeyEnum.WX_ACCESS_TOKEN_CACHE, sufKey);

        if (StrUtil.isNotBlank(accessToken)) {
            return accessToken;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
                .eq(SysOtherAppDO::getAppId, appId).eq(BaseEntityNoId::getEnableFlag, true).select(SysOtherAppDO::getSecret)
                .one();

        String errorMessageStr = "accessToken";

        if (sysOtherAppDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg(), errorMessageStr);
        }

        String jsonStr = HttpUtil.get(
                "https://api.weixin.qq.com/cgi-bin/token?appid=" + appId + "&secret=" + sysOtherAppDO.getSecret()
                        + "&grant_type=client_credential");

        WxAccessTokenVO wxAccessTokenVO = JSONUtil.toBean(jsonStr, WxAccessTokenVO.class);

        // 检查：微信回调 vo对象
        checkWxVO(wxAccessTokenVO, errorMessageStr, tenantId, appId);

        CacheRedisKafkaLocalUtil
                .put(BaseRedisKeyEnum.WX_ACCESS_TOKEN_CACHE, sufKey, null, wxAccessTokenVO.getExpiresIn() * 1000,
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
    public static String getMsgmenucontentA(String pre, String sendValue, String showValue, int index) {

        return pre + "<a href=\"weixin://bizmsgmenu?msgmenucontent=" + sendValue + "&msgmenuid=" + index + "\">"
                + showValue + "</a>";

    }

    /**
     * 获取：临时二维码的url地址
     * 注意：要保证 sceneStr + data的全局唯一性
     */
    @SneakyThrows
    public static String getQrCodeUrl(String accessToken, ISysQrCodeSceneType iSysQrCodeSceneType, @Nullable String data) {

        boolean foreverFlag = false;

        if (iSysQrCodeSceneType.getExpireSecond() <= 0) {

            foreverFlag = true;

        }

        String sceneStr = iSysQrCodeSceneType.getSceneStr();

        if (StrUtil.isNotBlank(data)) {

            sceneStr = sceneStr + ISysQrCodeSceneType.SEPARATOR + data;

        }

        // 执行
        return getQrCodeUrl(accessToken, sceneStr, foreverFlag, iSysQrCodeSceneType.getExpireSecond());

    }

    /**
     * 获取：二维码的url地址
     *
     * @param sceneStr     场景值，字符串类型，长度限制为1到64，一般为；用户的 wxOpenId
     * @param foreverFlag  是否是永久饿二维码地址，注意：永久二维码，是无过期时间的，但数量较少（目前为最多10万个），临时二维码，是有过期时间的，最长可以设置为在二维码生成后的30天（即2592000秒）后过期，但能够生成较多数量
     * @param expireSecond 该二维码有效时间，以秒为单位。 最大不超过2592000（即 30天），此字段如果不填，则默认有效期为 60秒。
     */
    @SneakyThrows
    public static String getQrCodeUrl(String accessToken, String sceneStr, boolean foreverFlag, @Nullable Integer expireSecond) {

        JSONObject jsonObject = JSONUtil.createObj().set("action_name", foreverFlag ? "QR_LIMIT_STR_SCENE" : "QR_STR_SCENE")
                .set("action_info", JSONUtil.createObj().set("scene", JSONUtil.createObj().set("scene_str", sceneStr)));

        if (!foreverFlag && expireSecond != null) {

            jsonObject.set("expire_seconds", expireSecond);

        }

        String result =
                HttpUtil.post("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken, jsonObject.toString());

        log.info("wx-qrcodeCreate-result：{}", result);

        String ticket = JSONUtil.parseObj(result).getStr("ticket");

        return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URLEncoder.encode(ticket, "UTF-8");

    }

    /**
     * 执行：发送文字消息
     */
    public static void doTextSend(String wxOpenId, String accessToken, String content) {

        MyStrUtil.subWithMaxLengthAndConsumer(content, 600, subContent -> {

            // 执行
            execDoTextSend(wxOpenId, accessToken, subContent);

        });

    }

    /**
     * 执行：发送文字消息
     * 注意：content的长度不要超过 600，这是微信官方那边的限制，不然会请求出错的
     * 建议使用：doTextSend，方法，因为该方法会裁减
     */
    public static void execDoTextSend(String wxOpenId, String accessToken, String content) {

        if (StrUtil.isBlank(content)) {
            return;
        }

        String bodyJsonStr = JSONUtil.createObj().set("touser", wxOpenId).set("msgtype", "text")
                .set("text", JSONUtil.createObj().set("content", content)).toString();

        String sendResultStr = HttpUtil
                .post("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken, bodyJsonStr);

        log.info("wx-sendResult-text：{}，touser：{}，content：{}", sendResultStr, wxOpenId, content);

    }

    /**
     * 执行：上传图片
     */
    public static JSONObject uploadImageUrl(String accessToken, String url) {

        File file = null;

        try {

            // 获取：流
            InputStream inputStream = RetryUtil.execHttpRequestInputStream(HttpRequest.get(url));

            file = FileUtil.touch(FileTempPathConstant.WX_MEDIA_UPLOAD_TEMP_PATH + IdUtil.simpleUUID() + ".jpg");

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
    public static JSONObject upload(String accessToken, File file, WxMediaUploadTypeEnum wxMediaUploadTypeEnum) {

        String resultStr = HttpRequest.post(
                "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=" + accessToken + "&type="
                        + wxMediaUploadTypeEnum.getName()).form("media", file).execute().body();

        log.info("WxMediaUpload，result：{}", resultStr);

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
                .post("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken, bodyJsonStr);

        log.info("wx-sendResult-image：{}，touser：{}", sendResultStr, wxOpenId);

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
                .post("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken, bodyJsonStr);

        log.info("wx-sendResult-voice：{}，touser：{}", sendResultStr, wxOpenId);

    }

    /**
     * 执行：发送模板消息
     */
    public static void doTemplateMessageSend(String wxOpenId, String accessToken, String templateId, JSONObject data,
                                             String url) {

        if (StrUtil.isBlank(templateId)) {
            return;
        }

        JSONObject jsonObject =
                JSONUtil.createObj().set("touser", wxOpenId).set("template_id", templateId).set("data", data);

        if (StrUtil.isNotBlank(url)) {
            jsonObject.set("url", url);
        }

        String bodyJsonStr = jsonObject.toString();

        String sendResultStr = HttpUtil
                .post("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken, bodyJsonStr);

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
                    .format("微信：获取【{}】失败，errcode：【{}】，errmsg：【{}】，tenantId：【{}】，appId：【{}】", msg, wxBaseVO.getErrcode(),
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
