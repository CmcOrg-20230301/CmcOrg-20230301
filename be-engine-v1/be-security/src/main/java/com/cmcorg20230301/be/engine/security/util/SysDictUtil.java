package com.cmcorg20230301.be.engine.security.util;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.cache.util.CacheHelper;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.vo.DictIntegerVO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysDictMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.SysDictDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysDictDictKeyEnum;
import com.cmcorg20230301.be.engine.security.model.enums.SysDictTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统字典 工具类
 */
@Component
@Slf4j
public class SysDictUtil {

    private static SysDictMapper sysDictMapper;

    @Resource
    public void setSysDictMapper(SysDictMapper sysDictMapper) {
        SysDictUtil.sysDictMapper = sysDictMapper;
    }

    /**
     * 通过：dictKey获取字典项集合，备注：会进行缓存
     */
    public static List<DictIntegerVO> listByDictKey(SysDictDictKeyEnum sysDictDictKeyEnum) {

        return listByDictKey(sysDictDictKeyEnum.name().toLowerCase());

    }

    /**
     * 通过：dictKey获取字典项集合，备注：会进行缓存
     */
    @Unmodifiable // 不可对返回值进行修改
    public static List<DictIntegerVO> listByDictKey(String dictKey) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        Map<Long, Map<String, List<DictIntegerVO>>> dictMap =
            MyCacheUtil.getMap(BaseRedisKeyEnum.SYS_DICT_CACHE, CacheHelper.getDefaultLongMapStringListMap(), () -> {

                return ChainWrappers.lambdaQueryChain(sysDictMapper).eq(SysDictDO::getType, SysDictTypeEnum.DICT_ITEM)
                    .eq(BaseEntityNoId::getEnableFlag, true) //
                    .select(SysDictDO::getValue, SysDictDO::getName, SysDictDO::getDictKey,
                        BaseEntityNoId::getTenantId) //
                    .orderByDesc(SysDictDO::getOrderNo).list() //
                    .stream().collect(Collectors.groupingBy(BaseEntityNoId::getTenantId, Collectors
                        .groupingBy(SysDictDO::getDictKey, Collectors
                            .mapping(it -> new DictIntegerVO(it.getValue(), it.getName()), Collectors.toList()))));

            });

        return dictMap.get(currentTenantIdDefault).get(dictKey);

    }

}

