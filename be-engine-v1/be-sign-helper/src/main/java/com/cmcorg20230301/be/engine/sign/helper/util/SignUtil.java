package com.cmcorg20230301.be.engine.sign.helper.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.dept.mapper.SysDeptRefUserMapper;
import com.cmcorg20230301.be.engine.dept.model.entity.SysDeptRefUserDO;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileDO;
import com.cmcorg20230301.be.engine.file.base.service.SysFileService;
import com.cmcorg20230301.be.engine.file.base.util.SysFileUtil;
import com.cmcorg20230301.be.engine.model.exception.IBizCode;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.BaseRegexConstant;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.model.model.constant.ParamConstant;
import com.cmcorg20230301.be.engine.model.model.interfaces.IRedisKey;
import com.cmcorg20230301.be.engine.mysql.util.TransactionUtil;
import com.cmcorg20230301.be.engine.post.mapper.SysPostRefUserMapper;
import com.cmcorg20230301.be.engine.post.model.entity.SysPostRefUserDO;
import com.cmcorg20230301.be.engine.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysRoleRefUserMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantRefUserMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.properties.SecurityProperties;
import com.cmcorg20230301.be.engine.security.util.*;
import com.cmcorg20230301.be.engine.sign.helper.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.util.util.NicknameUtil;
import com.cmcorg20230301.be.engine.util.util.VoidFunc2;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@Slf4j(topic = LogTopicConstant.USER)
public class SignUtil {

    private static SysUserInfoMapper sysUserInfoMapper;
    private static SysUserMapper sysUserMapper;
    private static SysRoleRefUserMapper sysRoleRefUserMapper;
    private static RedissonClient redissonClient;
    private static SecurityProperties securityProperties;
    private static SysDeptRefUserMapper sysDeptRefUserMapper;
    private static SysPostRefUserMapper sysPostRefUserMapper;
    private static SysTenantRefUserMapper sysTenantRefUserMapper;
    private static SysFileService sysFileService;

    public SignUtil(SysUserInfoMapper sysUserInfoMapper, RedissonClient redissonClient, SysUserMapper sysUserMapper,
        SecurityProperties securityProperties, SysRoleRefUserMapper sysRoleRefUserMapper,
        SysDeptRefUserMapper sysDeptRefUserMapper, SysPostRefUserMapper sysPostRefUserMapper,
        SysTenantRefUserMapper sysTenantRefUserMapper, SysFileService sysFileService) {

        SignUtil.sysUserInfoMapper = sysUserInfoMapper;
        SignUtil.sysUserMapper = sysUserMapper;
        SignUtil.redissonClient = redissonClient;
        SignUtil.securityProperties = securityProperties;
        SignUtil.sysRoleRefUserMapper = sysRoleRefUserMapper;
        SignUtil.sysDeptRefUserMapper = sysDeptRefUserMapper;
        SignUtil.sysPostRefUserMapper = sysPostRefUserMapper;
        SignUtil.sysTenantRefUserMapper = sysTenantRefUserMapper;
        SignUtil.sysFileService = sysFileService;

    }

    /**
     * 发送验证码
     */
    public static String sendCode(String key, LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper,
        boolean mustExist, IBizCode iBizCode, Consumer<String> consumer) {

        return RedissonUtil.doLock(key, () -> {

            // 判断是否存在
            boolean exists;
            if (lambdaQueryChainWrapper == null) {

                exists = true;

            } else {

                exists = lambdaQueryChainWrapper.exists();

            }

            if (mustExist) {

                if (BooleanUtil.isFalse(exists)) {
                    ApiResultVO.error(iBizCode);
                }

            } else {

                if (exists) {
                    ApiResultVO.error(iBizCode);
                }

            }

            String code = CodeUtil.getCode();

            // 保存到 redis中，设置 10分钟过期
            redissonClient.getBucket(key).set(code, Duration.ofMillis(BaseConstant.LONG_CODE_EXPIRE_TIME));

            consumer.accept(code); // 进行额外的处理

            return BaseBizCodeEnum.SEND_OK;

        });

    }

    /**
     * 获取账户信息，并执行发送验证码操作
     */
    public static String getAccountAndSendCode(Enum<? extends IRedisKey> redisKeyEnum,
        VoidFunc2<String, String> voidFunc2) {

        String account = getAccountByIdAndRedisKeyEnum(redisKeyEnum, UserUtil.getCurrentUserIdNotAdmin());

        if (RedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum)) {

            if (StrUtil.isBlank(account)) {

                ApiResultVO
                    .error(BaseBizCodeEnum.UNABLE_TO_SEND_VERIFICATION_CODE_BECAUSE_THE_EMAIL_ADDRESS_IS_NOT_BOUND);

            }

        } else if (RedisKeyEnum.PRE_PHONE.equals(redisKeyEnum)) {

            if (StrUtil.isBlank(account)) {

                ApiResultVO.error(BaseBizCodeEnum.UNABLE_TO_SEND_VERIFICATION_CODE_BECAUSE_THE_PHONE_IS_NOT_BOUND);

            }

        }

