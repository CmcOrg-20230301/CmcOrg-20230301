package com.cmcorg20230301.engine.be.security.util;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.engine.be.cache.util.CacheHelper;
import com.cmcorg20230301.engine.be.cache.util.CacheUtil;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.security.mapper.SysParamMapper;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.entity.SysParamDO;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统参数 工具类
 */
@Component
public class SysParamUtil {

    private static SysParamMapper sysParamMapper;

    public SysParamUtil(SysParamMapper sysParamMapper) {
        SysParamUtil.sysParamMapper = sysParamMapper;
    }

    /**
     * 通过主键 id，获取 value，没有 value则返回 null
     */
    @Nullable
    public static String getValueById(Long id) {

        Map<Long, String> map = CacheUtil.get(RedisKeyEnum.SYS_PARAM_CACHE, CacheHelper.getDefaultLongMap(), () -> {

            List<SysParamDO> sysParamDOList =
                ChainWrappers.lambdaQueryChain(sysParamMapper).select(BaseEntity::getId, SysParamDO::getValue)
                    .eq(BaseEntity::getEnableFlag, true).list();

            // 注意：Collectors.toMap()方法，key不能重复，不然会报错
            // 可以用第三个参数，解决这个报错：(v1, v2) -> v2 不覆盖（留前值）(v1, v2) -> v1 覆盖（取后值）
            return sysParamDOList.stream().collect(Collectors.toMap(BaseEntity::getId, SysParamDO::getValue));

        });

        return map.get(id);

    }

}

