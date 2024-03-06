package com.cmcorg20230301.be.engine.redisson.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import java.util.Date;
import java.util.LinkedList;
import javax.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

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

    // id池最小大小
    private static final int ID_SET_MIN_SIZE = 10;

    // id池最大大小
    private static final int ID_SET_SIZE = 100 + ID_SET_MIN_SIZE;

    // id池，备注：需要上锁进行操作
    private static final LinkedList<Long> ID_POOL = new LinkedList<>();

    @PostConstruct
    public void postConstruct() {

        fill(); // 填充

    }

    /**
     * 填充：id池
     */
    private static void fill() {

        if (ID_POOL.size() > ID_SET_MIN_SIZE) {
            return;
        }

        // 需要补充的数量
        int needSize = ID_SET_SIZE - ID_POOL.size();

        RAtomicLong atomicLong = redissonClient.getAtomicLong(
            BaseRedisKeyEnum.ATOMIC_LONG_ID_GENERATOR.name());

        long endId = atomicLong.addAndGet(needSize);

        int retryTotal = 0; // 重试次数

        while (endId > MAX_INCREMENT_VALUE) {

            if (retryTotal > 2) { // 循环 3次
                throw new RuntimeException("id获取异常，请联系管理员");
            }

            atomicLong.delete();
            endId = atomicLong.addAndGet(needSize);

            retryTotal = retryTotal + 1;

        }

        long beginId = endId - needSize + 1; // + 1，第一个 id不使用

        for (long i = beginId; i <= endId; i++) {

            ID_POOL.add(i); // 添加到：id池里

        }

    }

    /**
     * 获取：下一个 id 备注：一秒 999999个 id，并且可以使用到 2999年的最后一秒
     */
    @NotNull
    public static Long nextId() {

        // long最大值：9223372036854775807
        // 例如：20220928221425 -> 0220928221425
        String timeStr = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN)
            .substring(1);

        long id;

        synchronized (ID_POOL) {

            fill(); // 填充

            id = ID_POOL.removeFirst();

        }

        // 拼接：自增值，例如：220928221425999999
        return Convert.toLong(timeStr + StrUtil.padPre(String.valueOf(id), 6, "0"));

    }

}
