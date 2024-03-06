package com.cmcorg20230301.be.engine.security.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.service.SignOutService;
import com.cmcorg20230301.be.engine.security.util.MyJwtUtil;
import com.cmcorg20230301.be.engine.util.util.CallBack;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

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

        String jwtHash = MyJwtUtil.getJwtHashByRequest(httpServletRequest, jwtHashRemainMsCallBack,
            null);

        if (StrUtil.isBlank(jwtHash)) {
            return BaseBizCodeEnum.OK;
        }

        CacheRedisKafkaLocalUtil.put(jwtHash, jwtHashRemainMsCallBack.getValue(),
            () -> "不可用的 jwt：退出登录");

        return BaseBizCodeEnum.OK;

    }

}
