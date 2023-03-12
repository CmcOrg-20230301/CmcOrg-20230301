package com.cmcorg20230301.engine.be.redisson.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * id，生成工具类
 */
@Component
public class IdGeneratorUtil {

    private static RedissonClient redissonClient;

    public IdGeneratorUtil(RedissonClient redissonClient) {
        IdGeneratorUtil.redissonClient = redissonClient;
    }

    // 最大的，自增值
    private static final int MAX_INCREMENT_VALUE = 999999;

    /**
     * 获取：下一个 id
     * 备注：一秒 999999个 id，并且可以使用到 2999年的最后一秒
     */
    @NotNull
    public static Long nextId() {

        // long最大值：9223372036854775807
        // 例如：20220928221425 -> 0220928221425
        String timeStr = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN).substring(1);

        RAtomicLong atomicLong = redissonClient.getAtomicLong(RedisKeyEnum.ATOMIC_LONG_ID_GENERATOR.name());

        long incrementAndGet = atomicLong.incrementAndGet();

        if (incrementAndGet > MAX_INCREMENT_VALUE) {
            atomicLong.delete();
            incrementAndGet = atomicLong.incrementAndGet(); // 再次获取：自增值
        }

        // 拼接：自增值，例如：220928221425999999
        return Convert.toLong(timeStr + StrUtil.padPre(String.valueOf(incrementAndGet), 6, "0"));

    }

}
