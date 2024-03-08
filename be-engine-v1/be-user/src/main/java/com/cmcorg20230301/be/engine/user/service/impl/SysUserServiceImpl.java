package com.cmcorg20230301.be.engine.user.service.impl;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.dept.service.SysDeptRefUserService;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.BaseRegexConstant;
import com.cmcorg20230301.be.engine.model.model.constant.ParamConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.interfaces.IRedisKey;
import com.cmcorg20230301.be.engine.model.model.vo.DictVO;
import com.cmcorg20230301.be.engine.post.service.SysPostRefUserService;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.role.service.SysRoleRefUserService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.properties.SecurityProperties;
import com.cmcorg20230301.be.engine.security.util.*;
import com.cmcorg20230301.be.engine.sign.helper.util.SignUtil;
import com.cmcorg20230301.be.engine.tenant.service.SysTenantRefUserService;
import com.cmcorg20230301.be.engine.user.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.user.mapper.SysUserProMapper;
import com.cmcorg20230301.be.engine.user.model.dto.SysUserDictListDTO;
import com.cmcorg20230301.be.engine.user.model.dto.SysUserInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.user.model.dto.SysUserPageDTO;
import com.cmcorg20230301.be.engine.user.model.dto.SysUserUpdatePasswordDTO;
import com.cmcorg20230301.be.engine.user.model.vo.SysUserInfoByIdVO;
import com.cmcorg20230301.be.engine.user.model.vo.SysUserPageVO;
import com.cmcorg20230301.be.engine.user.service.SysUserService;
import com.cmcorg20230301.be.engine.util.util.MyMapUtil;
import com.cmcorg20230301.be.engine.util.util.NicknameUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserProMapper, SysUserDO> implements SysUserService {

    @Resource
    SysRoleRefUserService sysRoleRefUserService;

    @Resource
    SysUserInfoMapper sysUserInfoMapper;

    @Resource
    SecurityProperties securityProperties;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SysDeptRefUserService sysDeptRefUserService;

    @Resource
    SysPostRefUserService sysPostRefUserService;

    @Resource
    SysTenantRefUserService sysTenantRefUserService;

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserPageVO> myPage(SysUserPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, false);

        Page<SysUserPageVO> dtoPage = dto.page(false);

        // 备注：mysql 是先 group by 再 order by
        Page<SysUserPageVO> page = baseMapper.myPage(dtoPage, dto);

        Set<Long> userIdSet = new HashSet<>(MyMapUtil.getInitialCapacity(page.getRecords().size()));

        for (SysUserPageVO item : page.getRecords()) {

            // 备注：要和 userSelfInfo接口保持一致
            item.setEmail(DesensitizedUtil.email(item.getEmail())); // 脱敏
            item.setSignInName(DesensitizedUtil.chineseName(item.getSignInName())); // 脱敏
            item.setPhone(DesensitizedUtil.mobilePhone(item.getPhone())); // 脱敏
            item.setWxOpenId(StrUtil.hide(item.getWxOpenId(), 3, item.getWxOpenId().length() - 4)); // 脱敏：只显示前 3位，后 4位
            item.setWxAppId(StrUtil.hide(item.getWxAppId(), 3, item.getWxAppId().length() - 4)); // 脱敏：只显示前 3位，后 4位

            userIdSet.add(item.getId());

        }

        if (userIdSet.size() != 0) {

            // 处理：关联的数据
            handleRefData(page, userIdSet);

        }

        return page;

    }

    /**
     * 处理：关联的数据
     */
    private void handleRefData(Page<SysUserPageVO> page, Set<Long> userIdSet) {

        List<SysDeptRefUserDO> sysDeptRefUserDOList =
            sysDeptRefUserService.lambdaQuery().in(SysDeptRefUserDO::getUserId, userIdSet)
                .select(SysDeptRefUserDO::getUserId, SysDeptRefUserDO::getDeptId).list();

        List<SysPostRefUserDO> sysPostRefUserDOList =
            sysPostRefUserService.lambdaQuery().in(SysPostRefUserDO::getUserId, userIdSet)
                .select(SysPostRefUserDO::getUserId, SysPostRefUserDO::getPostId).list();

        Map<Long, Set<Long>> deptUserGroupMap =
            sysDeptRefUserDOList.stream().collect(Collectors.groupingBy(SysDeptRefUserDO::getUserId,
                Collectors.mapping(SysDeptRefUserDO::getDeptId, Collectors.toSet())));

        Map<Long, Set<Long>> postUserGroupMap =
            sysPostRefUserDOList.stream().collect(Collectors.groupingBy(SysPostRefUserDO::getUserId,
                Collectors.mapping(SysPostRefUserDO::getPostId, Collectors.toSet())));

        Map<Long, Set<Long>> userRefRoleIdSetMap = UserUtil.getUserRefRoleIdSetMap();

        Map<Long, Set<Long>> userIdRefTenantIdSetMap = SysTenantUtil.getUserIdRefTenantIdSetMap();

        page.getRecords().forEach(it -> {

            it.setRoleIdSet(userRefRoleIdSetMap.get(it.getId()));

            it.setDeptIdSet(deptUserGroupMap.get(it.getId()));

            it.setPostIdSet(postUserGroupMap.get(it.getId()));

            it.setTenantIdSet(userIdRefTenantIdSetMap.get(it.getId()));

            // 获取
            Boolean manageSignInFlag = getManageSignInFlag(it.getId(), it.getTenantId());

            it.setManageSignInFlag(manageSignInFlag);

        });

    }

    /**
     * 下拉列表
     */
    @Override
    public Page<DictVO> dictList(SysUserDictListDTO dto) {

        // 获取：用户关联的租户
        Set<Long> tenantIdSet;

        if (BooleanUtil.isTrue(dto.getAllTenantUserFlag())) {

            tenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        } else {

            Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

            tenantIdSet = CollUtil.newHashSet(currentTenantIdDefault);

        }

        // 获取所有：用户信息
        List<SysUserInfoDO> sysUserInfoDOList = ChainWrappers.lambdaQueryChain(sysUserInfoMapper)
            .select(SysUserInfoDO::getId, SysUserInfoDO::getNickname, SysUserInfoDO::getTenantId)
            .orderByDesc(SysUserInfoDO::getId).list();

        List<DictVO> dictVOList = sysUserInfoDOList.stream().filter(it -> tenantIdSet.contains(it.getTenantId()))
            .map(it -> new DictVO(it.getId(), it.getNickname())).collect(Collectors.toList());

        // 增加 admin账号
        if (BooleanUtil.isTrue(dto.getAddAdminFlag())) {

            dictVOList.add(new DictVO(BaseConstant.ADMIN_ID, securityProperties.getAdminNickname()));

        }

        return new Page<DictVO>().setTotal(dictVOList.size()).setRecords(dictVOList);

    }

    /**
     * 新增/修改
     */
    @Override
    @DSTransactional
    public String insertOrUpdate(SysUserInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        boolean emailBlank = StrUtil.isBlank(dto.getEmail());
        boolean signInNameBlank = StrUtil.isBlank(dto.getSignInName());
        boolean phoneBlank = StrUtil.isBlank(dto.getPhone());
        boolean wxAppIdBlank = StrUtil.isBlank(dto.getWxAppId());
        boolean wxOpenIdBlank = StrUtil.isBlank(dto.getWxOpenId());

        if (emailBlank && signInNameBlank && phoneBlank && wxAppIdBlank && wxOpenIdBlank) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_CANNOT_BE_EMPTY);
        }

        if ((!wxAppIdBlank && wxOpenIdBlank) || (wxAppIdBlank && !wxOpenIdBlank)) {
            ApiResultVO.errorMsg("操作失败：微信appId和微信openId，必须都有值");
        }

        boolean passwordFlag = StrUtil.isNotBlank(dto.getPassword()) && StrUtil.isNotBlank(dto.getOriginPassword());

        if (dto.getId() == null && passwordFlag) { // 只有新增时，才可以设置密码

            // 处理密码
            insertOrUpdateHandlePassword(dto);

        }

        Set<Enum<? extends IRedisKey>> redisKeyEnumSet = CollUtil.newHashSet();

        if (!emailBlank) {
            redisKeyEnumSet.add(BaseRedisKeyEnum.PRE_EMAIL);
        }

        if (!signInNameBlank) {
            redisKeyEnumSet.add(BaseRedisKeyEnum.PRE_SIGN_IN_NAME);
        }

        if (!phoneBlank) {
            redisKeyEnumSet.add(BaseRedisKeyEnum.PRE_PHONE);
        }

        if (!wxAppIdBlank) {
            redisKeyEnumSet.add(BaseRedisKeyEnum.PRE_WX_APP_ID);
        }

        if (!wxOpenIdBlank) {
            redisKeyEnumSet.add(BaseRedisKeyEnum.PRE_WX_OPEN_ID);
        }

        // 执行
        return doInsertOrUpdate(dto, redisKeyEnumSet);

    }

    /**
     * 执行：新增/修改
     */
    private String doInsertOrUpdate(SysUserInsertOrUpdateDTO dto, Set<Enum<? extends IRedisKey>> redisKeyEnumSet) {

        return RedissonUtil.doMultiLock(null, redisKeyEnumSet, () -> {

            Map<Enum<? extends IRedisKey>, String> accountMap = MapUtil.newHashMap();

            for (Enum<? extends IRedisKey> item : redisKeyEnumSet) {

                // 检查：账号是否存在
                if (accountIsExist(dto, item, accountMap, dto.getTenantId())) {

                    SignUtil.accountIsExistError();

                }

            }

            SysUserDO sysUserDO;

            if (dto.getId() == null) { // 新增：用户

                SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();

                sysUserInfoDO.setNickname(dto.getNickname());
                sysUserInfoDO.setBio(dto.getBio());

                sysUserDO = SignUtil.insertUser(dto.getPassword(), accountMap, false, sysUserInfoDO,
                    dto.getEnableFlag(), dto.getTenantId());

                insertOrUpdateSub(sysUserDO, dto); // 新增数据到子表

            } else { // 修改：用户

                // 删除子表数据
                SignUtil.doSignDeleteSub(CollUtil.newHashSet(dto.getId()), false);

                sysUserDO = new SysUserDO();

                sysUserDO.setId(dto.getId());
                sysUserDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
                sysUserDO.setEmail(MyEntityUtil.getNotNullStr(dto.getEmail()));
                sysUserDO.setSignInName(MyEntityUtil.getNotNullStr(dto.getSignInName()));
                sysUserDO.setWxAppId(MyEntityUtil.getNotNullStr(dto.getWxAppId()));
                sysUserDO.setWxOpenId(MyEntityUtil.getNotNullStr(dto.getWxOpenId()));

                sysUserMapper.updateById(sysUserDO);

                // 新增数据到子表
                insertOrUpdateSub(sysUserDO, dto);

                SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();

                sysUserInfoDO.setId(dto.getId());

                sysUserInfoDO
                    .setNickname(MyEntityUtil.getNotNullStr(dto.getNickname(), NicknameUtil.getRandomNickname()));

                sysUserInfoDO.setBio(MyEntityUtil.getNotNullStr(dto.getBio()));

                sysUserInfoDO.setEnableFlag(sysUserDO.getEnableFlag());

                sysUserInfoMapper.updateById(sysUserInfoDO);

            }

            // 设置
            setManageSignInFlag(sysUserDO.getId(), dto.getManageSignInFlag());

            setSysWxWorkKfAutoAssistantFlag(sysUserDO.getId(), dto.getSysWxWorkKfAutoAssistantFlag());

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 处理密码
     */
    private void insertOrUpdateHandlePassword(SysUserInsertOrUpdateDTO dto) {

        String paramValue = SysParamUtil.getValueByUuid(ParamConstant.RSA_PRIVATE_KEY_UUID, dto.getTenantId()); // 获取非对称
                                                                                                                // 私钥
        dto.setOriginPassword(MyRsaUtil.rsaDecrypt(dto.getOriginPassword(), paramValue));
        dto.setPassword(MyRsaUtil.rsaDecrypt(dto.getPassword(), paramValue));

        if (BooleanUtil.isFalse(ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getOriginPassword()))) {

            ApiResultVO.error(com.cmcorg20230301.be.engine.sign.helper.exception.BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常

        }

    }

    /**
     * 判断：账号是否重复
     */
    private boolean accountIsExist(SysUserInsertOrUpdateDTO dto, Enum<? extends IRedisKey> item,
        Map<Enum<? extends IRedisKey>, String> map, @Nullable Long tenantId) {

        boolean exist = false;

        if (BaseRedisKeyEnum.PRE_EMAIL.equals(item)) {

            exist = SignUtil.accountIsExists(item, dto.getEmail(), dto.getId(), tenantId, null);
            map.put(item, dto.getEmail());

        } else if (BaseRedisKeyEnum.PRE_SIGN_IN_NAME.equals(item)) {

            exist = SignUtil.accountIsExists(item, dto.getSignInName(), dto.getId(), tenantId, null);
            map.put(item, dto.getSignInName());

        } else if (BaseRedisKeyEnum.PRE_PHONE.equals(item)) {

            exist = SignUtil.accountIsExists(item, dto.getPhone(), dto.getId(), tenantId, null);
            map.put(item, dto.getPhone());

        } else if (BaseRedisKeyEnum.PRE_WX_OPEN_ID.equals(item)) {

            exist = SignUtil.accountIsExists(item, dto.getWxOpenId(), dto.getId(), tenantId, dto.getWxAppId());
            map.put(BaseRedisKeyEnum.PRE_WX_APP_ID, dto.getWxAppId());
            map.put(item, dto.getWxOpenId());

        }

        return exist;

    }

    /**
     * 新增/修改：新增数据到子表
     */
    private void insertOrUpdateSub(SysUserDO sysUserDO, SysUserInsertOrUpdateDTO dto) {

        // 如果禁用了，则子表不进行新增操作
        if (BooleanUtil.isFalse(sysUserDO.getEnableFlag())) {

            UserUtil.setDisable(sysUserDO.getId()); // 设置：账号被冻结

            return;

        } else {

            UserUtil.removeDisable(sysUserDO.getId()); // 移除：账号被冻结

        }

        // 新增数据到：角色用户关联表
        if (CollUtil.isNotEmpty(dto.getRoleIdSet())) {

            List<SysRoleRefUserDO> insertList =
                new ArrayList<>(MyMapUtil.getInitialCapacity(dto.getRoleIdSet().size()));

            for (Long item : dto.getRoleIdSet()) {

                SysRoleRefUserDO sysRoleRefUserDO = new SysRoleRefUserDO();

                sysRoleRefUserDO.setRoleId(item);
                sysRoleRefUserDO.setUserId(sysUserDO.getId());

                insertList.add(sysRoleRefUserDO);

            }

            sysRoleRefUserService.saveBatch(insertList);

        }

        // 新增数据到：部门用户关联表
        if (CollUtil.isNotEmpty(dto.getDeptIdSet())) {

            List<SysDeptRefUserDO> insertList =
                new ArrayList<>(MyMapUtil.getInitialCapacity(dto.getDeptIdSet().size()));

            for (Long item : dto.getDeptIdSet()) {

                SysDeptRefUserDO sysDeptRefUserDO = new SysDeptRefUserDO();

                sysDeptRefUserDO.setDeptId(item);
                sysDeptRefUserDO.setUserId(sysUserDO.getId());

                insertList.add(sysDeptRefUserDO);

            }

            sysDeptRefUserService.saveBatch(insertList);

        }

        // 新增数据到：岗位用户关联表
        if (CollUtil.isNotEmpty(dto.getPostIdSet())) {

            List<SysPostRefUserDO> insertList =
                new ArrayList<>(MyMapUtil.getInitialCapacity(dto.getPostIdSet().size()));

            for (Long item : dto.getPostIdSet()) {

                SysPostRefUserDO sysPostRefUserDO = new SysPostRefUserDO();

                sysPostRefUserDO.setPostId(item);
                sysPostRefUserDO.setUserId(sysUserDO.getId());

                insertList.add(sysPostRefUserDO);

            }

            sysPostRefUserService.saveBatch(insertList);

        }

        // 新增数据到：租户用户关联表
        if (CollUtil.isNotEmpty(dto.getTenantIdSet())) {

            List<SysTenantRefUserDO> insertList =
                new ArrayList<>(MyMapUtil.getInitialCapacity(dto.getTenantIdSet().size()));

            for (Long item : dto.getTenantIdSet()) {

                SysTenantRefUserDO sysTenantRefUserDO = new SysTenantRefUserDO();

                sysTenantRefUserDO.setTenantId(item);
                sysTenantRefUserDO.setUserId(sysUserDO.getId());

                insertList.add(sysTenantRefUserDO);

            }

            sysTenantRefUserService.saveBatch(insertList);

        }

    }

    /**
     * 通过主键id，查看详情
     */
    @SneakyThrows
    @Override
    public SysUserInfoByIdVO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        SysUserDO sysUserDO = lambdaQuery().eq(BaseEntity::getId, notNullId.getId())
            .in(BaseEntityNoId::getTenantId, queryTenantIdSet).one();

        if (sysUserDO == null) {
            return null;
        }

        SysUserInfoByIdVO sysUserInfoByIdVO = BeanUtil.copyProperties(sysUserDO, SysUserInfoByIdVO.class);

        CountDownLatch countDownLatch = ThreadUtil.newCountDownLatch(5);

        MyThreadUtil.execute(() -> {

            SysUserInfoDO sysUserInfoDO =
                ChainWrappers.lambdaQueryChain(sysUserInfoMapper).eq(SysUserInfoDO::getId, notNullId.getId())
                    .select(SysUserInfoDO::getNickname, SysUserInfoDO::getAvatarFileId, SysUserInfoDO::getBio).one();

            sysUserInfoByIdVO.setNickname(sysUserInfoDO.getNickname());
            sysUserInfoByIdVO.setAvatarFileId(sysUserInfoDO.getAvatarFileId());
            sysUserInfoByIdVO.setBio(sysUserInfoDO.getBio());

            // 获取
            Boolean manageSignInFlag = getManageSignInFlag(sysUserDO.getId(), sysUserDO.getTenantId());

            sysUserInfoByIdVO.setManageSignInFlag(manageSignInFlag);

            Boolean sysWxWorkKfAutoAssistantFlag = getSysWxWorkKfAutoAssistantFlag(sysUserDO.getId());

            sysUserInfoByIdVO.setSysWxWorkKfAutoAssistantFlag(sysWxWorkKfAutoAssistantFlag);

        }, countDownLatch);

        MyThreadUtil.execute(() -> {

            // 获取：用户绑定的角色 idSet
            List<SysRoleRefUserDO> refUserDOList = sysRoleRefUserService.lambdaQuery()
                .eq(SysRoleRefUserDO::getUserId, notNullId.getId()).select(SysRoleRefUserDO::getRoleId).list();

            Set<Long> roleIdSet = refUserDOList.stream().map(SysRoleRefUserDO::getRoleId).collect(Collectors.toSet());

            sysUserInfoByIdVO.setRoleIdSet(roleIdSet);

        }, countDownLatch);

        MyThreadUtil.execute(() -> {

            // 获取：用户绑定的部门 idSet
            List<SysDeptRefUserDO> deptRefUserDOList = sysDeptRefUserService.lambdaQuery()
                .eq(SysDeptRefUserDO::getUserId, notNullId.getId()).select(SysDeptRefUserDO::getDeptId).list();

            Set<Long> deptIdSet =
                deptRefUserDOList.stream().map(SysDeptRefUserDO::getDeptId).collect(Collectors.toSet());

            sysUserInfoByIdVO.setDeptIdSet(deptIdSet);

        }, countDownLatch);

        MyThreadUtil.execute(() -> {

            // 获取：用户绑定的岗位 idSet
            List<SysPostRefUserDO> jobRefUserDOList = sysPostRefUserService.lambdaQuery()
                .eq(SysPostRefUserDO::getUserId, notNullId.getId()).select(SysPostRefUserDO::getPostId).list();

            Set<Long> postIdSet =
                jobRefUserDOList.stream().map(SysPostRefUserDO::getPostId).collect(Collectors.toSet());

            sysUserInfoByIdVO.setPostIdSet(postIdSet);

        }, countDownLatch);

        MyThreadUtil.execute(() -> {

            // 获取：用户绑定的租户 idSet
            List<SysTenantRefUserDO> tenantRefUserDOList = sysTenantRefUserService.lambdaQuery()
                .eq(SysTenantRefUserDO::getUserId, notNullId.getId()).select(SysTenantRefUserDO::getTenantId).list();

            Set<Long> tenantIdSet =
                tenantRefUserDOList.stream().map(SysTenantRefUserDO::getTenantId).collect(Collectors.toSet());

            sysUserInfoByIdVO.setTenantIdSet(tenantIdSet);

        }, countDownLatch);

        countDownLatch.await();

        return sysUserInfoByIdVO;

    }

    /**
     * 是否允许后台登录
     */
    @Override
    @NotNull
    public Boolean manageSignInFlag() {

        Long currentUserId = UserUtil.getCurrentUserId();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        // 执行
        return getManageSignInFlag(currentUserId, currentTenantIdDefault);

    }

    /**
     * 获取：是否允许后台登录
     */
    public static boolean getManageSignInFlag(Long currentUserId, @Nullable Long currentTenantIdDefault) {

        if (UserUtil.getCurrentUserAdminFlag(currentUserId)) {
            return true;
        }

        Boolean manageSignInFlag = MyCacheUtil.onlyGetSecondMap(BaseRedisKeyEnum.SYS_USER_MANAGE_SIGN_IN_FLAG_CACHE,
            null, String.valueOf(currentUserId));

        if (manageSignInFlag == null) {

            String defaultManageSignInFlagStr =
                SysParamUtil.getValueByUuid(ParamConstant.DEFAULT_MANAGE_SIGN_IN_FLAG, currentTenantIdDefault);

            return Convert.toBool(defaultManageSignInFlagStr, false);

        }

        return manageSignInFlag;

    }

    /**
     * 设置：是否允许后台登录
     */
    public static void setManageSignInFlag(Long userId, @Nullable Boolean manageSignInFlag) {

        // 设置
        CacheRedisKafkaLocalUtil.putSecondMap(BaseRedisKeyEnum.SYS_USER_MANAGE_SIGN_IN_FLAG_CACHE, null,
            String.valueOf(userId), BooleanUtil.isTrue(manageSignInFlag), null);

    }

    /**
     * 获取：企业微信-微信客服，是否自动交给智能助手接待，默认：true
     */
    public static boolean getSysWxWorkKfAutoAssistantFlag(Long currentUserId) {

        if (UserUtil.getCurrentUserAdminFlag(currentUserId)) {
            return true;
        }

        Boolean sysWxWorkKfAutoAssistantFlag = MyCacheUtil.onlyGetSecondMap(
            BaseRedisKeyEnum.SYS_WX_WORK_KF_AUTO_ASSISTANT_FLAG_CACHE, null, String.valueOf(currentUserId));

        return !BooleanUtil.isFalse(sysWxWorkKfAutoAssistantFlag);

    }

    /**
     * 设置：企业微信-微信客服，是否自动交给智能助手接待，默认：true
     */
    public static void setSysWxWorkKfAutoAssistantFlag(Long userId, @Nullable Boolean sysWxWorkKfAutoAssistantFlag) {

        // 设置
        CacheRedisKafkaLocalUtil.putSecondMap(BaseRedisKeyEnum.SYS_WX_WORK_KF_AUTO_ASSISTANT_FLAG_CACHE, null,
            String.valueOf(userId), !BooleanUtil.isFalse(sysWxWorkKfAutoAssistantFlag), null);

    }

    /**
     * 批量注销用户
     */
    @Override
    @DSTransactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        if (CollUtil.isEmpty(notEmptyIdSet.getIdSet())) {
            return BaseBizCodeEnum.OK;
        }

        // 执行：账号注销
        SignUtil.doSignDelete(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.OK;

    }

    /**
     * 刷新用户 jwt私钥后缀
     */
    @Override
    public String refreshJwtSecretSuf(NotEmptyIdSet notEmptyIdSet) {

        if (CollUtil.isEmpty(notEmptyIdSet.getIdSet())) {
            return BaseBizCodeEnum.OK;
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(notEmptyIdSet.getIdSet(), getCheckIllegalFunc1(notEmptyIdSet.getIdSet()));

        for (Long item : notEmptyIdSet.getIdSet()) {

            UserUtil.setJwtSecretSuf(item); // 设置：jwt秘钥后缀

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 批量重置头像
     */
    @Override
    public String resetAvatar(NotEmptyIdSet notEmptyIdSet) {

        if (CollUtil.isEmpty(notEmptyIdSet.getIdSet())) {
            return BaseBizCodeEnum.OK;
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(notEmptyIdSet.getIdSet(), getCheckIllegalFunc1(notEmptyIdSet.getIdSet()));

        ChainWrappers.lambdaUpdateChain(sysUserInfoMapper).in(SysUserInfoDO::getId, notEmptyIdSet.getIdSet())
            .set(SysUserInfoDO::getAvatarFileId, -1).update();

        return BaseBizCodeEnum.OK;

    }

    /**
     * 批量修改密码
     */
    @Override
    @DSTransactional
    public String updatePassword(SysUserUpdatePasswordDTO dto) {

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(dto.getIdSet(), getCheckIllegalFunc1(dto.getIdSet()));

        boolean passwordFlag =
            StrUtil.isNotBlank(dto.getNewPassword()) && StrUtil.isNotBlank(dto.getNewOriginPassword());

        String password = "";

        if (passwordFlag) {

            String paramValue = SysParamUtil.getValueByUuid(ParamConstant.RSA_PRIVATE_KEY_UUID, null); // 获取非对称 私钥
            dto.setNewOriginPassword(MyRsaUtil.rsaDecrypt(dto.getNewOriginPassword(), paramValue));
            dto.setNewPassword(MyRsaUtil.rsaDecrypt(dto.getNewPassword(), paramValue));

            if (BooleanUtil.isFalse(ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getNewOriginPassword()))) {

                ApiResultVO.error(com.cmcorg20230301.be.engine.sign.helper.exception.BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常

            }

            password = PasswordConvertUtil.convert(dto.getNewPassword(), true);

        }

        lambdaUpdate().in(BaseEntity::getId, dto.getIdSet()).set(SysUserDO::getPassword, password).update();

        refreshJwtSecretSuf(new NotEmptyIdSet(dto.getIdSet())); // 刷新：jwt私钥后缀

        return BaseBizCodeEnum.OK;

    }

    /**
     * 批量：解冻
     */
    @DSTransactional
    @Override
    public String thaw(NotEmptyIdSet notEmptyIdSet) {

        if (CollUtil.isEmpty(notEmptyIdSet.getIdSet())) {
            return BaseBizCodeEnum.OK;
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(notEmptyIdSet.getIdSet(), getCheckIllegalFunc1(notEmptyIdSet.getIdSet()));

        lambdaUpdate().in(BaseEntity::getId, notEmptyIdSet.getIdSet()).set(SysUserDO::getEnableFlag, true).update();

        ChainWrappers.lambdaUpdateChain(sysUserInfoMapper).in(SysUserInfoDO::getId, notEmptyIdSet.getIdSet())
            .set(SysUserInfoDO::getEnableFlag, true).update();

        return BaseBizCodeEnum.OK;

    }

    /**
     * 批量：冻结
     */
    @DSTransactional
    @Override
    public String freeze(NotEmptyIdSet notEmptyIdSet) {

        if (CollUtil.isEmpty(notEmptyIdSet.getIdSet())) {
            return BaseBizCodeEnum.OK;
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(notEmptyIdSet.getIdSet(), getCheckIllegalFunc1(notEmptyIdSet.getIdSet()));

        lambdaUpdate().in(BaseEntity::getId, notEmptyIdSet.getIdSet()).set(SysUserDO::getEnableFlag, false).update();

        ChainWrappers.lambdaUpdateChain(sysUserInfoMapper).in(SysUserInfoDO::getId, notEmptyIdSet.getIdSet())
            .set(SysUserInfoDO::getEnableFlag, false).update();

        return BaseBizCodeEnum.OK;

    }

    /**
     * 获取：检查：是否非法操作的 getCheckIllegalFunc1
     */
    @NotNull
    private Func1<Set<Long>, Long> getCheckIllegalFunc1(Set<Long> idSet) {

        return tenantIdSet -> lambdaQuery().in(BaseEntity::getId, idSet).in(BaseEntityNoId::getTenantId, tenantIdSet)
            .count();

    }

    /**
     * 获取：检查：是否非法操作的 getTenantIdBaseEntityFunc1
     */
    @NotNull
    private Func1<Long, BaseEntity> getTenantIdBaseEntityFunc1() {

        return id -> lambdaQuery().eq(BaseEntity::getId, id).select(BaseEntity::getTenantId).one();

    }

}
