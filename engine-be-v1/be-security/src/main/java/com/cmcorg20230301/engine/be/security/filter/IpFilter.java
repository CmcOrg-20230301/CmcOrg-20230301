package com.cmcorg20230301.engine.be.security.filter;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.cmcorg20230301.engine.be.cache.util.CacheLocalUtil;
import com.cmcorg20230301.engine.be.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.engine.be.cache.util.MyCacheUtil;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.model.model.constant.ParamConstant;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.security.properties.SecurityProperties;
import com.cmcorg20230301.engine.be.security.util.ResponseUtil;
import com.cmcorg20230301.engine.be.security.util.SysParamUtil;
import com.cmcorg20230301.engine.be.util.util.SeparatorUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ip 拦截器
 */
@Order(value = Integer.MIN_VALUE)
@Component
@WebFilter(urlPatterns = "/*")
public class IpFilter implements Filter {

    @Resource
    SecurityProperties securityProperties;

    @Resource
    RedissonClient redissonClient;

    private static final long TIMEOUT = BaseConstant.SECOND_20_EXPIRE_TIME;

    // ip请求速率 map，key：ip
    private static final TimedCache<String, AtomicInteger> IP_SPEED_MAP = CacheUtil.newTimedCache(TIMEOUT);

    static {

        // 定时清理 map，过期的条目
        IP_SPEED_MAP.schedulePrune(TIMEOUT + BaseConstant.SECOND_3_EXPIRE_TIME);

    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {

        if (BooleanUtil.isFalse(securityProperties.getIpFilterEnable())) {
            return;
        }

        String ip = ServletUtil.getClientIP((HttpServletRequest)request);

        // ip 请求速率处理
        String timeStr = ipCheckHandler(ip);

        if (timeStr == null) {
            chain.doFilter(request, response);
        } else {
            ResponseUtil.out((HttpServletResponse)response, "操作次数过多，请在 " + timeStr + "后，再进行操作");
        }

    }

    /**
     * ip 请求速率处理
     * 返回 null，则表示不在黑名单，不为 null，则会返回剩余移除黑名单的倒计时时间（字符串）
     */
    @Nullable
    private String ipCheckHandler(String ip) {

        String key = RedisKeyEnum.PRE_IP_BLACK + ":" + ip;

        // 判断是否在 黑名单里
        String ipBlackStr = MyCacheUtil.onlyGet(key, null, true);

        if (StrUtil.isNotBlank(ipBlackStr)) {

            // 获取：剩余时间
            long remainTime = CacheLocalUtil.getRemainTime(key);

            if (remainTime > 0) {
                // 如果在 黑名单里，则返回剩余时间
                return DateUtil.formatBetween(remainTime, BetweenFormatter.Level.SECOND); // 剩余时间（字符串）
            }

        }

        // 给 ip设置：请求次数
        return addIpTotal(ip);

    }

    /**
     * 给 ip设置：请求次数
     */
    private String addIpTotal(String ip) {

        // 获取：ip请求速率相关对象，不限制请求速率，则返回 null
        IpSpeedBO ipSpeedBO = getIpSpeedBO();

        if (ipSpeedBO == null) {
            return null;
        }

        AtomicInteger atomicInteger = IP_SPEED_MAP.get(ip, false);

        // 如果不存在
        if (atomicInteger == null) {

            atomicInteger = new AtomicInteger(0);

            IP_SPEED_MAP.put(ip, atomicInteger, ipSpeedBO.getTimeS() * 1000); // 备注：0 表示永久存活

        }

        int incrementAndGet = atomicInteger.incrementAndGet(); // 次数 + 1

        if (incrementAndGet > ipSpeedBO.getTotal()) {

            IP_SPEED_MAP.remove(ip); // 移除：ip计数

            CacheRedisKafkaLocalUtil
                .put(RedisKeyEnum.PRE_IP_BLACK, ":" + ip, "黑名单 ip", BaseConstant.DAY_1_EXPIRE_TIME, null);

            return "24小时";

        }

        return null;

    }

    @Data
    @AllArgsConstructor
    private static class IpSpeedBO {

        Integer timeS; // 多少秒钟

        Integer total; // 可以请求多少次

    }

    /**
     * 获取：ip请求速率相关对象，不限制请求速率，则返回 null
     */
    @Nullable
    private IpSpeedBO getIpSpeedBO() {

        // ip 请求速率：多少秒钟，一个 ip可以请求多少次，用冒号隔开的
        String ipTotalCheckValue = SysParamUtil.getValueById(ParamConstant.IP_REQUESTS_PER_SECOND_ID);

        if (ipTotalCheckValue == null) {
            return null;
        }

        List<String> splitTrimList = StrUtil.splitTrim(ipTotalCheckValue, SeparatorUtil.COLON_SEPARATOR);

        if (splitTrimList.size() != 2) {
            return null;
        }

        Integer timeInt = Convert.toInt(splitTrimList.get(0)); // 多少秒钟
        if (timeInt == null || timeInt <= 0) {
            return null;
        }

        Integer total = Convert.toInt(splitTrimList.get(1)); // 可以请求多少次
        if (total == null || total <= 0) {
            return null;
        }

        return new IpSpeedBO(timeInt, total);

    }

}
