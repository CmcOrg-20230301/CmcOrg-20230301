package com.cmcorg20230301.engine.be.sign.helper.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.engine.be.model.exception.IBizCode;
import com.cmcorg20230301.engine.be.model.model.constant.BaseConstant;
import com.cmcorg20230301.engine.be.model.model.constant.BaseRegexConstant;
import com.cmcorg20230301.engine.be.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.engine.be.model.model.constant.ParamConstant;
import com.cmcorg20230301.engine.be.model.model.interfaces.IRedisKey;
import com.cmcorg20230301.engine.be.mysql.util.TransactionUtil;
import com.cmcorg20230301.engine.be.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.engine.be.redisson.util.RedissonUtil;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.mapper.SysRoleRefUserMapper;
import com.cmcorg20230301.engine.be.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.engine.be.security.mapper.SysUserMapper;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.entity.SysRoleRefUserDO;
import com.cmcorg20230301.engine.be.security.model.entity.SysUserDO;
import com.cmcorg20230301.engine.be.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.properties.SecurityProperties;
import com.cmcorg20230301.engine.be.security.util.*;
import com.cmcorg20230301.engine.be.sign.helper.configuration.AbstractSignHelperSecurityPermitAllConfiguration;
import com.cmcorg20230301.engine.be.sign.helper.exception.BizCodeEnum;
import com.cmcorg20230301.engine.be.util.util.NicknameUtil;
import com.cmcorg20230301.engine.be.util.util.VoidFunc2;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
@Slf4j(topic = LogTopicConstant.USER)
public class SignUtil {

    private static SysUserInfoMapper sysUserInfoMapper;
    private static SysUserMapper sysUserMapper;
    private static SysRoleRefUserMapper sysRoleRefUserMapper;
    private static RedissonClient redissonClient;
    private static SecurityProperties securityProperties;
    private static List<AbstractSignHelperSecurityPermitAllConfiguration>
        abstractSignHelperSecurityPermitAllConfigurationList;