        String code = CodeUtil.getCode();

        // 保存到 redis中，设置 10分钟过期
        redissonClient.getBucket(redisKeyEnum + account)
            .set(code, Duration.ofMillis(BaseConstant.LONG_CODE_EXPIRE_TIME));

        // 执行：发送验证码操作
        voidFunc2.call(code, account);

        return BaseBizCodeEnum.SEND_OK;

    }

    /**
     * 注册
     */
    public static String signUp(String password, String originPassword, String code,
        Enum<? extends IRedisKey> redisKeyEnum, String account, @Nullable Long tenantId) {

        if (BaseConstant.ADMIN_ACCOUNT.equals(account)) {

            ApiResultVO.error(BaseBizCodeEnum.THE_ADMIN_ACCOUNT_DOES_NOT_SUPPORT_THIS_OPERATION);

        }

        String paramValue = SysParamUtil.getValueById(ParamConstant.RSA_PRIVATE_KEY_ID); // 获取非对称 私钥
        password = MyRsaUtil.rsaDecrypt(password, paramValue);
        originPassword = MyRsaUtil.rsaDecrypt(originPassword, paramValue);

        if (BooleanUtil.isFalse(ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, originPassword))) {

            ApiResultVO.error(BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常

        }

        String key = redisKeyEnum + account;

        String finalPassword = password;

        RBucket<String> bucket = redissonClient.getBucket(key);

        boolean checkCodeFlag =
            RedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum) || RedisKeyEnum.PRE_PHONE.equals(redisKeyEnum);

        return RedissonUtil.doLock(key, () -> {

            if (checkCodeFlag) {
                CodeUtil.checkCode(code, bucket.get()); // 检查 code是否正确
            }

            // 检查：注册的登录账号是否存在
            boolean exist = accountIsExists(redisKeyEnum, account, null, tenantId);

            if (exist) {

                if (checkCodeFlag) {

                    bucket.delete(); // 删除：验证码

                }

                ApiResultVO.error(BizCodeEnum.THE_ACCOUNT_HAS_ALREADY_BEEN_REGISTERED);

            }

            Map<Enum<? extends IRedisKey>, String> accountMap = MapUtil.newHashMap();
            accountMap.put(redisKeyEnum, account);

            // 新增：用户
            SignUtil.insertUser(finalPassword, accountMap, true, null, null, tenantId);

            if (checkCodeFlag) {
                bucket.delete(); // 删除：验证码
            }

            return "注册成功";

        });

    }

    /**
     * 新增：用户
     */
    public static SysUserDO insertUser(String password, Map<Enum<? extends IRedisKey>, String> accountMap,
        boolean checkPasswordBlank, SysUserInfoDO tempSysUserInfoDO, Boolean enableFlag, @Nullable Long tenantId) {

        // 获取：SysUserDO对象
        SysUserDO sysUserDO = insertUserGetSysUserDO(password, accountMap, checkPasswordBlank, enableFlag, tenantId);

        return TransactionUtil.exec(() -> {

            sysUserMapper.insert(sysUserDO); // 保存：用户

            SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();

            sysUserInfoDO.setId(sysUserDO.getId());
            sysUserInfoDO.setUuid(IdUtil.simpleUUID());
            sysUserInfoDO.setTenantId(sysUserDO.getTenantId()); // 设置：租户 id

            if (tempSysUserInfoDO == null) {

                sysUserInfoDO.setNickname(NicknameUtil.getRandomNickname());
                sysUserInfoDO.setBio("");

                sysUserInfoDO.setAvatarFileId(-1L);

            } else {

                sysUserInfoDO.setNickname(
                    MyEntityUtil.getNotNullStr(tempSysUserInfoDO.getNickname(), NicknameUtil.getRandomNickname()));

                sysUserInfoDO.setBio(MyEntityUtil.getNotNullStr(tempSysUserInfoDO.getBio()));

                sysUserInfoDO.setAvatarFileId(MyEntityUtil.getNotNullLong(tempSysUserInfoDO.getAvatarFileId()));

            }

            sysUserInfoMapper.insert(sysUserInfoDO); // 保存：用户基本信息

            UserUtil.setJwtSecretSuf(sysUserDO.getId()); // 设置：jwt秘钥后缀

            return sysUserDO;

        });

    }

    /**
     * 获取：SysUserDO对象
     */
    @NotNull
    private static SysUserDO insertUserGetSysUserDO(String password, Map<Enum<? extends IRedisKey>, String> accountMap,
        boolean checkPasswordBlank, Boolean enableFlag, @Nullable Long tenantId) {

        SysUserDO sysUserDO = new SysUserDO();

        if (enableFlag == null) {
            sysUserDO.setEnableFlag(true);
        } else {
            sysUserDO.setEnableFlag(enableFlag);
        }

        sysUserDO.setDelFlag(false);
        sysUserDO.setRemark("");

        sysUserDO.setEmail("");
        sysUserDO.setSignInName("");
        sysUserDO.setPhone("");
        sysUserDO.setWxOpenId("");

        for (Map.Entry<Enum<? extends IRedisKey>, String> item : accountMap.entrySet()) {

            if (RedisKeyEnum.PRE_EMAIL.equals(item.getKey())) {

                sysUserDO.setEmail(item.getValue());

            } else if (RedisKeyEnum.PRE_SIGN_IN_NAME.equals(item.getKey())) {

                sysUserDO.setSignInName(item.getValue());

            } else if (RedisKeyEnum.PRE_PHONE.equals(item.getKey())) {

                sysUserDO.setPhone(item.getValue());

            } else if (RedisKeyEnum.PRE_WX_OPEN_ID.equals(item.getKey())) {

                sysUserDO.setWxOpenId(item.getValue());

            }

        }

        sysUserDO.setPassword(PasswordConvertUtil.convert(password, checkPasswordBlank));

        tenantId = SysTenantUtil.getTenantId(tenantId);

        sysUserDO.setTenantId(tenantId); // 设置：租户 id

        return sysUserDO;

    }

    /**
     * 直接通过账号登录
     * 注意：这是一个高风险方法，调用时，请确认账号来源的可靠性！
     */
    @NotNull
    public static String signInAccount(LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper,
        Enum<? extends IRedisKey> redisKeyEnum, String account, SysUserInfoDO tempSysUserInfoDO,
        @Nullable Long tenantId) {

        String key = redisKeyEnum + account;

        return RedissonUtil.doLock(key, () -> {

            // 登录时，获取账号信息
            SysUserDO sysUserDO = signInGetSysUserDO(lambdaQueryChainWrapper, false, tenantId);

            if (sysUserDO == null) {

                // 如果登录的账号不存在，则进行新增
                Map<Enum<? extends IRedisKey>, String> accountMap = MapUtil.newHashMap();

                accountMap.put(redisKeyEnum, account);

                sysUserDO = SignUtil.insertUser(null, accountMap, false, tempSysUserInfoDO, null, tenantId);

            }

            // 登录时，获取：jwt
            return signInGetJwt(sysUserDO);

        });

    }

    /**
     * 验证码登录
     */
    @NotNull
    public static String signInCode(LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper, String code,
        Enum<? extends IRedisKey> redisKeyEnum, String account, @Nullable Long tenantId) {

        // 登录时，获取账号信息
        SysUserDO sysUserDOTemp = signInGetSysUserDO(lambdaQueryChainWrapper, false, tenantId);

        String key = redisKeyEnum + account;

        return RedissonUtil.doLock(key, () -> {

            RBucket<String> bucket = redissonClient.getBucket(key);

            CodeUtil.checkCode(code, bucket.get()); // 检查 code是否正确

            SysUserDO sysUserDO = sysUserDOTemp;

            if (sysUserDO == null) {

                // 如果登录的账号不存在，则进行新增
                Map<Enum<? extends IRedisKey>, String> accountMap = MapUtil.newHashMap();
                accountMap.put(redisKeyEnum, account);

                sysUserDO = SignUtil.insertUser(null, accountMap, false, null, null, tenantId);

            }

            bucket.delete(); // 删除：验证码

            // 登录时，获取：jwt
            return signInGetJwt(sysUserDO);

        });

    }

    /**
     * 账号密码登录
     */
    public static String signInPassword(LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper, String password,
        String account, @Nullable Long tenantId) {

        // 密码解密
        password = MyRsaUtil.rsaDecrypt(password);

        // 如果是 admin账户
        if (BaseConstant.ADMIN_ACCOUNT.equals(account)) {

            if (signInPasswordForAdmin(password)) {
                return MyJwtUtil.generateJwt(BaseConstant.ADMIN_ID, null, null, tenantId);
            }

        }

        // 登录时，获取账号信息
        SysUserDO sysUserDO = signInGetSysUserDO(lambdaQueryChainWrapper, true, tenantId);

        if (sysUserDO == null || StrUtil.isBlank(sysUserDO.getPassword())) { // 备注：这里 sysUserDO不会为 null
            ApiResultVO.error(BizCodeEnum.NO_PASSWORD_SET); // 未设置密码，请点击【忘记密码】，进行密码设置
        }

        if (BooleanUtil.isFalse(PasswordConvertUtil.match(sysUserDO.getPassword(), password))) {

            // 密码输入错误处理
            passwordErrorHandler(sysUserDO.getId());

            ApiResultVO.error(BizCodeEnum.ACCOUNT_OR_PASSWORD_NOT_VALID);

        }

        // 登录时，获取：jwt
        return signInGetJwt(sysUserDO);

    }

    /**
     * admin登录，登录成功返回 true
     */
    private static boolean signInPasswordForAdmin(String password) {

        if (BooleanUtil.isTrue(securityProperties.getAdminEnable())) { // 并且配置文件中允许 admin登录

            // 判断：密码错误次数过多，是否被冻结
            checkTooManyPasswordError(BaseConstant.ADMIN_ID);

            if (BooleanUtil.isFalse(securityProperties.getAdminPassword().equals(password))) {

                // 密码输入错误处理
                passwordErrorHandler(BaseConstant.ADMIN_ID);

                ApiResultVO.error(BizCodeEnum.ACCOUNT_OR_PASSWORD_NOT_VALID);

                return false;

            } else {

                return true;

            }

        } else {

            ApiResultVO.error(BizCodeEnum.ACCOUNT_OR_PASSWORD_NOT_VALID);

            return false;

        }

    }

    /**
     * 登录时，获取：jwt
     */
    @Nullable
    private static String signInGetJwt(SysUserDO sysUserDO) {

        // 校验密码，成功之后，再判断是否被冻结，免得透露用户被封号的信息
        if (BooleanUtil.isFalse(sysUserDO.getEnableFlag())) {

            ApiResultVO.error(BaseBizCodeEnum.ACCOUNT_IS_DISABLED);

        }

        String jwtSecretSuf = UserUtil.getJwtSecretSuf(sysUserDO.getId());

        // 颁发，并返回 jwt
        return MyJwtUtil.generateJwt(sysUserDO.getId(), jwtSecretSuf, null, sysUserDO.getTenantId());

    }

    /**
     * 登录时，获取：账号信息
     */
    @Nullable
    private static SysUserDO signInGetSysUserDO(LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper,
        boolean errorFlag, @Nullable Long tenantId) {

        tenantId = SysTenantUtil.getTenantId(tenantId);

        lambdaQueryChainWrapper.eq(BaseEntityNoId::getTenantId, tenantId);

        SysUserDO sysUserDO = lambdaQueryChainWrapper
            .select(SysUserDO::getPassword, BaseEntity::getEnableFlag, BaseEntity::getId, BaseEntityNoId::getTenantId)
            .one();

        // 账户是否存在
        if (sysUserDO == null) {

            if (errorFlag) {

                ApiResultVO.error(BizCodeEnum.ACCOUNT_OR_PASSWORD_NOT_VALID);

            } else {

                return null;

            }

        }

        // 判断：密码错误次数过多，是否被冻结
        checkTooManyPasswordError(sysUserDO.getId());

        return sysUserDO;

    }

    /**
     * 判断：密码错误次数过多，是否被冻结
     */
    private static void checkTooManyPasswordError(Long userId) {

        String lockMessageStr =
            redissonClient.<Long, String>getMap(RedisKeyEnum.PRE_TOO_MANY_PASSWORD_ERROR.name()).get(userId);

        if (StrUtil.isNotBlank(lockMessageStr)) {
            ApiResultVO.error(BizCodeEnum.TOO_MANY_PASSWORD_ERROR);
        }

    }

    /**
     * 密码错误次数过多，直接锁定账号，可以进行【忘记密码】操作，解除锁定
     */
    private static void passwordErrorHandler(Long userId) {

        if (userId == null) {
            ApiResultVO.sysError();
        }

        RAtomicLong atomicLong =
            redissonClient.getAtomicLong(RedisKeyEnum.PRE_PASSWORD_ERROR_COUNT.name() + ":" + userId);

        long count = atomicLong.incrementAndGet(); // 次数 + 1

        if (count == 1) {
            atomicLong.expire(Duration.ofMillis(BaseConstant.DAY_30_EXPIRE_TIME)); // 等于 1表示，是第一次访问，则设置过期时间
        }

        if (count > 10) {

            // 超过十次密码错误，则封禁账号，下次再错误，则才会提示
            redissonClient.<Long, String>getMap(RedisKeyEnum.PRE_TOO_MANY_PASSWORD_ERROR.name())
                .put(userId, "密码错误次数过多，被锁定的账号");

            atomicLong.delete(); // 清空错误次数

        }

    }

    /**
     * 修改密码
     */
    public static String updatePassword(String newPasswordTemp, String originNewPasswordTemp,
        Enum<? extends IRedisKey> redisKeyEnum, String code, String oldPassword) {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        String paramValue = SysParamUtil.getValueById(ParamConstant.RSA_PRIVATE_KEY_ID); // 获取非对称 私钥

        if (RedisKeyEnum.PRE_SIGN_IN_NAME.equals(redisKeyEnum)) {
            checkCurrentPassword(oldPassword, currentUserIdNotAdmin, paramValue); // 检查：当前密码是否正确
        }

        String newPassword = MyRsaUtil.rsaDecrypt(newPasswordTemp, paramValue);
        String originNewPassword = MyRsaUtil.rsaDecrypt(originNewPasswordTemp, paramValue);

        if (BooleanUtil.isFalse(ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, originNewPassword))) {
            ApiResultVO.error(BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常
        }

        // 获取：账号
        String account = getAccountByIdAndRedisKeyEnum(redisKeyEnum, currentUserIdNotAdmin);

        String key = redisKeyEnum + account;

        return RedissonUtil.doLock(key, () -> {

            RBucket<String> bucket = redissonClient.getBucket(key);

            // 是否检查：验证码
            boolean checkCodeFlag = BooleanUtil.isFalse(RedisKeyEnum.PRE_SIGN_IN_NAME.equals(redisKeyEnum));

            if (checkCodeFlag) {
                CodeUtil.checkCode(code, bucket.get()); // 检查 code是否正确
            }

            SysUserDO sysUserDO = new SysUserDO();
            sysUserDO.setId(currentUserIdNotAdmin);

            sysUserDO.setPassword(PasswordConvertUtil.convert(newPassword, true));

            return TransactionUtil.exec(() -> {

                sysUserMapper.updateById(sysUserDO); // 保存：用户

                if (checkCodeFlag) {
                    bucket.delete(); // 删除：验证码
                }

                UserUtil.setJwtSecretSuf(currentUserIdNotAdmin); // 设置：jwt秘钥后缀

                return BaseBizCodeEnum.OK;

            });

        });

    }

    /**
     * 检查：当前密码是否正确
     */
    private static void checkCurrentPassword(String currentPassword, Long currentUserIdNotAdmin, String paramValue) {

        // 判断：密码错误次数过多，是否被冻结
        checkTooManyPasswordError(currentUserIdNotAdmin);

        SysUserDO sysUserDO = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntity::getId, currentUserIdNotAdmin)
            .select(SysUserDO::getPassword).one();

        if (sysUserDO == null) {
            ApiResultVO.error(BizCodeEnum.USER_DOES_NOT_EXIST);
        }

        if (paramValue == null) {
            currentPassword = MyRsaUtil.rsaDecrypt(currentPassword);
        } else {
            currentPassword = MyRsaUtil.rsaDecrypt(currentPassword, paramValue);
        }

        if (BooleanUtil.isFalse(PasswordConvertUtil.match(sysUserDO.getPassword(), currentPassword))) {

            // 密码输入错误处理
            passwordErrorHandler(currentUserIdNotAdmin);

            ApiResultVO.error(BizCodeEnum.PASSWORD_NOT_VALID);

        }

    }

    /**
     * 获取：账号，通过：redisKeyEnum
     */
    @NotNull
    private static String getAccountByIdAndRedisKeyEnum(Enum<? extends IRedisKey> redisKeyEnum,
        Long currentUserIdNotAdmin) {

        // 获取：用户信息
        SysUserDO sysUserDO = getSysUserDOByIdAndRedisKeyEnum(redisKeyEnum, currentUserIdNotAdmin);

        if (RedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum)) {

            return sysUserDO.getEmail();

        } else if (RedisKeyEnum.PRE_SIGN_IN_NAME.equals(redisKeyEnum)) {

            return sysUserDO.getSignInName();

        } else if (RedisKeyEnum.PRE_PHONE.equals(redisKeyEnum)) {

            return sysUserDO.getPhone();

        } else {

            ApiResultVO.sysError();

            return null; // 这里不会执行，只是为了通过语法检查

        }

    }

    /**
     * 获取：SysUserDO，通过：userId和 redisKeyEnum
     */
    @NotNull
    private static SysUserDO getSysUserDOByIdAndRedisKeyEnum(Enum<? extends IRedisKey> redisKeyEnum,
        Long currentUserIdNotAdmin) {

        LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper =
            ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntity::getId, currentUserIdNotAdmin);

        if (RedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.select(SysUserDO::getEmail);

        } else if (RedisKeyEnum.PRE_SIGN_IN_NAME.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.select(SysUserDO::getSignInName);

        } else if (RedisKeyEnum.PRE_PHONE.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.select(SysUserDO::getPhone);

        } else {

            ApiResultVO.sysError();

        }

        SysUserDO sysUserDO = lambdaQueryChainWrapper.one();

        if (sysUserDO == null) {
            ApiResultVO.error(BizCodeEnum.USER_DOES_NOT_EXIST);
        }

        return sysUserDO;

    }

    /**
     * 修改登录账号
     */
    public static String updateAccount(String oldCode, String newCode, Enum<? extends IRedisKey> redisKeyEnum,
        String newAccount, String currentPassword) {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (RedisKeyEnum.PRE_SIGN_IN_NAME.equals(redisKeyEnum)) {
            checkCurrentPassword(currentPassword, currentUserIdNotAdmin, null);
        }

        // 获取：旧的账号
        String oldAccount = getAccountByIdAndRedisKeyEnum(redisKeyEnum, currentUserIdNotAdmin);

        String oldKey = redisKeyEnum + oldAccount;
        String newKey = redisKeyEnum + newAccount;

        return RedissonUtil.doMultiLock(null, CollUtil.newHashSet(oldKey, newKey), () -> {

            RBucket<String> oldBucket = redissonClient.getBucket(oldKey);

            if (RedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum)) {

                // 检查 code是否正确
                CodeUtil.checkCode(oldCode, oldBucket.get(), "操作失败：请先获取旧邮箱的验证码", "旧邮箱验证码有误，请重新输入");

            } else if (RedisKeyEnum.PRE_PHONE.equals(redisKeyEnum)) {

                // 检查 code是否正确
                CodeUtil.checkCode(oldCode, oldBucket.get(), "操作失败：请先获取旧手机号码的验证码", "旧手机号码验证码有误，请重新输入");

            }

            RBucket<String> newBucket = redissonClient.getBucket(newKey);

            if (RedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum)) {

                // 检查 code是否正确
                CodeUtil.checkCode(newCode, newBucket.get(), "操作失败：请先获取新邮箱的验证码", "新邮箱验证码有误，请重新输入");

            } else if (RedisKeyEnum.PRE_PHONE.equals(redisKeyEnum)) {

                // 检查 code是否正确
                CodeUtil.checkCode(oldCode, oldBucket.get(), "操作失败：请先获取新手机号码的验证码", "新手机号码验证码有误，请重新输入");

            }

            // 检查：新的登录账号是否存在
            boolean exist = accountIsExists(redisKeyEnum, newAccount, null, currentTenantIdDefault);

            // 是否删除：redis中的验证码
            boolean deleteRedisFlag =
                RedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum) || RedisKeyEnum.PRE_PHONE.equals(redisKeyEnum);

            if (exist) {
                if (deleteRedisFlag) {
                    newBucket.delete();
                }
                ApiResultVO.errorMsg("操作失败：已被人占用");
            }

            SysUserDO sysUserDO = new SysUserDO();
            sysUserDO.setId(currentUserIdNotAdmin);

            // 通过：RedisKeyEnum，设置：账号
            setSysUserDOAccountByRedisKeyEnum(redisKeyEnum, newAccount, sysUserDO);

            return TransactionUtil.exec(() -> {

                sysUserMapper.updateById(sysUserDO); // 更新：用户

                if (deleteRedisFlag) {

                    // 删除：验证码
                    oldBucket.delete();
                    newBucket.delete();

                }

                UserUtil.setJwtSecretSuf(currentUserIdNotAdmin); // 设置：jwt秘钥后缀

                return BaseBizCodeEnum.OK;

            });

        });

    }

    /**
     * 通过：RedisKeyEnum，设置：账号
     */
    private static void setSysUserDOAccountByRedisKeyEnum(Enum<? extends IRedisKey> redisKeyEnum, String newAccount,
        SysUserDO sysUserDO) {

        if (RedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum)) {

            sysUserDO.setEmail(newAccount);

        } else if (RedisKeyEnum.PRE_SIGN_IN_NAME.equals(redisKeyEnum)) {

            sysUserDO.setSignInName(newAccount);

        } else if (RedisKeyEnum.PRE_PHONE.equals(redisKeyEnum)) {

            sysUserDO.setPhone(newAccount);

        } else {

            ApiResultVO.sysError();

        }

    }

    /**
     * 检查登录账号是否存在
     */
    public static boolean accountIsExists(Enum<? extends IRedisKey> redisKeyEnum, String newAccount, @Nullable Long id,
        @Nullable Long tenantId) {

        tenantId = SysTenantUtil.getTenantId(tenantId);

        LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper =
            ChainWrappers.lambdaQueryChain(sysUserMapper).ne(id != null, BaseEntity::getId, id)
                .eq(BaseEntityNoId::getTenantId, tenantId);

        if (RedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.eq(SysUserDO::getEmail, newAccount);

        } else if (RedisKeyEnum.PRE_SIGN_IN_NAME.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.eq(SysUserDO::getSignInName, newAccount);

        } else if (RedisKeyEnum.PRE_PHONE.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.eq(SysUserDO::getPhone, newAccount);

        } else {

            ApiResultVO.sysError();

        }

        return lambdaQueryChainWrapper.exists();

    }

    /**
     * 抛出：该账号已被注册，异常
     */
    public static void accountIsExistError() {

        ApiResultVO.error(BizCodeEnum.THE_ACCOUNT_HAS_ALREADY_BEEN_REGISTERED);

    }

    /**
     * 忘记密码
     */
    public static String forgetPassword(String newPasswordTemp, String originNewPasswordTemp, String code,
        Enum<? extends IRedisKey> redisKeyEnum, String account,
        LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper) {

        String paramValue = SysParamUtil.getValueById(ParamConstant.RSA_PRIVATE_KEY_ID); // 获取非对称 私钥
        String newPassword = MyRsaUtil.rsaDecrypt(newPasswordTemp, paramValue);
        String originNewPassword = MyRsaUtil.rsaDecrypt(originNewPasswordTemp, paramValue);

        if (BooleanUtil.isFalse(ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, originNewPassword))) {
            ApiResultVO.error(BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常
        }

        String key = redisKeyEnum.name() + account;

        return RedissonUtil.doLock(key, () -> {

            RBucket<String> bucket = redissonClient.getBucket(key);

            CodeUtil.checkCode(code, bucket.get()); // 检查 code是否正确

            // 获取：用户 id
            SysUserDO sysUserDO = lambdaQueryChainWrapper.select(BaseEntity::getId).one();

            if (sysUserDO == null) {

                bucket.delete(); // 删除：验证码
                ApiResultVO.error(BizCodeEnum.USER_DOES_NOT_EXIST);

            }

            sysUserDO.setPassword(PasswordConvertUtil.convert(newPassword, true));

            return TransactionUtil.exec(() -> {

                sysUserMapper.updateById(sysUserDO); // 保存：用户

                RedissonUtil.batch((batch) -> {

                    // 移除密码错误次数相关
                    batch.getBucket(RedisKeyEnum.PRE_PASSWORD_ERROR_COUNT.name() + ":" + sysUserDO.getId())
                        .deleteAsync();
                    batch.getMap(RedisKeyEnum.PRE_TOO_MANY_PASSWORD_ERROR.name()).removeAsync(sysUserDO.getId());

                    // 删除：验证码
                    batch.getBucket(key).deleteAsync();

                });

                UserUtil.setJwtSecretSuf(sysUserDO.getId()); // 设置：jwt秘钥后缀

                return BaseBizCodeEnum.OK;

            });

        });

    }

    /**
     * 账号注销
     */
    public static String signDelete(String code, Enum<? extends IRedisKey> redisKeyEnum, String currentPassword) {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        if (StrUtil.isNotBlank(currentPassword)) {
            checkCurrentPassword(currentPassword, currentUserIdNotAdmin, null);
        }

        // 通过：redisKeyEnum，获取账号
        String account = getAccountByIdAndRedisKeyEnum(redisKeyEnum, currentUserIdNotAdmin);

        String key = redisKeyEnum + account;

        return RedissonUtil.doLock(key, () -> {

            RBucket<String> bucket = redissonClient.getBucket(key);

            // 是否：检查验证码
            boolean checkCodeFlag = BooleanUtil.isFalse(RedisKeyEnum.PRE_SIGN_IN_NAME.equals(redisKeyEnum));

            if (checkCodeFlag) {
                CodeUtil.checkCode(code, bucket.get()); // 检查 code是否正确
            }

            return TransactionUtil.exec(() -> {

                // 执行：账号注销
                doSignDelete(CollUtil.newHashSet(currentUserIdNotAdmin));

                if (checkCodeFlag) {
                    bucket.delete(); // 删除：验证码
                }

                return BaseBizCodeEnum.OK;

            });

        });

    }

    /**
     * 执行：账号注销
     */
    public static void doSignDelete(Set<Long> userIdSet) {

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(userIdSet,
            tenantIdSet -> ChainWrappers.lambdaQueryChain(sysUserMapper).in(BaseEntity::getId, userIdSet)
                .in(BaseEntityNoId::getTenantId, tenantIdSet).count());

        // 找到用户：拥有的文件
        List<SysFileDO> sysFileDOList =
            sysFileService.lambdaQuery().in(SysFileDO::getBelongId, userIdSet).select(BaseEntity::getId).list();

        Set<Long> fileIdSet = sysFileDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        TransactionUtil.exec(() -> {

            sysUserMapper.deleteBatchIds(userIdSet); // 直接：删除用户

            doSignDeleteSub(userIdSet, true); // 删除子表数据

            // 删除：用户的文件
            SysFileUtil.removeByFileIdSet(fileIdSet, false);

            for (Long item : userIdSet) {

                // 删除 jwt后缀
                UserUtil.removeJwtSecretSuf(item);

            }

        });

    }

    /**
     * 执行：账号注销，删除子表数据
     */
    public static void doSignDeleteSub(Set<Long> idSet, boolean removeUserInfoFlag) {

        TransactionUtil.exec(() -> {

            if (removeUserInfoFlag) {

                // 直接：删除用户基本信息
                ChainWrappers.lambdaUpdateChain(sysUserInfoMapper).in(SysUserInfoDO::getId, idSet).remove();

            }

            // 直接：删除用户绑定的角色
            ChainWrappers.lambdaUpdateChain(sysRoleRefUserMapper).in(SysRoleRefUserDO::getUserId, idSet).remove();

            // 直接：删除用户绑定的部门
            ChainWrappers.lambdaUpdateChain(sysDeptRefUserMapper).in(SysDeptRefUserDO::getUserId, idSet).remove();

            // 直接：删除用户绑定的岗位
            ChainWrappers.lambdaUpdateChain(sysPostRefUserMapper).in(SysPostRefUserDO::getUserId, idSet).remove();

            // 直接：删除用户绑定的租户
            ChainWrappers.lambdaUpdateChain(sysTenantRefUserMapper).in(SysTenantRefUserDO::getUserId, idSet).remove();

        });

    }

    /**
     * 绑定登录账号
     */
    public static String bindAccount(String code, Enum<? extends IRedisKey> redisKeyEnum, String account) {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        String key = redisKeyEnum + account;

        return RedissonUtil.doLock(key, () -> {

            RBucket<String> bucket = redissonClient.getBucket(key);

            // 检查：绑定的登录账号是否存在
            boolean exist = accountIsExists(redisKeyEnum, account, null, currentTenantIdDefault);

            if (exist) {

                bucket.delete();
                ApiResultVO.errorMsg("操作失败：账号已被绑定，请重试");

            }

            CodeUtil.checkCode(code, bucket.get()); // 检查 code是否正确

            SysUserDO sysUserDO = new SysUserDO();

            // 通过：RedisKeyEnum，设置：账号
            setSysUserDOAccountByRedisKeyEnum(redisKeyEnum, account, sysUserDO);

            sysUserDO.setId(currentUserIdNotAdmin);

            return TransactionUtil.exec(() -> {

                sysUserMapper.updateById(sysUserDO); // 保存：用户

                bucket.delete(); // 删除：验证码

                UserUtil.setJwtSecretSuf(currentUserIdNotAdmin); // 设置：jwt秘钥后缀

                return BaseBizCodeEnum.OK;

            });

        });

    }

    /**
     * 检查：是否可以进行操作：根据用户有无手机号，有无邮箱，有无密码，来做判断
     * 敏感操作都需要调用此方法，例如：修改登录名，修改邮箱，修改手机号，修改绑定的微信，忘记密码，账号注销，绑定邮箱，绑定手机，绑定微信
     *
     * @param account      账号信息，一般情况为 null，目前只有忘记密码的时候，才会传值
     * @param notCheckFlag 是否不检查，一般情况为 false，目前，在绑定邮箱，修改邮箱的时候，才会为 true，目的：不检查：是否有手机
     */
    public static void checkWillError(RedisKeyEnum redisKeyEnum, String account, boolean notCheckFlag) {

        if (notCheckFlag) {
            return;
        }

        Long userId = null;

        if (StrUtil.isBlank(account)) {
            userId = UserUtil.getCurrentUserIdNotAdmin();
        }

        boolean legalFlag = false; // 是否合法

        if (redisKeyEnum.equals(RedisKeyEnum.PRE_SIGN_IN_NAME)) { // 如果是：登录名

            // 判断：密码不能为空，并且不能有邮箱，手机
            legalFlag = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(userId != null, BaseEntity::getId, userId)
                .ne(SysUserDO::getPassword, "").eq(SysUserDO::getEmail, "").eq(SysUserDO::getPhone, "").exists();

        } else if (redisKeyEnum.equals(RedisKeyEnum.PRE_EMAIL)) { // 如果是：邮箱

            // 判断：不能有手机
            legalFlag = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(userId != null, BaseEntity::getId, userId)
                .eq(userId == null, SysUserDO::getEmail, account).eq(SysUserDO::getPhone, "").exists();

        } else if (redisKeyEnum.equals(RedisKeyEnum.PRE_PHONE)) { // 如果是：手机号

            legalFlag = true; // 目前手机号操作，都合法

        }

        if (BooleanUtil.isFalse(legalFlag)) { // 如果不合法

            ApiResultVO.errorMsg(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg() + "：" + redisKeyEnum.name());

        }

    }

}
