package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.cache.util.CacheHelper;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.ParamConstant;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysParamMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.SysParamDO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统参数 工具类
 */
@Component
@Slf4j
public class SysParamUtil {

    // 系统内置参数 uuidSet，备注：不允许删除
    // 备注：系统内置参数的 uuid等于 id
    public static final Set<String> SYSTEM_PARAM_UUID_SET = CollUtil.newHashSet(ParamConstant.RSA_PRIVATE_KEY_UUID, ParamConstant.IP_REQUESTS_PER_SECOND_UUID, ParamConstant.TENANT_REF_CHILDREN_FLAG_UUID);

    // 不允许删除的：参数主键 id
    public static final Set<String> SYSTEM_PARAM_NOT_DELETE_ID_SET = (Set<String>) CollUtil.addAll(new HashSet<>(SYSTEM_PARAM_UUID_SET), CollUtil.newHashSet(ParamConstant.DEFAULT_MANAGE_SIGN_IN_FLAG));


    private static SysParamMapper sysParamMapper;

    public SysParamUtil(SysParamMapper sysParamMapper) {
        SysParamUtil.sysParamMapper = sysParamMapper;
    }

    /**
     * 通过：参数的 uuid，获取 value，没有 value则返回 null
     * 备注：请不要直接传字符串，请在：ParamConstant 类里面加一个常量
     */
    @Nullable
    public static String getValueByUuid(String paramUuid, @Nullable Long tenantId) {

        if (SYSTEM_PARAM_UUID_SET.contains(paramUuid)) { // 如果是：系统内置参数

            tenantId = BaseConstant.TOP_TENANT_ID; // 则使用默认租户

        } else {

            if (tenantId == null) {

                tenantId = UserUtil.getCurrentTenantIdDefault();

            }

        }

        Map<Long, Map<String, String>> map = MyCacheUtil.getMap(BaseRedisKeyEnum.SYS_PARAM_CACHE, CacheHelper.getDefaultLongMapStringMap(), () -> {

            List<SysParamDO> sysParamDOList = ChainWrappers.lambdaQueryChain(sysParamMapper).select(SysParamDO::getUuid, SysParamDO::getValue, BaseEntityNoId::getTenantId).eq(BaseEntity::getEnableFlag, true).list();

            // 注意：Collectors.toMap()方法，key不能重复，不然会报错
            // 可以用第三个参数，解决这个报错：(v1, v2) -> v2 不覆盖（留前值）(v1, v2) -> v1 覆盖（取后值）
            return sysParamDOList.stream().collect(Collectors.groupingBy(BaseEntityNoId::getTenantId, Collectors.toMap(SysParamDO::getUuid, SysParamDO::getValue)));

        });

        String resultValue = map.get(tenantId).get(paramUuid);

        if (resultValue == null) { // 如果：不存在该参数，则从默认租户里面取

            return map.get(BaseConstant.TOP_TENANT_ID).get(paramUuid);

        } else {

            return resultValue;

        }

    }

}

