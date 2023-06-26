package com.cmcorg20230301.engine.be.security.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.RegisteredPayload;
import com.cmcorg20230301.engine.be.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.service.SignOutService;
import com.cmcorg20230301.engine.be.security.util.MyJwtUtil;
import com.cmcorg20230301.engine.be.security.util.RequestUtil;
import com.cmcorg20230301.engine.be.security.util.UserUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class SignOutServiceImpl implements SignOutService {

    @Resource
    HttpServletRequest httpServletRequest;

    /**
     * 退出登录
     */
    @Override
    public String signOut() {

        // 从请求头里，获取：jwt字符串
        String jwtStr = MyJwtUtil.getJwtStrByRequest(httpServletRequest);

        if (jwtStr == null) {
            return BaseBizCodeEnum.OK;
        }

        JWT jwt = JWT.of(jwtStr); // 备注：这里不会报错

        JSONObject claimsJson = jwt.getPayload().getClaimsJson();

        Date expiresDate = claimsJson.getDate(RegisteredPayload.EXPIRES_AT);

        if (expiresDate == null) { // 备注：这里不会为 null
            return BaseBizCodeEnum.OK;
        }

        Long currentUserId = UserUtil.getCurrentUserId();

        String jwtHash = MyJwtUtil
            .generateRedisJwtHash(jwtStr, currentUserId, RequestUtil.getRequestCategoryEnum(httpServletRequest));

        // jwt剩余时间
        long remainMs = expiresDate.getTime() - System.currentTimeMillis();

        CacheRedisKafkaLocalUtil.put(jwtHash, remainMs, () -> "不可用的 jwt：退出登录");

        return BaseBizCodeEnum.OK;

    }
}
