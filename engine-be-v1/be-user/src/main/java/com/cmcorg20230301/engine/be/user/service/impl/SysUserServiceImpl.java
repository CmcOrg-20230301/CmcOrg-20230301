package com.cmcorg20230301.engine.be.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.engine.be.dept.model.entity.SysDeptRefUserDO;
import com.cmcorg20230301.engine.be.dept.service.SysDeptRefUserService;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.model.model.constant.BaseRegexConstant;
import com.cmcorg20230301.engine.be.model.model.constant.ParamConstant;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.model.model.interfaces.IRedisKey;
import com.cmcorg20230301.engine.be.model.model.vo.DictVO;
import com.cmcorg20230301.engine.be.mysql.model.annotation.MyTransactional;
import com.cmcorg20230301.engine.be.post.model.entity.SysPostRefUserDO;
import com.cmcorg20230301.engine.be.post.service.SysPostRefUserService;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.redisson.util.RedissonUtil;
import com.cmcorg20230301.engine.be.role.service.SysRoleRefUserService;
import com.cmcorg20230301.engine.be.role.service.SysRoleService;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.engine.be.security.mapper.SysUserMapper;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.entity.SysRoleRefUserDO;
import com.cmcorg20230301.engine.be.security.model.entity.SysUserDO;
import com.cmcorg20230301.engine.be.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.properties.SecurityProperties;
import com.cmcorg20230301.engine.be.security.util.*;
import com.cmcorg20230301.engine.be.sign.helper.util.SignUtil;
import com.cmcorg20230301.engine.be.user.exception.BizCodeEnum;
import com.cmcorg20230301.engine.be.user.mapper.SysUserProMapper;
import com.cmcorg20230301.engine.be.user.model.dto.SysUserDictListDTO;
import com.cmcorg20230301.engine.be.user.model.dto.SysUserInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.user.model.dto.SysUserPageDTO;
import com.cmcorg20230301.engine.be.user.model.dto.SysUserUpdatePasswordDTO;
import com.cmcorg20230301.engine.be.user.model.vo.SysUserInfoByIdVO;
import com.cmcorg20230301.engine.be.user.model.vo.SysUserPageVO;
import com.cmcorg20230301.engine.be.user.service.SysUserService;
import com.cmcorg20230301.engine.be.util.util.MyMapUtil;
import com.cmcorg20230301.engine.be.util.util.NicknameUtil;
import org.springframework.stereotype.Service;

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
    SysRoleService sysRoleService;

    @Resource
    SysDeptRefUserService sysDeptRefUserService;

    @Resource
    SysPostRefUserService sysPostRefUserService;

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserPageVO> myPage(SysUserPageDTO dto) {

        Page<SysUserPageVO> page = baseMapper.myPage(dto.createTimeDescDefaultOrderPage(), dto);

        Set<Long> userIdSet = new HashSet<>(MyMapUtil.getInitialCapacity(page.getRecords().size()));

        for (SysUserPageVO item : page.getRecords()) {

            item.setEmail(DesensitizedUtil.email(item.getEmail())); // 脱敏
            item.setSignInName(DesensitizedUtil.chineseName(item.getSignInName())); // 脱敏
            item.setPhone(DesensitizedUtil.mobilePhone(item.getPhone())); // 脱敏

            userIdSet.add(item.getId());

        }

        if (userIdSet.size() != 0) {

            // 组装：roleIdSet
            List<SysRoleRefUserDO> sysRoleRefUserDOList =
                sysRoleRefUserService.lambdaQuery().in(SysRoleRefUserDO::getUserId, userIdSet)
                    .select(SysRoleRefUserDO::getUserId, SysRoleRefUserDO::getRoleId).list();

            Map<Long, Set<Long>> roleUserGroupMap = sysRoleRefUserDOList.stream().collect(Collectors
                .groupingBy(SysRoleRefUserDO::getUserId,
                    Collectors.mapping(SysRoleRefUserDO::getRoleId, Collectors.toSet())));

            page.getRecords().forEach(it -> it.setRoleIdSet(roleUserGroupMap.get(it.getId())));

        }

        return page;

    }

    /**
     * 下拉列表
     */
    @Override
    public Page<DictVO> dictList(SysUserDictListDTO dto) {

        List<SysUserInfoDO> sysUserInfoDOList =
            ChainWrappers.lambdaQueryChain(sysUserInfoMapper).select(SysUserInfoDO::getId, SysUserInfoDO::getNickname)
                .list();

        List<DictVO> dictListVOList =
            sysUserInfoDOList.stream().map(it -> new DictVO(it.getId(), it.getNickname())).collect(Collectors.toList());

        // 增加 admin账号
        if (BooleanUtil.isTrue(dto.getAddAdminFlag())) {

            dictListVOList.add(new DictVO(BaseConstant.ADMIN_ID, securityProperties.getAdminNickname()));

        }

        return new Page<DictVO>().setTotal(sysUserInfoDOList.size()).setRecords(dictListVOList);

    }

    /**
     * 新增/修改
     */
    @Override
    @MyTransactional
    public String insertOrUpdate(SysUserInsertOrUpdateDTO dto) {

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

                if (accountIsExist(dto, item, accountMap)) {

                    SignUtil.accountIsExistError();

                }

            }

            if (dto.getId() == null) { // 新增：用户

                SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();
                sysUserInfoDO.setNickname(dto.getNickname());
                sysUserInfoDO.setBio(dto.getBio());

                SysUserDO sysUserDO =
                    SignUtil.insertUser(dto.getPassword(), accountMap, false, sysUserInfoDO, dto.getEnableFlag());

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

        String paramValue = SysParamUtil.getValueById(ParamConstant.RSA_PRIVATE_KEY_ID); // 获取非对称 私钥
        dto.setOriginPassword(MyRsaUtil.rsaDecrypt(dto.getOriginPassword(), paramValue));
        dto.setPassword(MyRsaUtil.rsaDecrypt(dto.getPassword(), paramValue));

        if (BooleanUtil.isFalse(ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getOriginPassword()))) {

            ApiResultVO.error(
                com.cmcorg20230301.engine.be.sign.helper.exception.BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常

        }

    }

    /**
     * 判断：账号是否重复
     */
    private boolean accountIsExist(SysUserInsertOrUpdateDTO dto, Enum<? extends IRedisKey> item,
        Map<Enum<? extends IRedisKey>, String> map) {

        boolean exist = false;

        if (RedisKeyEnum.PRE_EMAIL.equals(item)) {

            exist = SignUtil.accountIsExists(item, dto.getEmail(), dto.getId());
            map.put(item, dto.getEmail());

        } else if (RedisKeyEnum.PRE_SIGN_IN_NAME.equals(item)) {

            exist = SignUtil.accountIsExists(item, dto.getSignInName(), dto.getId());
            map.put(item, dto.getSignInName());

        } else if (RedisKeyEnum.PRE_PHONE.equals(item)) {

            exist = SignUtil.accountIsExists(item, dto.getPhone(), dto.getId());
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
            return;
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

    }

    /**
     * 批量注销用户
     */
    @Override
    @MyTransactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        SignUtil.doSignDelete(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.OK;

    }

    /**
     * 刷新用户 jwt私钥后缀
     */
    @Override
    public String refreshJwtSecretSuf(NotEmptyIdSet notEmptyIdSet) {

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

        SysUserDO sysUserDO = lambdaQuery()
            .select(SysUserDO::getSignInName, SysUserDO::getEmail, SysUserDO::getEnableFlag, SysUserDO::getPhone,
                BaseEntity::getId, BaseEntity::getUpdateTime, BaseEntity::getCreateTime)
            .eq(BaseEntity::getId, notNullId.getId()).one();

        SysUserInfoByIdVO sysUserInfoByIdVO = BeanUtil.copyProperties(sysUserDO, SysUserInfoByIdVO.class);

        if (sysUserInfoByIdVO == null) {
            return null;
        }

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

        sysUserInfoByIdVO.setRoleIdSet(roleIdSet);
        sysUserInfoByIdVO.setDeptIdSet(deptIdSet);
        sysUserInfoByIdVO.setPostIdSet(postIdSet);

        return sysUserInfoByIdVO;

    }

    /**
     * 批量重置头像
     */
    @Override
    public String resetAvatar(NotEmptyIdSet notEmptyIdSet) {

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

        boolean passwordFlag =
            StrUtil.isNotBlank(dto.getNewPassword()) && StrUtil.isNotBlank(dto.getNewOriginPassword());

        String password = "";

        if (passwordFlag) {

            String paramValue = SysParamUtil.getValueById(ParamConstant.RSA_PRIVATE_KEY_ID); // 获取非对称 私钥
            dto.setNewOriginPassword(MyRsaUtil.rsaDecrypt(dto.getNewOriginPassword(), paramValue));
            dto.setNewPassword(MyRsaUtil.rsaDecrypt(dto.getNewPassword(), paramValue));

            if (BooleanUtil.isFalse(ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getNewOriginPassword()))) {

                ApiResultVO.error(
                    com.cmcorg20230301.engine.be.sign.helper.exception.BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常

            }

            password = PasswordConvertUtil.convert(dto.getNewPassword(), true);

        }

        lambdaUpdate().in(BaseEntity::getId, dto.getIdSet()).set(SysUserDO::getPassword, password).update();

        refreshJwtSecretSuf(new NotEmptyIdSet(dto.getIdSet())); // 刷新：jwt私钥后缀

        return BaseBizCodeEnum.OK;

    }

}
