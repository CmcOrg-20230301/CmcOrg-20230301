package com.cmcorg20230301.be.engine.wx.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.sign.helper.model.entity.SysOtherAppDO;
import com.cmcorg20230301.be.engine.sign.helper.service.SysOtherAppService;
import com.cmcorg20230301.be.engine.wx.model.vo.WxAccessTokenVO;
import com.cmcorg20230301.be.engine.wx.model.vo.WxBaseVO;
import com.cmcorg20230301.be.engine.wx.model.vo.WxOpenIdVO;
import com.cmcorg20230301.be.engine.wx.model.vo.WxPhoneByCodeVO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
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
            tenantId = BaseConstant.TENANT_ID;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdFather::getTenantId, tenantId)
            .eq(SysOtherAppDO::getAppId, appId).select(SysOtherAppDO::getSecret).one();

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
            tenantId = BaseConstant.TENANT_ID;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdFather::getTenantId, tenantId)
            .eq(SysOtherAppDO::getAppId, appId).select(SysOtherAppDO::getSecret).one();

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
            tenantId = BaseConstant.TENANT_ID;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdFather::getTenantId, tenantId)
            .eq(SysOtherAppDO::getAppId, appId).select(SysOtherAppDO::getSecret).one();

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
     * 获取：微信小程序全局唯一后台接口调用凭据
     */
    @NotNull
    private static String getAccessToken(@Nullable Long tenantId, String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TENANT_ID;
        }

        String accessToken = MyCacheUtil.onlyGet(RedisKeyEnum.WX_ACCESS_TOKEN_CACHE, appId);

        if (StrUtil.isNotBlank(accessToken)) {
            return accessToken;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdFather::getTenantId, tenantId)
            .eq(SysOtherAppDO::getAppId, appId).select(SysOtherAppDO::getSecret).one();

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
            .put(RedisKeyEnum.WX_ACCESS_TOKEN_CACHE, appId, null, wxAccessTokenVO.getExpires_in() * 1000,
                wxAccessTokenVO::getAccess_token);

        return wxAccessTokenVO.getAccess_token();

    }

    /**
     * 检查：微信回调 vo对象
     */
    private static void checkWxVO(WxBaseVO wxBaseVO, String msg, long tenantId, String appId) {

        if (wxBaseVO.getErrcode() != null && wxBaseVO.getErrcode() != 0) {

            throw new RuntimeException(StrUtil
                .format("微信：获取【{}】失败，errcode：【{}】，errmsg：【{}】，tenantId：【{}】，appId：【{}】", msg, wxBaseVO.getErrcode(),
                    wxBaseVO.getErrmsg(), tenantId, appId));

        }

    }

}