    public SignUtil(SysUserInfoMapper sysUserInfoMapper, RedissonClient redissonClient, SysUserMapper sysUserMapper,
        SecurityProperties securityProperties,
        List<AbstractSignHelperSecurityPermitAllConfiguration> abstractSignHelperSecurityPermitAllConfigurationList,
        SysRoleRefUserMapper sysRoleRefUserMapper) {

        SignUtil.sysUserInfoMapper = sysUserInfoMapper;
        SignUtil.sysUserMapper = sysUserMapper;
        SignUtil.redissonClient = redissonClient;
        SignUtil.securityProperties = securityProperties;
        SignUtil.abstractSignHelperSecurityPermitAllConfigurationList =
            abstractSignHelperSecurityPermitAllConfigurationList;
        SignUtil.sysRoleRefUserMapper = sysRoleRefUserMapper;

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
            redissonClient.getBucket(key).set(code, BaseConstant.LONG_CODE_EXPIRE_TIME, TimeUnit.MILLISECONDS);

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
            .set(code, BaseConstant.LONG_CODE_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        // 执行：发送验证码操作
        voidFunc2.call(code, account);

        return BaseBizCodeEnum.SEND_OK;

    }

    /**
     * 注册
     */
    public static String signUp(String password, String originPassword, String code,
        Enum<? extends IRedisKey> redisKeyEnum, String account) {

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
            boolean exist = accountIsExists(redisKeyEnum, account, null);

            if (exist) {

                if (checkCodeFlag) {

                    bucket.delete(); // 删除：验证码

                }

                ApiResultVO.error(BizCodeEnum.THE_ACCOUNT_HAS_ALREADY_BEEN_REGISTERED);

            }

            Map<Enum<? extends IRedisKey>, String> accountMap = MapUtil.newHashMap();
            accountMap.put(redisKeyEnum, account);

            // 新增：用户
            SignUtil.insertUser(finalPassword, accountMap, true, null, null);

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
        boolean checkPasswordBlank, SysUserInfoDO tempSysUserInfoDO, Boolean enableFlag) {

        // 获取：SysUserDO对象
        SysUserDO sysUserDO = insertUserGetSysUserDO(password, accountMap, checkPasswordBlank, enableFlag);

        return TransactionUtil.exec(() -> {

            sysUserMapper.insert(sysUserDO); // 保存：用户

            SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();
            sysUserInfoDO.setId(sysUserDO.getId());
            sysUserInfoDO.setUuid(IdUtil.simpleUUID());

            if (tempSysUserInfoDO == null) {

                sysUserInfoDO.setNickname(NicknameUtil.getRandomNickname());
                sysUserInfoDO.setBio("");

                sysUserInfoDO.setAvatarUri("");
                sysUserInfoDO.setAvatarFileId(-1L);
                sysUserInfoDO.setAvatarUriExpireTime(null);

            } else {

                sysUserInfoDO.setNickname(
                    MyEntityUtil.getNotNullStr(tempSysUserInfoDO.getNickname(), NicknameUtil.getRandomNickname()));

                sysUserInfoDO.setBio(MyEntityUtil.getNotNullStr(tempSysUserInfoDO.getBio()));

                sysUserInfoDO.setAvatarUri(MyEntityUtil.getNotNullStr(tempSysUserInfoDO.getAvatarUri()));
                sysUserInfoDO.setAvatarFileId(MyEntityUtil.getNotNullLong(tempSysUserInfoDO.getAvatarFileId()));
                sysUserInfoDO.setAvatarUriExpireTime(tempSysUserInfoDO.getAvatarUriExpireTime());

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
        boolean checkPasswordBlank, Boolean enableFlag) {

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

        return sysUserDO;

    }

    /**
     * 直接通过账号登录
     * 注意：这是一个高风险方法，调用时，请确认账号来源的可靠性！
     */
    @NotNull
    public static String signInAccount(LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper,
        Enum<? extends IRedisKey> redisKeyEnum, String account, SysUserInfoDO tempSysUserInfoDO) {

        String key = redisKeyEnum + account;

        return RedissonUtil.doLock(key, () -> {

            // 登录时，获取账号信息
            SysUserDO sysUserDO = signInGetSysUserDO(lambdaQueryChainWrapper, false);

            if (sysUserDO == null) {

                // 如果登录的账号不存在，则进行新增
                Map<Enum<? extends IRedisKey>, String> accountMap = MapUtil.newHashMap();

                accountMap.put(redisKeyEnum, account);

                sysUserDO = SignUtil.insertUser(null, accountMap, false, tempSysUserInfoDO, null);

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
        Enum<? extends IRedisKey> redisKeyEnum, String account) {

        // 登录时，获取账号信息
        final SysUserDO[] sysUserDOArr = {signInGetSysUserDO(lambdaQueryChainWrapper, false)};

        String key = redisKeyEnum + account;

        return RedissonUtil.doLock(key, () -> {

            RBucket<String> bucket = redissonClient.getBucket(key);

            CodeUtil.checkCode(code, bucket.get()); // 检查 code是否正确

            if (sysUserDOArr[0] == null) {

                // 如果登录的账号不存在，则进行新增
                Map<Enum<? extends IRedisKey>, String> accountMap = MapUtil.newHashMap();
                accountMap.put(redisKeyEnum, account);

                sysUserDOArr[0] = SignUtil.insertUser(null, accountMap, false, null, null);

            }

            bucket.delete(); // 删除：验证码

            // 登录时，获取：jwt
            return signInGetJwt(sysUserDOArr[0]);

        });

    }

    /**
     * 账号密码登录
     */
    public static String signInPassword(LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper, String password,
        String account) {

        // 密码解密
        password = MyRsaUtil.rsaDecrypt(password);

        // 如果是 admin账户
        if (BaseConstant.ADMIN_ACCOUNT.equals(account)) {

            if (signInPasswordForAdmin(password)) {
                return MyJwtUtil.generateJwt(BaseConstant.ADMIN_ID, null, null);
            }

        }

        // 登录时，获取账号信息
        SysUserDO sysUserDO = signInGetSysUserDO(lambdaQueryChainWrapper, true);

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

            ApiResultVO.error(BizCodeEnum.ACCOUNT_IS_DISABLED);

        }

        String jwtSecretSuf = UserUtil.getJwtSecretSuf(sysUserDO.getId());

        // 颁发，并返回 jwt
        return MyJwtUtil.generateJwt(sysUserDO.getId(), jwtSecretSuf, null);

    }

    /**
     * 登录时，获取：账号信息
     */
    @Nullable
    private static SysUserDO signInGetSysUserDO(LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper,
        boolean errorFlag) {

        SysUserDO sysUserDO =
            lambdaQueryChainWrapper.select(SysUserDO::getPassword, BaseEntity::getEnableFlag, BaseEntity::getId).one();

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
            boolean exist = accountIsExists(redisKeyEnum, newAccount, null);

            // 是否删除：redis中的验证码
            boolean deleteRedisFlag =
                RedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum) || RedisKeyEnum.PRE_PHONE.equals(redisKeyEnum);

            if (exist) {
                if (deleteRedisFlag) {
                    newBucket.delete();
                }
                ApiResultVO.error("操作失败：已被人绑定，请重试");
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
    public static boolean accountIsExists(Enum<? extends IRedisKey> redisKeyEnum, String newAccount,
        @Nullable Long id) {

        LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper =
            ChainWrappers.lambdaQueryChain(sysUserMapper).ne(id != null, BaseEntity::getId, id);

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
    public static void doSignDelete(Set<Long> idSet) {

        TransactionUtil.exec(() -> {

            sysUserMapper.deleteBatchIds(idSet); // 直接：删除用户

            doSignDeleteSub(idSet, true); // 删除子表数据

            for (Long item : idSet) {

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

        });

    }

    /**
     * 绑定登录账号
     */
    public static String bindAccount(String code, Enum<? extends IRedisKey> redisKeyEnum, String account) {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        String key = redisKeyEnum + account;

        return RedissonUtil.doLock(key, () -> {

            RBucket<String> bucket = redissonClient.getBucket(key);

            // 检查：绑定的登录账号是否存在
            boolean exist = accountIsExists(redisKeyEnum, account, null);

            if (exist) {

                bucket.delete();
                ApiResultVO.error("操作失败：账号已被绑定，请重试");

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
     * 检查等级
     */
    public static void checkSignLevel(int signLevel) {

        boolean anyMatch =
            abstractSignHelperSecurityPermitAllConfigurationList.stream().anyMatch(it -> it.getSignLevel() > signLevel);

        if (anyMatch) {

            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);

        }

    }

}
