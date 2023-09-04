package com.cmcorg20230301.be.engine.security.util;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.cache.util.CacheHelper;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.vo.DictVO;
import com.cmcorg20230301.be.engine.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysDictMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.SysDictDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysDictDictKeyEnum;
import com.cmcorg20230301.be.engine.security.model.enums.SysDictTypeEnum;
import lombok.extern.slf4j.Slf4j;
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
    public static List<DictVO> listByDictKey(SysDictDictKeyEnum sysDictDictKeyEnum) {

        return listByDictKey(sysDictDictKeyEnum.name().toLowerCase());

    }

    /**
     * 通过：dictKey获取字典项集合，备注：会进行缓存
     */
    public static List<DictVO> listByDictKey(String dictKey) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        Map<Long, Map<String, List<DictVO>>> dictMap =
            MyCacheUtil.getMap(RedisKeyEnum.SYS_DICT_CACHE, CacheHelper.getDefaultLongMapStringListMap(), () -> {

                return ChainWrappers.lambdaQueryChain(sysDictMapper).eq(SysDictDO::getType, SysDictTypeEnum.DICT_ITEM)
                    .eq(BaseEntityNoId::getEnableFlag, true) //
                    .select(SysDictDO::getValue, SysDictDO::getName, SysDictDO::getDictKey,
                        BaseEntityNoId::getTenantId) //
                    .orderByDesc(SysDictDO::getOrderNo).list() //
                    .stream().collect(Collectors.groupingBy(BaseEntityNoId::getTenantId, Collectors
                        .groupingBy(SysDictDO::getDictKey, Collectors
                            .mapping(it -> new DictVO(it.getValue().longValue(), it.getName()), Collectors.toList()))));

            });

        return dictMap.get(currentTenantIdDefault).get(dictKey);

    }

}

