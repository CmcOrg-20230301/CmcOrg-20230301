package com.cmcorg20230301.engine.be.security.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.service.SignOutService;
import com.cmcorg20230301.engine.be.security.util.MyJwtUtil;
import com.cmcorg20230301.engine.be.util.util.CallBack;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class SignOutServiceImpl implements SignOutService {

    @Resource
    HttpServletRequest httpServletRequest;

    /**
     * 退出登录
     */
    @Override
    public String signOut() {

        CallBack<Long> jwtHashRemainMsCallBack = new CallBack<>();

        String jwtHash = MyJwtUtil.getJwtHashByRequest(httpServletRequest, jwtHashRemainMsCallBack);

        if (StrUtil.isBlank(jwtHash)) {
            return BaseBizCodeEnum.OK;
        }

        CacheRedisKafkaLocalUtil.put(jwtHash, jwtHashRemainMsCallBack.getValue(), () -> "不可用的 jwt：退出登录");

        return BaseBizCodeEnum.OK;

    }

}
