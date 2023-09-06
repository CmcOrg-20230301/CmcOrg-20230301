package com.cmcorg20230301.be.engine.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.cache.util.CacheHelper;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.dept.model.entity.SysDeptRefUserDO;
import com.cmcorg20230301.be.engine.dept.service.SysDeptRefUserService;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.BaseRegexConstant;
import com.cmcorg20230301.be.engine.model.model.constant.ParamConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.interfaces.IRedisKey;
import com.cmcorg20230301.be.engine.model.model.vo.DictVO;
import com.cmcorg20230301.be.engine.mysql.model.annotation.MyTransactional;
import com.cmcorg20230301.be.engine.post.model.entity.SysPostRefUserDO;
import com.cmcorg20230301.be.engine.post.service.SysPostRefUserService;
import com.cmcorg20230301.be.engine.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.role.service.SysRoleRefUserService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.BaseSysRequestMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.dto.MyPageDTO;
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
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    BaseSysRequestMapper baseSysRequestMapper;

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserPageVO> myPage(SysUserPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, false);

        // 过滤：request表的数据
        dto.setIdSet(new HashSet<>()); // 先清除：idSet的数据

        if (dto.getBeginLastActiveTime() != null || dto.getEndLastActiveTime() != null) {

            List<SysRequestDO> sysRequestDOList = ChainWrappers.lambdaQueryChain(baseSysRequestMapper)
                .le(dto.getEndLastActiveTime() != null, BaseEntityNoId::getCreateTime, dto.getEndLastActiveTime())
                .ge(dto.getBeginLastActiveTime() != null, BaseEntityNoId::getCreateTime, dto.getBeginLastActiveTime())
                .select(BaseEntityNoId::getCreateId).groupBy(BaseEntityNoId::getCreateId).list();

            Set<Long> idSet = sysRequestDOList.stream().map(BaseEntityNoId::getCreateId).collect(Collectors.toSet());

            dto.setIdSet(idSet);

        }

        Page<SysUserPageVO> dtoPage = dto.page();

        if (dto.orderEmpty()) {

            dtoPage.orders().add(MyPageDTO.createTimeOrderItem()); // 如果不存在排序，则默认根据：创建时间排序

        } else {

            if ("createTime".equals(dto.getOrder().getName())) {

                // 添加 orderList里面的排序规则
                dtoPage.orders().add(MyPageDTO.orderToOrderItem(dto.getOrder(), false));

            }

        }

        Page<SysUserPageVO> page = baseMapper.myPage(dtoPage, dto);

        Set<Long> userIdSet = new HashSet<>(MyMapUtil.getInitialCapacity(page.getRecords().size()));

        for (SysUserPageVO item : page.getRecords()) {

            item.setEmail(DesensitizedUtil.email(item.getEmail())); // 脱敏
            item.setSignInName(DesensitizedUtil.chineseName(item.getSignInName())); // 脱敏
            item.setPhone(DesensitizedUtil.mobilePhone(item.getPhone())); // 脱敏

            userIdSet.add(item.getId());

        }

        if (userIdSet.size() != 0) {

            List<SysDeptRefUserDO> sysDeptRefUserDOList =
                sysDeptRefUserService.lambdaQuery().in(SysDeptRefUserDO::getUserId, userIdSet)
                    .select(SysDeptRefUserDO::getUserId, SysDeptRefUserDO::getDeptId).list();

            List<SysPostRefUserDO> sysPostRefUserDOList =
                sysPostRefUserService.lambdaQuery().in(SysPostRefUserDO::getUserId, userIdSet)
                    .select(SysPostRefUserDO::getUserId, SysPostRefUserDO::getPostId).list();

            // 备注：mysql 是先 group by 再 order by
            List<SysRequestDO> sysRequestDOList =
                ChainWrappers.queryChain(baseSysRequestMapper).select(" create_id, MAX( create_time ) AS createTime")
                    .in("create_id", userIdSet).groupBy("create_id").list();

            Map<Long, Set<Long>> deptUserGroupMap = sysDeptRefUserDOList.stream().collect(Collectors
                .groupingBy(SysDeptRefUserDO::getUserId,
                    Collectors.mapping(SysDeptRefUserDO::getDeptId, Collectors.toSet())));

            Map<Long, Set<Long>> postUserGroupMap = sysPostRefUserDOList.stream().collect(Collectors
                .groupingBy(SysPostRefUserDO::getUserId,
                    Collectors.mapping(SysPostRefUserDO::getPostId, Collectors.toSet())));

            Map<Long, Date> requestCreateIdAndCreateTimeMap = sysRequestDOList.stream()
                .collect(Collectors.toMap(BaseEntityNoId::getCreateId, BaseEntityNoId::getCreateTime));

            page.getRecords().forEach(it -> {

                it.setRoleIdSet(UserUtil.getUserRefRoleIdSetMap().get(it.getId()));

                it.setDeptIdSet(deptUserGroupMap.get(it.getId()));

                it.setPostIdSet(postUserGroupMap.get(it.getId()));

                it.setTenantIdSet(SysTenantUtil.getUserIdRefTenantIdSetMap().get(it.getId()));

                it.setLastActiveTime(requestCreateIdAndCreateTimeMap.getOrDefault(it.getId(), it.getCreateTime()));

            });

        }

        // 排序
        if (dto.orderEmpty() == false) {

            if ("lastActiveTime".equals(dto.getOrder().getName())) {

                List<SysUserPageVO> sysUserPageVOList;

                if ("descend".equals(dto.getOrder().getValue())) { // 降序

                    sysUserPageVOList = page.getRecords().stream()
                        .sorted(Comparator.comparing(SysUserPageVO::getLastActiveTime, Comparator.reverseOrder()))
                        .collect(Collectors.toList());

                } else { // 升序

                    sysUserPageVOList =
                        page.getRecords().stream().sorted(Comparator.comparing(SysUserPageVO::getLastActiveTime))
                            .collect(Collectors.toList());

                }

                page.setRecords(sysUserPageVOList);

            }

        }

        return page;

    }

    /**
     * 下拉列表
     */
    @Override
    public Page<DictVO> dictList(SysUserDictListDTO dto) {

        // 获取：用户关联的租户
        Set<Long> tenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        // 获取所有：用户信息
        List<SysUserInfoDO> sysUserInfoDOList =
            MyCacheUtil.getCollection(RedisKeyEnum.SYS_USER_INFO_CACHE, CacheHelper.getDefaultList(), () -> {

                return ChainWrappers.lambdaQueryChain(sysUserInfoMapper)
                    .select(SysUserInfoDO::getId, SysUserInfoDO::getNickname, SysUserInfoDO::getTenantId)
                    .orderByDesc(SysUserInfoDO::getId).list();

            });

        List<DictVO> dictListVOList = sysUserInfoDOList.stream().filter(it -> tenantIdSet.contains(it.getTenantId()))
            .map(it -> new DictVO(it.getId(), it.getNickname())).collect(Collectors.toList());

        // 增加 admin账号
        if (BooleanUtil.isTrue(dto.getAddAdminFlag())) {

            dictListVOList.add(new DictVO(BaseConstant.ADMIN_ID, securityProperties.getAdminNickname()));

        }

        return new Page<DictVO>().setTotal(dictListVOList.size()).setRecords(dictListVOList);

    }

    /**
     * 新增/修改
     */
    @Override
    @MyTransactional
    public String insertOrUpdate(SysUserInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        boolean emailBlank = StrUtil.isBlank(dto.getEmail());
        boolean signInNameBlank = StrUtil.isBlank(dto.getSignInName());
        boolean phoneBlank = StrUtil.isBlank(dto.getPhone());

        if (emailBlank && signInNameBlank && phoneBlank) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_CANNOT_BE_EMPTY);
        }

        boolean passwordFlag = StrUtil.isNotBlank(dto.getPassword()) && StrUtil.isNotBlank(dto.getOriginPassword());

        if (dto.getId() == null && passwordFlag) { // 只有新增时，才可以设置密码

            // 处理密码
            insertOrUpdateHandlePassword(dto);

        }

        Set<Enum<? extends IRedisKey>> redisKeyEnumSet = CollUtil.newHashSet();

        if (!emailBlank) {
            redisKeyEnumSet.add(RedisKeyEnum.PRE_EMAIL);
        }
        if (!signInNameBlank) {
            redisKeyEnumSet.add(RedisKeyEnum.PRE_SIGN_IN_NAME);
        }
        if (!phoneBlank) {
            redisKeyEnumSet.add(RedisKeyEnum.PRE_PHONE);
        }

        return RedissonUtil.doMultiLock(null, redisKeyEnumSet, () -> {

            Map<Enum<? extends IRedisKey>, String> accountMap = MapUtil.newHashMap();

            // 检查：账号是否存在
            for (Enum<? extends IRedisKey> item : redisKeyEnumSet) {

                if (accountIsExist(dto, item, accountMap, dto.getTenantId())) {

                    SignUtil.accountIsExistError();

                }

            }

            if (dto.getId() == null) { // 新增：用户

                SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();
                sysUserInfoDO.setNickname(dto.getNickname());
                sysUserInfoDO.setBio(dto.getBio());

                SysUserDO sysUserDO = SignUtil
                    .insertUser(dto.getPassword(), accountMap, false, sysUserInfoDO, dto.getEnableFlag(),
                        dto.getTenantId());

                insertOrUpdateSub(sysUserDO, dto); // 新增数据到子表

            } else { // 修改：用户

                // 删除子表数据
                SignUtil.doSignDeleteSub(CollUtil.newHashSet(dto.getId()), false);

                SysUserDO sysUserDO = new SysUserDO();
                sysUserDO.setId(dto.getId());
                sysUserDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
                sysUserDO.setEmail(MyEntityUtil.getNotNullStr(dto.getEmail()));
                sysUserDO.setSignInName(MyEntityUtil.getNotNullStr(dto.getSignInName()));
                sysUserMapper.updateById(sysUserDO);

                // 新增数据到子表
                insertOrUpdateSub(sysUserDO, dto);

                SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();
                sysUserInfoDO.setId(dto.getId());
                sysUserInfoDO
                    .setNickname(MyEntityUtil.getNotNullStr(dto.getNickname(), NicknameUtil.getRandomNickname()));
                sysUserInfoDO.setBio(MyEntityUtil.getNotNullStr(dto.getBio()));

                sysUserInfoMapper.updateById(sysUserInfoDO);

            }

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 处理密码
     */
    private void insertOrUpdateHandlePassword(SysUserInsertOrUpdateDTO dto) {

        String paramValue = SysParamUtil.getValueByUuid(ParamConstant.RSA_PRIVATE_KEY_UUID); // 获取非对称 私钥
        dto.setOriginPassword(MyRsaUtil.rsaDecrypt(dto.getOriginPassword(), paramValue));
        dto.setPassword(MyRsaUtil.rsaDecrypt(dto.getPassword(), paramValue));

        if (BooleanUtil.isFalse(ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getOriginPassword()))) {

            ApiResultVO.error(
                com.cmcorg20230301.be.engine.sign.helper.exception.BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常

        }

    }

    /**
     * 判断：账号是否重复
     */
    private boolean accountIsExist(SysUserInsertOrUpdateDTO dto, Enum<? extends IRedisKey> item,
        Map<Enum<? extends IRedisKey>, String> map, @Nullable Long tenantId) {

        boolean exist = false;

        if (RedisKeyEnum.PRE_EMAIL.equals(item)) {

            exist = SignUtil.accountIsExists(item, dto.getEmail(), dto.getId(), tenantId);
            map.put(item, dto.getEmail());

        } else if (RedisKeyEnum.PRE_SIGN_IN_NAME.equals(item)) {

            exist = SignUtil.accountIsExists(item, dto.getSignInName(), dto.getId(), tenantId);
            map.put(item, dto.getSignInName());

        } else if (RedisKeyEnum.PRE_PHONE.equals(item)) {

            exist = SignUtil.accountIsExists(item, dto.getPhone(), dto.getId(), tenantId);
            map.put(item, dto.getPhone());

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
     * 批量注销用户
     */
    @Override
    @MyTransactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        if (CollUtil.isEmpty(notEmptyIdSet.getIdSet())) {
            return BaseBizCodeEnum.OK;
        }

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
     * 通过主键id，查看详情
     */
    @Override
    public SysUserInfoByIdVO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        SysUserDO sysUserDO =
            lambdaQuery().eq(BaseEntity::getId, notNullId.getId()).in(BaseEntityNoId::getTenantId, queryTenantIdSet)
                .one();

        if (sysUserDO == null) {
            return null;
        }

        SysUserInfoByIdVO sysUserInfoByIdVO = BeanUtil.copyProperties(sysUserDO, SysUserInfoByIdVO.class);

        SysUserInfoDO sysUserInfoDO =
            ChainWrappers.lambdaQueryChain(sysUserInfoMapper).eq(SysUserInfoDO::getId, notNullId.getId())
                .select(SysUserInfoDO::getNickname, SysUserInfoDO::getAvatarFileId, SysUserInfoDO::getBio).one();

        sysUserInfoByIdVO.setNickname(sysUserInfoDO.getNickname());
        sysUserInfoByIdVO.setAvatarFileId(sysUserInfoDO.getAvatarFileId());
        sysUserInfoByIdVO.setBio(sysUserInfoDO.getBio());

        // 获取：用户绑定的角色 idSet
        List<SysRoleRefUserDO> refUserDOList =
            sysRoleRefUserService.lambdaQuery().eq(SysRoleRefUserDO::getUserId, notNullId.getId())
                .select(SysRoleRefUserDO::getRoleId).list();

        Set<Long> roleIdSet = refUserDOList.stream().map(SysRoleRefUserDO::getRoleId).collect(Collectors.toSet());

        // 获取：用户绑定的部门 idSet
        List<SysDeptRefUserDO> deptRefUserDOList =
            sysDeptRefUserService.lambdaQuery().eq(SysDeptRefUserDO::getUserId, notNullId.getId())
                .select(SysDeptRefUserDO::getDeptId).list();

        Set<Long> deptIdSet = deptRefUserDOList.stream().map(SysDeptRefUserDO::getDeptId).collect(Collectors.toSet());

        // 获取：用户绑定的岗位 idSet
        List<SysPostRefUserDO> jobRefUserDOList =
            sysPostRefUserService.lambdaQuery().eq(SysPostRefUserDO::getUserId, notNullId.getId())
                .select(SysPostRefUserDO::getPostId).list();

        Set<Long> postIdSet = jobRefUserDOList.stream().map(SysPostRefUserDO::getPostId).collect(Collectors.toSet());

        // 获取：用户绑定的租户 idSet
        List<SysTenantRefUserDO> tenantRefUserDOList =
            sysTenantRefUserService.lambdaQuery().eq(SysTenantRefUserDO::getUserId, notNullId.getId())
                .select(SysTenantRefUserDO::getTenantId).list();

        Set<Long> tenantIdSet =
            tenantRefUserDOList.stream().map(SysTenantRefUserDO::getTenantId).collect(Collectors.toSet());

        sysUserInfoByIdVO.setRoleIdSet(roleIdSet);
        sysUserInfoByIdVO.setDeptIdSet(deptIdSet);
        sysUserInfoByIdVO.setPostIdSet(postIdSet);
        sysUserInfoByIdVO.setTenantIdSet(tenantIdSet);

        return sysUserInfoByIdVO;

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
    @MyTransactional
    public String updatePassword(SysUserUpdatePasswordDTO dto) {

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(dto.getIdSet(), getCheckIllegalFunc1(dto.getIdSet()));

        boolean passwordFlag =
            StrUtil.isNotBlank(dto.getNewPassword()) && StrUtil.isNotBlank(dto.getNewOriginPassword());

        String password = "";

        if (passwordFlag) {

            String paramValue = SysParamUtil.getValueByUuid(ParamConstant.RSA_PRIVATE_KEY_UUID); // 获取非对称 私钥
            dto.setNewOriginPassword(MyRsaUtil.rsaDecrypt(dto.getNewOriginPassword(), paramValue));
            dto.setNewPassword(MyRsaUtil.rsaDecrypt(dto.getNewPassword(), paramValue));

            if (BooleanUtil.isFalse(ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getNewOriginPassword()))) {

                ApiResultVO.error(
                    com.cmcorg20230301.be.engine.sign.helper.exception.BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常

            }

            password = PasswordConvertUtil.convert(dto.getNewPassword(), true);

        }

        lambdaUpdate().in(BaseEntity::getId, dto.getIdSet()).set(SysUserDO::getPassword, password).update();

        refreshJwtSecretSuf(new NotEmptyIdSet(dto.getIdSet())); // 刷新：jwt私钥后缀

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
