package com.cmcorg20230301.engine.be.wx.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.engine.be.cache.util.MyCacheUtil;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.wx.model.vo.WxAccessTokenVO;
import com.cmcorg20230301.engine.be.wx.model.vo.WxBaseVO;
import com.cmcorg20230301.engine.be.wx.model.vo.WxOpenIdVO;
import com.cmcorg20230301.engine.be.wx.model.vo.WxPhoneByCodeVO;
import com.cmcorg20230301.engine.be.wx.properties.WxProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class WxUtil {

    private static WxProperties wxProperties;

    public WxUtil(WxProperties wxProperties) {

        WxUtil.wxProperties = wxProperties;

    }

    /**
     * 通过微信小程序的 code，获取微信的 openId信息
     */
    @NotNull
    public static WxOpenIdVO getWxMiniProgramOpenIdVOByCode(String code) {

        String jsonStr = HttpUtil.get(
            "https://api.weixin.qq.com/sns/jscode2session?appid=" + wxProperties.getAppId() + "&secret=" + wxProperties
                .getSecret() + "&js_code=" + code + "&grant_type=authorization_code");

        WxOpenIdVO wxOpenIdVO = JSONUtil.toBean(jsonStr, WxOpenIdVO.class);

        checkWxVO(wxOpenIdVO, "miniProgramOpenId"); // 检查：微信回调 vo对象

        return wxOpenIdVO;

    }

    /**
     * code换取用户手机号信息，每个code只能使用一次，code的有效期为5min
     */
    @NotNull
    public static WxPhoneByCodeVO.WxPhoneInfoVO getWxMiniProgramPhoneInfoVOByCode(String code) {

        String accessToken = getAccessToken();

        JSONObject formJson = JSONUtil.createObj().set("code", code);

        String postStr = HttpUtil
            .post("https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken,
                formJson.toString());

        WxPhoneByCodeVO wxPhoneByCodeVO = JSONUtil.toBean(postStr, WxPhoneByCodeVO.class);

        checkWxVO(wxPhoneByCodeVO, "用户手机号"); // 检查：微信回调 vo对象

        return wxPhoneByCodeVO.getPhone_info();

    }

    /**
     * 通过微信浏览器的 code，获取微信的 openId信息
     */
    @NotNull
    public static WxOpenIdVO getWxBrowserOpenIdVOByCode(String code) {

        String jsonStr = HttpUtil.get(
            "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + wxProperties.getAppId() + "&secret="
                + wxProperties.getSecret() + "&code=" + code + "&grant_type=authorization_code");

        WxOpenIdVO wxOpenIdVO = JSONUtil.toBean(jsonStr, WxOpenIdVO.class);

        checkWxVO(wxOpenIdVO, "browserOpenId"); // 检查：微信回调 vo对象

        return wxOpenIdVO;

    }

    /**
     * 获取：微信小程序全局唯一后台接口调用凭据
     */
    @NotNull
    private static String getAccessToken() {

        String accessToken = MyCacheUtil.onlyGet(RedisKeyEnum.WX_ACCESS_TOKEN_CACHE, null, true);

        if (StrUtil.isNotBlank(accessToken)) {
            return accessToken;
        }

        String jsonStr = HttpUtil.get(
            "https://api.weixin.qq.com/cgi-bin/token?appid=" + wxProperties.getAppId() + "&secret=" + wxProperties
                .getSecret() + "&grant_type=client_credential");

        WxAccessTokenVO wxAccessTokenVO = JSONUtil.toBean(jsonStr, WxAccessTokenVO.class);

        // 检查：微信回调 vo对象
        checkWxVO(wxAccessTokenVO, "accessToken");

        CacheRedisKafkaLocalUtil.put(RedisKeyEnum.WX_ACCESS_TOKEN_CACHE.name(), wxAccessTokenVO.getExpires_in(),
            wxAccessTokenVO::getAccess_token);

        return wxAccessTokenVO.getAccess_token();

    }

    /**
     * 检查：微信回调 vo对象
     */
    private static void checkWxVO(WxBaseVO wxBaseVO, String msg) {

        if (wxBaseVO.getErrcode() != null && wxBaseVO.getErrcode() != 0) {

            throw new RuntimeException(StrUtil
                .format("微信：获取【{}】失败，errcode：【{}】，errmsg：【{}】", msg, wxBaseVO.getErrcode(), wxBaseVO.getErrmsg()));

        }

    }

}
