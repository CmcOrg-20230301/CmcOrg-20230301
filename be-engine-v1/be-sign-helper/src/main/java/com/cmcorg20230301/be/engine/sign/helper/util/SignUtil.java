package com.cmcorg20230301.be.engine.sign.helper.util;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.datasource.util.TransactionUtil;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileDO;
import com.cmcorg20230301.be.engine.file.base.service.SysFileService;
import com.cmcorg20230301.be.engine.file.base.util.SysFileUtil;
import com.cmcorg20230301.be.engine.ip2region.util.Ip2RegionUtil;
import com.cmcorg20230301.be.engine.model.exception.IBizCode;
import com.cmcorg20230301.be.engine.model.model.bo.SysQrCodeSceneBindBO;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.BaseRegexConstant;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.model.model.constant.ParamConstant;
import com.cmcorg20230301.be.engine.model.model.interfaces.IRedisKey;
import com.cmcorg20230301.be.engine.model.model.vo.GetQrCodeVO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.model.model.vo.SysQrCodeSceneBindVO;
import com.cmcorg20230301.be.engine.other.app.mapper.SysOtherAppMapper;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppDO;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppTypeEnum;
import com.cmcorg20230301.be.engine.other.app.wx.util.WxUtil;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.*;
import com.cmcorg20230301.be.engine.security.model.configuration.IUserSignConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.*;
import com.cmcorg20230301.be.engine.security.model.interfaces.ISysQrCodeSceneType;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.properties.SecurityProperties;
import com.cmcorg20230301.be.engine.security.properties.SingleSignInProperties;
import com.cmcorg20230301.be.engine.security.util.*;
import com.cmcorg20230301.be.engine.sign.helper.exception.BizCodeEnum;
import com.cmcorg20230301.be.engine.util.util.CallBack;
import com.cmcorg20230301.be.engine.util.util.NicknameUtil;
import com.cmcorg20230301.be.engine.util.util.VoidFunc2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

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
    private static SysOtherAppMapper sysOtherAppMapper;
    private static SysUserSingleSignInMapper sysUserSingleSignInMapper;
    private static SingleSignInProperties singleSignInProperties;

    @Nullable
    private static List<IUserSignConfiguration> iUserSignConfigurationList;

    public SignUtil(SysUserInfoMapper sysUserInfoMapper, RedissonClient redissonClient, SysUserMapper sysUserMapper,
        SecurityProperties securityProperties, SysRoleRefUserMapper sysRoleRefUserMapper,
        SysDeptRefUserMapper sysDeptRefUserMapper, SysPostRefUserMapper sysPostRefUserMapper,
        SysTenantRefUserMapper sysTenantRefUserMapper, SysFileService sysFileService,
        @Autowired(required = false) @Nullable List<IUserSignConfiguration> iUserSignConfigurationList,
        SysOtherAppMapper sysOtherAppMapper, SysUserSingleSignInMapper sysUserSingleSignInMapper,
        SingleSignInProperties singleSignInProperties) {

        SignUtil.sysUserInfoMapper = sysUserInfoMapper;
        SignUtil.sysUserMapper = sysUserMapper;
        SignUtil.redissonClient = redissonClient;
        SignUtil.securityProperties = securityProperties;
        SignUtil.sysRoleRefUserMapper = sysRoleRefUserMapper;
        SignUtil.sysDeptRefUserMapper = sysDeptRefUserMapper;
        SignUtil.sysPostRefUserMapper = sysPostRefUserMapper;
        SignUtil.sysTenantRefUserMapper = sysTenantRefUserMapper;
        SignUtil.sysFileService = sysFileService;
        SignUtil.iUserSignConfigurationList = iUserSignConfigurationList;
        SignUtil.sysOtherAppMapper = sysOtherAppMapper;
        SignUtil.sysUserSingleSignInMapper = sysUserSingleSignInMapper;
        SignUtil.singleSignInProperties = singleSignInProperties;

    }

    /**
     * 发送验证码
     *
     * @param mustExist 是否必须存在，如果为 null，则，不存在和 存在都不会报错，例如：手机验证码注册并登录时
     */
    public static String sendCode(String key, @Nullable LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper,
        @Nullable Boolean mustExist, IBizCode iBizCode, Consumer<String> consumer, @Nullable Long tenantId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        // 检查：账户是否存在
        checkAccountExistWillError(lambdaQueryChainWrapper, mustExist, iBizCode, tenantId);

        String code = CodeUtil.getCode();

        consumer.accept(code); // 进行额外的处理

        // 保存到 redis中，设置 10分钟过期
        redissonClient.getBucket(key).set(code, Duration.ofMillis(BaseConstant.LONG_CODE_EXPIRE_TIME));

        return BaseBizCodeEnum.SEND_OK;

    }

    /**
     * 发送验证码：统一登录
     *
     * @param mustExist 是否必须存在，如果为 null，则，不存在和 存在都不会报错，例如：手机验证码注册并登录时
     */
    public static String sendCodeForSingle(String account, @Nullable Boolean mustExist, String errorMsg,
        Consumer<String> consumer, BaseRedisKeyEnum baseRedisKeyEnum) {

        // 判断是否存在
        boolean exists = false;

        if (BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_PHONE.equals(baseRedisKeyEnum)) {

            Long smsConfigurationId = singleSignInProperties.getSmsConfigurationId();

            if (smsConfigurationId == null) {
                ApiResultVO.errorMsg("操作失败：暂未配置手机验证码统一登录，请刷新重试");
            }

            exists = ChainWrappers.lambdaQueryChain(sysUserSingleSignInMapper)
                .eq(SysUserSingleSignInDO::getPhone, account).exists();

        } else if (BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_EMAIL.equals(baseRedisKeyEnum)) {

            Long emailConfigurationId = singleSignInProperties.getEmailConfigurationId();

            if (emailConfigurationId == null) {
                ApiResultVO.errorMsg("操作失败：暂未配置邮箱验证码统一登录，请刷新重试");
            }

            exists = ChainWrappers.lambdaQueryChain(sysUserSingleSignInMapper)
                .eq(SysUserSingleSignInDO::getEmail, account).exists();

        } else {

            ApiResultVO.error(BaseBizCodeEnum.API_RESULT_SYS_ERROR, baseRedisKeyEnum.name());

        }

        String key = baseRedisKeyEnum + account;

        if (mustExist == null) {

        } else if (mustExist) {

            if (!exists) {
                ApiResultVO.error(errorMsg, account);
            }

        } else {

            if (exists) {
                ApiResultVO.error(errorMsg, account);
            }

        }

        String code = CodeUtil.getCode();

        consumer.accept(code); // 进行额外的处理

        // 保存到 redis中，设置 10分钟过期
        redissonClient.getBucket(key).set(code, Duration.ofMillis(BaseConstant.LONG_CODE_EXPIRE_TIME));

        return BaseBizCodeEnum.SEND_OK;

    }

    /**
     * 检查：账号是否存在
     *
     * @param mustExist 是否必须存在，如果为 null，则，不存在和 存在都不会报错，例如：手机验证码注册并登录时
     */
    @SneakyThrows
    public static void checkAccountExistWillError(@Nullable LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper,
        @Nullable Boolean mustExist, IBizCode iBizCode, @Nullable Long tenantId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        // 判断是否存在
        boolean exists;

        if (lambdaQueryChainWrapper == null) {

            exists = true;

        } else {

            exists = lambdaQueryChainWrapper.eq(BaseEntityNoIdSuper::getTenantId, tenantId).exists();

        }

        if (mustExist == null) {

        } else if (mustExist) {

            if (!exists) {
                ApiResultVO.error(iBizCode);
            }

        } else {

            if (exists) {
                ApiResultVO.error(iBizCode);
            }

        }

    }

    /**
     * 获取账户信息，并执行发送验证码操作
     */
    public static String getAccountAndSendCode(Enum<? extends IRedisKey> redisKeyEnum,
        VoidFunc2<String, String> voidFunc2) {

        String account = getAccountByIdAndRedisKeyEnum(redisKeyEnum, UserUtil.getCurrentUserIdNotAdmin());

        if (BaseRedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum)) {

            if (StrUtil.isBlank(account)) {

                ApiResultVO.error(BaseBizCodeEnum.THIS_OPERATION_CANNOT_BE_PERFORMED_WITHOUT_BINDING_AN_EMAIL_ADDRESS);

            }

        } else if (BaseRedisKeyEnum.PRE_PHONE.equals(redisKeyEnum)) {

            if (StrUtil.isBlank(account)) {

                ApiResultVO
                    .error(BaseBizCodeEnum.THERE_IS_NO_BOUND_MOBILE_PHONE_NUMBER_SO_THIS_OPERATION_CANNOT_BE_PERFORMED);

            }

        }

        String code = CodeUtil.getCode();

        // 保存到 redis中，设置 10分钟过期
        redissonClient.getBucket(redisKeyEnum + account).set(code,
            Duration.ofMillis(BaseConstant.LONG_CODE_EXPIRE_TIME));

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

        String paramValue = SysParamUtil.getValueByUuid(ParamConstant.RSA_PRIVATE_KEY_UUID, tenantId); // 获取非对称 私钥
        password = MyRsaUtil.rsaDecrypt(password, paramValue);
        originPassword = MyRsaUtil.rsaDecrypt(originPassword, paramValue);

        if (BooleanUtil.isFalse(ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, originPassword))) {

            ApiResultVO.error(BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常

        }

        String key = redisKeyEnum + account;

        String finalPassword = password;

        RBucket<String> bucket = redissonClient.getBucket(key);

        boolean checkCodeFlag =
            BaseRedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum) || BaseRedisKeyEnum.PRE_PHONE.equals(redisKeyEnum);

        return RedissonUtil.doLock(key, () -> {

            if (checkCodeFlag) {
                CodeUtil.checkCode(code, bucket.get()); // 检查 code是否正确
            }

            // 检查：注册的登录账号是否存在
            boolean exist = accountIsExists(redisKeyEnum, account, null, tenantId, null);

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
    @NotNull
    public static SysUserDO insertUser(String password, Map<Enum<? extends IRedisKey>, String> accountMap,
        boolean checkPasswordBlank, @Nullable SysUserInfoDO tempSysUserInfoDO, Boolean enableFlag,
        @Nullable Long tenantId) {

        // 获取：SysUserDO对象
        SysUserDO sysUserDO = insertUserGetSysUserDO(password, accountMap, checkPasswordBlank, enableFlag, tenantId);

        return TransactionUtil.exec(() -> {

            sysUserMapper.insert(sysUserDO); // 保存：用户

            SysUserInfoDO sysUserInfoDO = new SysUserInfoDO();

            sysUserInfoDO.setId(sysUserDO.getId());

            sysUserInfoDO.setTenantId(sysUserDO.getTenantId()); // 设置：租户 id

            sysUserInfoDO.setCreateTime(sysUserDO.getCreateTime()); // 设置：创建时间

            sysUserInfoDO.setDelFlag(sysUserDO.getDelFlag());

            sysUserInfoDO.setEnableFlag(sysUserDO.getEnableFlag());

            sysUserInfoDO.setLastActiveTime(sysUserDO.getCreateTime());

            if (tempSysUserInfoDO == null) {

                sysUserInfoDO.setNickname(NicknameUtil.getRandomNickname());
                sysUserInfoDO.setBio("");

                sysUserInfoDO.setAvatarFileId(-1L);

                sysUserInfoDO.setSignUpType(RequestUtil.getRequestCategoryEnum());

                sysUserInfoDO.setLastIp(RequestUtil.getIp());

            } else {

                sysUserInfoDO.setNickname(
                    MyEntityUtil.getNotNullStr(tempSysUserInfoDO.getNickname(), NicknameUtil.getRandomNickname()));

                sysUserInfoDO.setBio(MyEntityUtil.getNotNullStr(tempSysUserInfoDO.getBio()));

                sysUserInfoDO.setAvatarFileId(MyEntityUtil.getNotNullLong(tempSysUserInfoDO.getAvatarFileId()));

                sysUserInfoDO.setSignUpType(MyEntityUtil.getNotNullObject(tempSysUserInfoDO.getSignUpType(),
                    RequestUtil.getRequestCategoryEnum()));

                sysUserInfoDO.setLastIp(MyEntityUtil.getNotNullStr(tempSysUserInfoDO.getLastIp(), RequestUtil.getIp()));

            }

            sysUserInfoDO.setLastRegion(Ip2RegionUtil.getRegion(sysUserInfoDO.getLastIp()));

            sysUserInfoMapper.insert(sysUserInfoDO); // 保存：用户基本信息

            if (CollUtil.isNotEmpty(iUserSignConfigurationList)) {

                for (IUserSignConfiguration item : iUserSignConfigurationList) {

                    item.signUp(sysUserDO.getId(), sysUserDO.getTenantId()); // 添加：用户额外的数据

                }

            }

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

        sysUserDO.setParentId(BaseConstant.TOP_PARENT_ID);

        sysUserDO.setDelFlag(false);
        sysUserDO.setRemark("");

        sysUserDO.setEmail("");
        sysUserDO.setSignInName("");
        sysUserDO.setPhone("");
        sysUserDO.setWxOpenId("");
        sysUserDO.setWxAppId("");
        sysUserDO.setWxUnionId("");

        for (Map.Entry<Enum<? extends IRedisKey>, String> item : accountMap.entrySet()) {

            if (BaseRedisKeyEnum.PRE_EMAIL.equals(item.getKey())) {

                sysUserDO.setEmail(item.getValue());

            } else if (BaseRedisKeyEnum.PRE_SIGN_IN_NAME.equals(item.getKey())) {

                sysUserDO.setSignInName(item.getValue());

            } else if (BaseRedisKeyEnum.PRE_PHONE.equals(item.getKey())) {

                sysUserDO.setPhone(item.getValue());

            } else if (BaseRedisKeyEnum.PRE_WX_APP_ID.equals(item.getKey())) {

                sysUserDO.setWxAppId(item.getValue());

            } else if (BaseRedisKeyEnum.PRE_WX_OPEN_ID.equals(item.getKey())) {

                sysUserDO.setWxOpenId(item.getValue());

            } else if (BaseRedisKeyEnum.PRE_WX_UNION_ID.equals(item.getKey())) {

                sysUserDO.setWxUnionId(item.getValue());

            }

        }

        sysUserDO.setPassword(PasswordConvertUtil.convert(password, checkPasswordBlank));

        tenantId = SysTenantUtil.getTenantId(tenantId);

        sysUserDO.setTenantId(tenantId); // 设置：租户 id

        return sysUserDO;

    }

    /**
     * 直接通过账号登录 注意：这是一个高风险方法，调用时，请确认账号来源的可靠性！
     *
     * @param consumer 可以给 userDO对象，额外增加一些属性
     */
    @NotNull
    public static SignInVO signInAccount(LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper,
        Enum<? extends IRedisKey> redisKeyEnum, String account, Supplier<SysUserInfoDO> sysUserInfoDOSupplier,
        @Nullable Long tenantId, @Nullable Consumer<Map<Enum<? extends IRedisKey>, String>> consumer,
        @Nullable CallBack<SysUserDO> sysUserDOCallBack) {

        if (StrUtil.isBlank(account)) {
            ApiResultVO.errorMsg("操作失败：账号信息为空");
        }

        String key = redisKeyEnum + account;

        return RedissonUtil.doLock(key, () -> {

            // 登录时，获取账号信息
            SysUserDO sysUserDO = signInGetSysUserDO(lambdaQueryChainWrapper, false, tenantId);

            if (sysUserDO == null) {

                // 如果登录的账号不存在，则进行新增
                Map<Enum<? extends IRedisKey>, String> accountMap = MapUtil.newHashMap();

                accountMap.put(redisKeyEnum, account);

                if (consumer != null) {
                    consumer.accept(accountMap);
                }

                SysUserInfoDO tempSysUserInfoDO = null;

                if (sysUserInfoDOSupplier != null) {

                    tempSysUserInfoDO = sysUserInfoDOSupplier.get();

                }

                sysUserDO = SignUtil.insertUser(null, accountMap, false, tempSysUserInfoDO, null, tenantId);

            }

            if (sysUserDOCallBack != null) {

                sysUserDOCallBack.setValue(sysUserDO); // 回调该对象

            }

            // 登录时，获取：jwt
            return signInGetJwt(sysUserDO);

        });

    }

    /**
     * 验证码登录
     */
    @Nullable
    public static SignInVO signInCode(LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper, String code,
        Enum<? extends IRedisKey> redisKeyEnum, String account, @Nullable Long tenantId,
        @Nullable VoidFunc0 voidFunc0) {

        // 登录时，获取账号信息
        SysUserDO sysUserDO = signInGetSysUserDO(lambdaQueryChainWrapper, false, tenantId);

        String key = redisKeyEnum + account;

        return RedissonUtil.doLock(key, () -> {

            // 执行
            return doSignInCode(code, redisKeyEnum, account, tenantId, voidFunc0, key, sysUserDO);

        });

    }

    /**
     * 执行：验证码登录
     */
    @SneakyThrows
    @Nullable
    private static SignInVO doSignInCode(String code, Enum<? extends IRedisKey> redisKeyEnum, String account,
        @Nullable Long tenantId, @Nullable VoidFunc0 voidFunc0, String key, SysUserDO sysUserDoTemp) {

        RBucket<String> bucket = redissonClient.getBucket(key);

        CodeUtil.checkCode(code, bucket.get()); // 检查 code是否正确

        SysUserDO sysUserDO = sysUserDoTemp;

        if (sysUserDO == null) {

            if (voidFunc0 != null) {

                voidFunc0.call();

            }

            // 如果登录的账号不存在，则进行新增
            Map<Enum<? extends IRedisKey>, String> accountMap = MapUtil.newHashMap();
            accountMap.put(redisKeyEnum, account);

            sysUserDO = SignUtil.insertUser(null, accountMap, false, null, null, tenantId);

        }

        bucket.delete(); // 删除：验证码

        // 登录时，获取：jwt
        return signInGetJwt(sysUserDO);

    }

    /**
     * 账号密码登录
     */
    public static SignInVO signInPassword(LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper, String password,
        String account, @Nullable Long tenantId) {

        // 密码解密
        password = MyRsaUtil.rsaDecrypt(password, tenantId);

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
    public static SignInVO signInGetJwt(SysUserDO sysUserDO) {

        // 校验密码，成功之后，再判断是否被冻结，免得透露用户被封号的信息
        if (BooleanUtil.isFalse(sysUserDO.getEnableFlag())) {

            ApiResultVO.error(BaseBizCodeEnum.ACCOUNT_IS_DISABLED);

        }

        String jwtSecretSuf = UserUtil.getJwtSecretSuf(sysUserDO.getId());

        // 颁发，并返回 jwt
        return MyJwtUtil.generateJwt(sysUserDO.getId(), jwtSecretSuf, payloadMap -> {

            payloadMap.set(MyJwtUtil.PAYLOAD_MAP_WX_APP_ID_KEY, sysUserDO.getWxAppId());

            payloadMap.set(MyJwtUtil.PAYLOAD_MAP_WX_OPEN_ID_KEY, sysUserDO.getWxOpenId());

        }, sysUserDO.getTenantId());

    }

    /**
     * 登录时，获取：账号信息
     */
    @Nullable
    private static SysUserDO signInGetSysUserDO(LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper,
        boolean errorFlag, @Nullable Long tenantId) {

        tenantId = SysTenantUtil.getTenantId(tenantId);

        lambdaQueryChainWrapper.eq(BaseEntityNoId::getTenantId, tenantId);

        SysUserDO sysUserDO = lambdaQueryChainWrapper.one();

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
            redissonClient.<Long, String>getMap(BaseRedisKeyEnum.PRE_TOO_MANY_PASSWORD_ERROR.name()).get(userId);

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
            redissonClient.getAtomicLong(BaseRedisKeyEnum.PRE_PASSWORD_ERROR_COUNT.name() + ":" + userId);

        long count = atomicLong.incrementAndGet(); // 次数 + 1

        if (count == 1) {
            atomicLong.expire(Duration.ofMillis(BaseConstant.DAY_30_EXPIRE_TIME)); // 等于 1表示，是第一次访问，则设置过期时间
        }

        if (count > 10) {

            // 超过十次密码错误，则封禁账号，下次再错误，则才会提示
            redissonClient.<Long, String>getMap(BaseRedisKeyEnum.PRE_TOO_MANY_PASSWORD_ERROR.name()).put(userId,
                "密码错误次数过多，被锁定的账号");

            atomicLong.delete(); // 清空错误次数

        }

    }

    /**
     * 修改密码
     */
    public static String updatePassword(String newPasswordTemp, String originNewPasswordTemp,
        Enum<? extends IRedisKey> redisKeyEnum, String code, String oldPassword) {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        String paramValue = SysParamUtil.getValueByUuid(ParamConstant.RSA_PRIVATE_KEY_UUID, currentTenantIdDefault); // 获取非对称
                                                                                                                     // 私钥

        if (BaseRedisKeyEnum.PRE_SIGN_IN_NAME.equals(redisKeyEnum)) {

            // 检查：当前密码是否正确
            checkCurrentPasswordWillError(oldPassword, currentUserIdNotAdmin, paramValue, null, currentTenantIdDefault);

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
            boolean checkCodeFlag = getDeleteRedisFlag(redisKeyEnum);

            if (checkCodeFlag) {
                CodeUtil.checkCode(code, bucket.get()); // 检查 code是否正确
            }

            SysUserDO sysUserDO = new SysUserDO();

            sysUserDO.setId(currentUserIdNotAdmin);

            sysUserDO.setPassword(PasswordConvertUtil.convert(newPassword, true));

            return TransactionUtil.exec(() -> {

                sysUserMapper.updateById(sysUserDO); // 保存：用户

                RedissonUtil.batch((batch) -> {

                    // 移除密码错误次数相关
                    batch.getBucket(BaseRedisKeyEnum.PRE_PASSWORD_ERROR_COUNT.name() + ":" + currentUserIdNotAdmin)
                        .deleteAsync();

                    batch.getMap(BaseRedisKeyEnum.PRE_TOO_MANY_PASSWORD_ERROR.name())
                        .removeAsync(currentUserIdNotAdmin);

                    if (checkCodeFlag) {
                        batch.getBucket(key).deleteAsync(); // 删除：验证码
                    }

                });

                UserUtil.setJwtSecretSuf(currentUserIdNotAdmin); // 设置：jwt秘钥后缀

                return BaseBizCodeEnum.OK;

            });

        });

    }

    /**
     * 检查：当前密码是否正确
     *
     * @param checkPassword 前端传过来的密码
     * @param userPassword 用户，数据库里面的密码
     */
    public static void checkCurrentPasswordWillError(String checkPassword, Long currentUserIdNotAdmin,
        @Nullable String paramValue, @Nullable String userPassword, @Nullable Long tenantId) {

        if (StrUtil.isBlank(checkPassword)) {

            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);

        }

        // 判断：密码错误次数过多，是否被冻结
        checkTooManyPasswordError(currentUserIdNotAdmin);

        if (StrUtil.isBlank(userPassword)) {

            SysUserDO sysUserDO = ChainWrappers.lambdaQueryChain(sysUserMapper)
                .eq(BaseEntity::getId, currentUserIdNotAdmin).select(SysUserDO::getPassword).one();

            if (sysUserDO == null) {
                ApiResultVO.error(BizCodeEnum.USER_DOES_NOT_EXIST);
            }

            userPassword = sysUserDO.getPassword();

        }

        if (paramValue == null) {
            checkPassword = MyRsaUtil.rsaDecrypt(checkPassword, tenantId);
        } else {
            checkPassword = MyRsaUtil.rsaDecrypt(checkPassword, paramValue);
        }

        if (BooleanUtil.isFalse(PasswordConvertUtil.match(userPassword, checkPassword))) {

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

        if (BaseRedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum)) {

            return sysUserDO.getEmail();

        } else if (BaseRedisKeyEnum.PRE_SIGN_IN_NAME.equals(redisKeyEnum)) {

            return sysUserDO.getSignInName();

        } else if (BaseRedisKeyEnum.PRE_PHONE.equals(redisKeyEnum)) {

            return sysUserDO.getPhone();

        } else if (BaseRedisKeyEnum.PRE_WX_OPEN_ID.equals(redisKeyEnum)) {

            return sysUserDO.getWxOpenId();

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

        if (BaseRedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.select(SysUserDO::getEmail);

        } else if (BaseRedisKeyEnum.PRE_SIGN_IN_NAME.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.select(SysUserDO::getSignInName);

        } else if (BaseRedisKeyEnum.PRE_PHONE.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.select(SysUserDO::getPhone);

        } else if (BaseRedisKeyEnum.PRE_WX_OPEN_ID.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.select(SysUserDO::getWxOpenId, SysUserDO::getWxAppId);

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
     *
     * @param oldRedisKeyEnum 这个参数不能为 null
     * @param newRedisKeyEnum 这个参数不能为 null
     */
    public static String updateAccount(String oldCode, String newCode, Enum<? extends IRedisKey> oldRedisKeyEnum,
        Enum<? extends IRedisKey> newRedisKeyEnum, String newAccount, String currentPassword, String appId) {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (BaseRedisKeyEnum.PRE_SIGN_IN_NAME.equals(newRedisKeyEnum)) {
            checkCurrentPasswordWillError(currentPassword, currentUserIdNotAdmin, null, null, currentTenantIdDefault);
        }

        // 获取：旧的账号
        String oldAccount = getAccountByIdAndRedisKeyEnum(oldRedisKeyEnum, currentUserIdNotAdmin);

        String oldKey = oldRedisKeyEnum + oldAccount;
        String newKey = newRedisKeyEnum + newAccount;

        return RedissonUtil.doMultiLock(null, CollUtil.newHashSet(oldKey, newKey), () -> {

            RBucket<String> oldBucket = redissonClient.getBucket(oldKey);

            if (BaseRedisKeyEnum.PRE_EMAIL.equals(oldRedisKeyEnum)) {

                // 检查 code是否正确
                CodeUtil.checkCode(oldCode, oldBucket.get(), "操作失败：请先获取旧邮箱的验证码", "旧邮箱的验证码有误，请重新输入");

            } else if (BaseRedisKeyEnum.PRE_PHONE.equals(oldRedisKeyEnum)) {

                // 检查 code是否正确
                CodeUtil.checkCode(oldCode, oldBucket.get(), "操作失败：请先获取旧手机号码的验证码", "旧手机号码的验证码有误，请重新输入");

            }

            RBucket<String> newBucket = redissonClient.getBucket(newKey);

            if (BaseRedisKeyEnum.PRE_EMAIL.equals(newRedisKeyEnum)) {

                // 检查 code是否正确
                CodeUtil.checkCode(newCode, newBucket.get(), "操作失败：请先获取新邮箱的验证码", "新邮箱的验证码有误，请重新输入");

            } else if (BaseRedisKeyEnum.PRE_PHONE.equals(newRedisKeyEnum)) {

                // 检查 code是否正确
                CodeUtil.checkCode(newCode, newBucket.get(), "操作失败：请先获取新手机号码的验证码", "新手机号码的验证码有误，请重新输入");

            }

            // 检查：新的登录账号是否存在
            boolean exist = accountIsExists(newRedisKeyEnum, newAccount, null, currentTenantIdDefault, appId);

            // 是否删除：redis中的验证码
            boolean oldDeleteRedisFlag = getDeleteRedisFlag(oldRedisKeyEnum);

            boolean newDeleteRedisFlag = getDeleteRedisFlag(newRedisKeyEnum);

            if (exist) {

                if (newDeleteRedisFlag) {
                    newBucket.delete();
                }

                if (BaseRedisKeyEnum.PRE_EMAIL.equals(newRedisKeyEnum)) {

                    ApiResultVO.errorMsg("操作失败：邮箱已被人占用");

                } else if (BaseRedisKeyEnum.PRE_PHONE.equals(newRedisKeyEnum)) {

                    ApiResultVO.errorMsg("操作失败：手机号码已被人占用");

                } else {

                    ApiResultVO.errorMsg("操作失败：已被人占用");

                }

            }

            SysUserDO sysUserDO = new SysUserDO();

            sysUserDO.setId(currentUserIdNotAdmin);

            // 通过：BaseRedisKeyEnum，设置：账号
            setSysUserDOAccountByRedisKeyEnum(newRedisKeyEnum, newAccount, sysUserDO, appId);

            return TransactionUtil.exec(() -> {

                sysUserMapper.updateById(sysUserDO); // 更新：用户

                if (oldDeleteRedisFlag) {

                    // 删除：验证码
                    oldBucket.delete();

                }

                if (newDeleteRedisFlag) {

                    // 删除：验证码
                    newBucket.delete();

                }

                UserUtil.setJwtSecretSuf(currentUserIdNotAdmin); // 设置：jwt秘钥后缀

                return BaseBizCodeEnum.OK;

            });

        });

    }

    /**
     * 是否删除：redis中的验证码
     */
    private static boolean getDeleteRedisFlag(Enum<? extends IRedisKey> redisKeyEnum) {

        return BaseRedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum) || BaseRedisKeyEnum.PRE_PHONE.equals(redisKeyEnum);

    }

    /**
     * 通过：BaseRedisKeyEnum，设置：账号
     */
    private static void setSysUserDOAccountByRedisKeyEnum(Enum<? extends IRedisKey> redisKeyEnum, String newAccount,
        SysUserDO sysUserDO, String appId) {

        if (BaseRedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum)) {

            sysUserDO.setEmail(newAccount);

        } else if (BaseRedisKeyEnum.PRE_SIGN_IN_NAME.equals(redisKeyEnum)) {

            sysUserDO.setSignInName(newAccount);

        } else if (BaseRedisKeyEnum.PRE_PHONE.equals(redisKeyEnum)) {

            sysUserDO.setPhone(newAccount);

        } else if (BaseRedisKeyEnum.PRE_WX_OPEN_ID.equals(redisKeyEnum)) {

            sysUserDO.setWxAppId(appId);
            sysUserDO.setWxOpenId(newAccount);

        } else {

            ApiResultVO.sysError();

        }

    }

    /**
     * 检查登录账号是否存在
     */
    public static boolean accountIsExists(Enum<? extends IRedisKey> redisKeyEnum, String newAccount, @Nullable Long id,
        @Nullable Long tenantId, String appId) {

        // 如果是：统一登录，设置微信时
        if (BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_WX.equals(redisKeyEnum)) {

            return ChainWrappers.lambdaQueryChain(sysUserSingleSignInMapper)
                .eq(SysUserSingleSignInDO::getWxAppId, appId).eq(SysUserSingleSignInDO::getWxOpenId, newAccount)
                .exists();

        } else if (BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_PHONE.equals(redisKeyEnum)) {

            // 如果是：统一登录，设置手机验证码登录时
            return ChainWrappers.lambdaQueryChain(sysUserSingleSignInMapper)
                .eq(SysUserSingleSignInDO::getPhone, newAccount).exists();

        }

        tenantId = SysTenantUtil.getTenantId(tenantId);

        LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper = ChainWrappers.lambdaQueryChain(sysUserMapper)
            .ne(id != null, BaseEntity::getId, id).eq(!BaseRedisKeyEnum.PRE_WX_OPEN_ID.equals(redisKeyEnum),
                // 当检查微信账号时，要求：全部租户的微信账号，不能重复
                BaseEntityNoId::getTenantId, tenantId);

        if (BaseRedisKeyEnum.PRE_EMAIL.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.eq(SysUserDO::getEmail, newAccount);

        } else if (BaseRedisKeyEnum.PRE_SIGN_IN_NAME.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.eq(SysUserDO::getSignInName, newAccount);

        } else if (BaseRedisKeyEnum.PRE_PHONE.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.eq(SysUserDO::getPhone, newAccount);

        } else if (BaseRedisKeyEnum.PRE_WX_OPEN_ID.equals(redisKeyEnum)) {

            lambdaQueryChainWrapper.eq(SysUserDO::getWxAppId, appId).eq(SysUserDO::getWxOpenId, newAccount);

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
        LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper, @Nullable Long tenantId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        String paramValue = SysParamUtil.getValueByUuid(ParamConstant.RSA_PRIVATE_KEY_UUID, tenantId); // 获取非对称 私钥
        String newPassword = MyRsaUtil.rsaDecrypt(newPasswordTemp, paramValue);
        String originNewPassword = MyRsaUtil.rsaDecrypt(originNewPasswordTemp, paramValue);

        if (BooleanUtil.isFalse(ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, originNewPassword))) {
            ApiResultVO.error(BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常
        }

        String key = redisKeyEnum.name() + account;

        Long finalTenantId = tenantId;

        return RedissonUtil.doLock(key, () -> {

            RBucket<String> bucket = redissonClient.getBucket(key);

            CodeUtil.checkCode(code, bucket.get()); // 检查 code是否正确

            // 获取：用户 id
            SysUserDO sysUserDO = lambdaQueryChainWrapper.eq(BaseEntityNoIdSuper::getTenantId, finalTenantId)
                .select(BaseEntity::getId).one();

            if (sysUserDO == null) {

                bucket.delete(); // 删除：验证码
                ApiResultVO.error(BizCodeEnum.USER_DOES_NOT_EXIST);

            }

            sysUserDO.setPassword(PasswordConvertUtil.convert(newPassword, true));

            return TransactionUtil.exec(() -> {

                sysUserMapper.updateById(sysUserDO); // 保存：用户

                RedissonUtil.batch((batch) -> {

                    // 移除密码错误次数相关
                    batch.getBucket(BaseRedisKeyEnum.PRE_PASSWORD_ERROR_COUNT.name() + ":" + sysUserDO.getId())
                        .deleteAsync();

                    batch.getMap(BaseRedisKeyEnum.PRE_TOO_MANY_PASSWORD_ERROR.name()).removeAsync(sysUserDO.getId());

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
    public static String signDelete(@Nullable String code, Enum<? extends IRedisKey> redisKeyEnum,
        String currentPassword, @Nullable Long userId) {

        if (userId == null) {
            userId = UserUtil.getCurrentUserIdNotAdmin();
        }

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (StrUtil.isNotBlank(currentPassword)) {
            checkCurrentPasswordWillError(currentPassword, userId, null, null, currentTenantIdDefault);
        }

        // 通过：redisKeyEnum，获取账号
        String account = getAccountByIdAndRedisKeyEnum(redisKeyEnum, userId);

        String key = redisKeyEnum + account;

        Long finalUserId = userId;

        return RedissonUtil.doLock(key, () -> {

            RBucket<String> bucket = redissonClient.getBucket(key);

            // 是否：检查验证码
            boolean deleteRedisFlag = getDeleteRedisFlag(redisKeyEnum);

            if (deleteRedisFlag) {
                CodeUtil.checkCode(code, bucket.get()); // 检查 code是否正确
            }

            return TransactionUtil.exec(() -> {

                // 执行：账号注销
                doSignDelete(CollUtil.newHashSet(finalUserId));

                if (deleteRedisFlag) {
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
        SysTenantUtil.checkIllegal(userIdSet, tenantIdSet -> ChainWrappers.lambdaQueryChain(sysUserMapper)
            .in(BaseEntity::getId, userIdSet).in(BaseEntityNoId::getTenantId, tenantIdSet).count());

        // 找到用户：拥有的文件
        List<SysFileDO> sysFileDOList =
            sysFileService.lambdaQuery().in(SysFileDO::getBelongId, userIdSet).select(BaseEntity::getId).list();

        Set<Long> fileIdSet = sysFileDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        TransactionUtil.exec(() -> {

            doSignDeleteSub(userIdSet, true); // 删除子表数据

            sysUserMapper.deleteBatchIds(userIdSet); // 直接：删除用户

            // 删除：用户的文件
            SysFileUtil.removeByFileIdSet(fileIdSet, false);

            for (Long item : userIdSet) {

                // 删除：jwt后缀
                UserUtil.removeJwtSecretSuf(item);

            }

        });

    }

    /**
     * 执行：账号注销，删除子表数据
     *
     * @param deleteFlag true 账号注销，需要删除用户相关的数据 false 修改用户的基础绑定信息
     */
    public static void doSignDeleteSub(Set<Long> idSet, boolean deleteFlag) {

        TransactionUtil.exec(() -> {

            if (deleteFlag) {

                if (CollUtil.isNotEmpty(iUserSignConfigurationList) && CollUtil.isNotEmpty(idSet)) {

                    for (IUserSignConfiguration item : iUserSignConfigurationList) {

                        item.delete(idSet); // 移除：用户额外的数据

                    }

                }

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
     *
     * @param accountRedisKeyEnum 不能为 null
     */
    public static String bindAccount(@Nullable String code, Enum<? extends IRedisKey> accountRedisKeyEnum,
        String account, String appId, @Nullable String codeKey, @Nullable String password) {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (StrUtil.isNotBlank(password)) {

            // 检查密码是否正确
            checkCurrentPasswordWillError(password, currentUserIdNotAdmin, null, null, currentTenantIdDefault);

        }

        String accountKey = accountRedisKeyEnum + account;

        boolean codeKeyBlankFlag = StrUtil.isBlank(codeKey);

        Set<String> nameSet = CollUtil.newHashSet(accountKey);

        if (!codeKeyBlankFlag) {

            nameSet.add(codeKey);

        }

        // 是否是：设置统一登录
        boolean singleSignInFlag = BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_WX.equals(accountRedisKeyEnum)
            || BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_PHONE.equals(accountRedisKeyEnum);

        if (singleSignInFlag) {

            nameSet.add(BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN.name() + currentUserIdNotAdmin); // 锁定该用户

        }

        return RedissonUtil.doMultiLock(null, nameSet, () -> {

            RBucket<String> bucket = redissonClient.getBucket(codeKeyBlankFlag ? accountKey : codeKey);

            // 检查：绑定的登录账号是否存在
            boolean exist = accountIsExists(accountRedisKeyEnum, account, null, currentTenantIdDefault, appId);

            boolean deleteRedisFlag = StrUtil.isNotBlank(code);

            if (exist) {

                if (deleteRedisFlag) {

                    bucket.delete();

                }

                ApiResultVO.errorMsg("操作失败：账号已被绑定，请重试");

            }

            if (deleteRedisFlag) {

                CodeUtil.checkCode(code, bucket.get()); // 检查 code是否正确

            }

            // 如果是：微信统一登录绑定时
            if (singleSignInFlag) {

                // 处理
                return bindAccountForSingleSignIn(account, appId, currentUserIdNotAdmin, currentTenantIdDefault,
                    deleteRedisFlag, bucket, accountRedisKeyEnum, null);

            } else {

                SysUserDO sysUserDO = new SysUserDO();

                // 通过：BaseRedisKeyEnum，设置：账号
                setSysUserDOAccountByRedisKeyEnum(accountRedisKeyEnum, account, sysUserDO, appId);

                sysUserDO.setId(currentUserIdNotAdmin);

                return TransactionUtil.exec(() -> {

                    sysUserMapper.updateById(sysUserDO); // 保存：用户

                    if (deleteRedisFlag) {

                        bucket.delete();

                    }

                    UserUtil.setJwtSecretSuf(currentUserIdNotAdmin); // 设置：jwt秘钥后缀

                    return BaseBizCodeEnum.OK;

                });

            }

        });

    }

    /**
     * 绑定统一登录账号
     *
     * @param singleSignInRedisKeyEnum 不能为 null
     */
    public static String bindAccountForSingle(@Nullable String singleSignInCode,
        Enum<? extends IRedisKey> singleSignInRedisKeyEnum, String singleSignInAccount, @Nullable String password,
        @Nullable String currentCode, @Nullable Enum<? extends IRedisKey> currentRedisKeyEnum) {

        if (!BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_PHONE.equals(singleSignInRedisKeyEnum)) {

            ApiResultVO.error(BaseBizCodeEnum.API_RESULT_SYS_ERROR, singleSignInRedisKeyEnum);

        }

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (StrUtil.isNotBlank(password)) {

            // 检查密码是否正确
            checkCurrentPasswordWillError(password, currentUserIdNotAdmin, null, null, currentTenantIdDefault);

        }

        Set<String> nameSet =
            CollUtil.newHashSet(BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN.name() + currentUserIdNotAdmin);

        String currentKey = null;

        String singleSignInKey = singleSignInRedisKeyEnum.name() + singleSignInAccount;

        nameSet.add(singleSignInKey);

        if (StrUtil.isNotBlank(currentCode)) {

            if (BaseRedisKeyEnum.PRE_PHONE.equals(currentRedisKeyEnum)) {

                String currentUserPhoneNotAdmin = UserUtil.getCurrentUserPhoneNotAdmin();

                currentKey = currentRedisKeyEnum.name() + currentUserPhoneNotAdmin;

                nameSet.add(currentKey);

            } else if (BaseRedisKeyEnum.PRE_EMAIL.equals(currentRedisKeyEnum)) {

                String currentUserEmailNotAdmin = UserUtil.getCurrentUserEmailNotAdmin();

                currentKey = currentRedisKeyEnum.name() + currentUserEmailNotAdmin;

                nameSet.add(currentKey);

            } else {

                ApiResultVO.error(BaseBizCodeEnum.API_RESULT_SYS_ERROR, currentRedisKeyEnum);

            }

        }

        String finalCurrentKey = currentKey;

        return RedissonUtil.doMultiLock(null, nameSet, () -> {

            RBucket<String> currentBucket = null;

            if (StrUtil.isNotBlank(finalCurrentKey)) {

                currentBucket = redissonClient.getBucket(finalCurrentKey);

                if (BaseRedisKeyEnum.PRE_EMAIL.equals(currentRedisKeyEnum)) {

                    // 检查 code是否正确
                    CodeUtil.checkCode(currentCode, currentBucket.get(), "操作失败：请先获取当前邮箱的验证码", "当前邮箱的验证码有误，请重新输入");

                } else if (BaseRedisKeyEnum.PRE_PHONE.equals(currentRedisKeyEnum)) {

                    // 检查 code是否正确
                    CodeUtil.checkCode(currentCode, currentBucket.get(), "操作失败：请先获取当前手机号码的验证码", "当前手机号码的验证码有误，请重新输入");

                }

            }

            RBucket<String> singleSignInBucket = redissonClient.getBucket(singleSignInKey);

            if (BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_PHONE.equals(singleSignInRedisKeyEnum)) {

                // 检查 code是否正确
                CodeUtil.checkCode(singleSignInCode, singleSignInBucket.get(), "操作失败：请先获取统一登录的手机验证码",
                    "统一登录的手机验证码有误，请重新输入");

            }

            // 检查：新的统一登录账号是否存在
            boolean exist = true;

            if (BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_PHONE.equals(singleSignInRedisKeyEnum)) {

                exist = ChainWrappers.lambdaQueryChain(sysUserSingleSignInMapper)
                    .eq(SysUserSingleSignInDO::getPhone, singleSignInAccount).exists();

            }

            if (exist) {

                singleSignInBucket.delete();

                if (BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_PHONE.equals(singleSignInRedisKeyEnum)) {

                    ApiResultVO.errorMsg("操作失败：统一登录的手机号码已被人占用");

                } else {

                    ApiResultVO.errorMsg("操作失败：已被人占用");

                }

            }

            RBucket<String> finalCurrentBucket = currentBucket;

            // 设置：统一登录的账号
            return bindAccountForSingleSignIn(singleSignInAccount, null, currentUserIdNotAdmin, currentTenantIdDefault,
                true, singleSignInBucket, singleSignInRedisKeyEnum, () -> {

                    if (finalCurrentBucket != null) {

                        // 删除：验证码
                        finalCurrentBucket.delete();

                    }

                });

        });

    }

    /**
     * 处理：统一登录设置账号
     */
    @NotNull
    private static String bindAccountForSingleSignIn(String account, String appId, Long currentUserIdNotAdmin,
        Long currentTenantIdDefault, boolean deleteRedisFlag, @Nullable RBucket<String> bucket,
        Enum<? extends IRedisKey> accountRedisKeyEnum, VoidFunc0 voidFunc0) {

        SysUserSingleSignInDO sysUserSingleSignInDO = new SysUserSingleSignInDO();

        boolean singleSignInExists = ChainWrappers.lambdaQueryChain(sysUserSingleSignInMapper)
            .eq(SysUserSingleSignInDO::getId, currentUserIdNotAdmin).exists();

        sysUserSingleSignInDO.setId(currentUserIdNotAdmin);

        if (!singleSignInExists) {

            sysUserSingleSignInDO.setWxAppId("");
            sysUserSingleSignInDO.setWxOpenId("");
            sysUserSingleSignInDO.setPhone("");
            sysUserSingleSignInDO.setEmail("");

        }

        if (BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_WX.equals(accountRedisKeyEnum)) {

            sysUserSingleSignInDO.setWxAppId(appId);
            sysUserSingleSignInDO.setWxOpenId(account);

        } else if (BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_PHONE.equals(accountRedisKeyEnum)) {

            sysUserSingleSignInDO.setPhone(account);

        } else {

            ApiResultVO.sysError();

        }

        return TransactionUtil.exec(() -> {

            // 执行
            return doBindAccountForSingleSignIn(currentUserIdNotAdmin, currentTenantIdDefault, deleteRedisFlag, bucket,
                voidFunc0, singleSignInExists, sysUserSingleSignInDO);

        });

    }

    /**
     * 执行
     */
    @SneakyThrows
    @NotNull
    private static String doBindAccountForSingleSignIn(Long currentUserIdNotAdmin, Long currentTenantIdDefault,
        boolean deleteRedisFlag, @Nullable RBucket<String> bucket, VoidFunc0 voidFunc0, boolean singleSignInExists,
        SysUserSingleSignInDO sysUserSingleSignInDO) {

        if (singleSignInExists) {

            sysUserSingleSignInMapper.updateById(sysUserSingleSignInDO); // 更新

        } else {

            sysUserSingleSignInDO.setTenantId(currentTenantIdDefault);

            sysUserSingleSignInMapper.insert(sysUserSingleSignInDO); // 新增

        }

        if (deleteRedisFlag) {

            if (bucket != null) {

                bucket.delete();

            }

        }

        if (voidFunc0 != null) {

            voidFunc0.call();

        }

        UserUtil.setJwtSecretSuf(currentUserIdNotAdmin); // 设置：jwt秘钥后缀

        return BaseBizCodeEnum.OK;

    }

    /**
     * 检查：是否可以进行操作：敏感操作都需要调用此方法
     *
     * @param baseRedisKeyEnum 操作账户的类型：登录名，邮箱，微信，手机号
     * @param account 账号信息，一般情况为 null，只有：忘记密码，或者 验证码登录并注册 的时候，才会传值
     */
    public static void checkWillError(BaseRedisKeyEnum baseRedisKeyEnum, String account, @Nullable Long tenantId,
        String appId) {

        tenantId = SysTenantUtil.getTenantId(tenantId);

        Long userId = null;

        boolean accountBlankFlag = StrUtil.isBlank(account);

        if (accountBlankFlag) {
            userId = UserUtil.getCurrentUserIdNotAdmin();
        }

        // 敏感操作：
        // 1 设置或者修改：密码，登录名，邮箱，手机，微信
        // 2 忘记密码
        // 3 账户注销
        LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper = ChainWrappers.lambdaQueryChain(sysUserMapper)
            .eq(accountBlankFlag, BaseEntity::getId, userId).eq(BaseEntityNoIdSuper::getTenantId, tenantId);

        // 处理：lambdaQueryChainWrapper对象
        checkWillErrorHandleLambdaQueryChainWrapper(baseRedisKeyEnum, account, appId, lambdaQueryChainWrapper,
            accountBlankFlag);

        SysUserDO sysUserDO = lambdaQueryChainWrapper.one();

        if (sysUserDO == null) {
            return;
        }

        // 执行：检查
        checkWillErrorDoCheck(baseRedisKeyEnum, sysUserDO);

    }

    /**
     * 执行：检查
     */
    private static void checkWillErrorDoCheck(BaseRedisKeyEnum baseRedisKeyEnum, SysUserDO sysUserDO) {

        if (baseRedisKeyEnum.equals(BaseRedisKeyEnum.PRE_SIGN_IN_NAME)) { // 登录名

            // 必须有密码，并且，邮箱为空，手机为空，微信为空
            if (StrUtil.isBlank(sysUserDO.getPassword())) {
                ApiResultVO.errorMsg("操作失败：请设置密码之后再试");
            }

            if (StrUtil.isNotBlank(sysUserDO.getEmail())) {
                ApiResultVO.errorMsg("操作失败：请用邮箱验证码进行操作");
            }

            if (StrUtil.isNotBlank(sysUserDO.getWxAppId())) {
                ApiResultVO.errorMsg("操作失败：请用微信扫码进行操作");
            }

            if (StrUtil.isNotBlank(sysUserDO.getPhone())) {
                ApiResultVO.errorMsg("操作失败：请用手机验证码进行操作");
            }

        } else if (baseRedisKeyEnum.equals(BaseRedisKeyEnum.PRE_EMAIL)) { // 邮箱

            // 必须有密码，并且，手机为空，微信为空
            if (StrUtil.isBlank(sysUserDO.getPassword())) {
                ApiResultVO.errorMsg("操作失败：请设置密码之后再试");
            }

            if (StrUtil.isNotBlank(sysUserDO.getWxAppId())) {
                ApiResultVO.errorMsg("操作失败：请用微信扫码进行操作");
            }

            if (StrUtil.isNotBlank(sysUserDO.getPhone())) {
                ApiResultVO.errorMsg("操作失败：请用手机验证码进行操作");
            }

        } else if (baseRedisKeyEnum.equals(BaseRedisKeyEnum.PRE_WX_OPEN_ID)) { // 微信

            // 必须手机为空
            if (StrUtil.isNotBlank(sysUserDO.getPhone())) {
                ApiResultVO.errorMsg("操作失败：请用手机验证码进行操作");
            }

        } else if (baseRedisKeyEnum.equals(BaseRedisKeyEnum.PRE_PHONE)) { // 手机

            // 目前：手机支持任何操作

        }

    }

    /**
     * 处理：lambdaQueryChainWrapper对象
     */
    private static void checkWillErrorHandleLambdaQueryChainWrapper(BaseRedisKeyEnum baseRedisKeyEnum, String account,
        String appId, LambdaQueryChainWrapper<SysUserDO> lambdaQueryChainWrapper, boolean accountBlankFlag) {

        if (accountBlankFlag) {
            return;
        }

        if (baseRedisKeyEnum.equals(BaseRedisKeyEnum.PRE_SIGN_IN_NAME)) { // 登录名

            lambdaQueryChainWrapper.eq(SysUserDO::getSignInName, account);

        } else if (baseRedisKeyEnum.equals(BaseRedisKeyEnum.PRE_EMAIL)) { // 邮箱

            lambdaQueryChainWrapper.eq(SysUserDO::getEmail, account);

        } else if (baseRedisKeyEnum.equals(BaseRedisKeyEnum.PRE_WX_OPEN_ID)) { // 微信

            if (StrUtil.isBlank(appId)) {
                ApiResultVO.errorMsg(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg() + "：wxAppId" + "，请联系管理员");
            }

            lambdaQueryChainWrapper.eq(SysUserDO::getWxAppId, appId).eq(SysUserDO::getWxOpenId, account);

        } else if (baseRedisKeyEnum.equals(BaseRedisKeyEnum.PRE_PHONE)) { // 手机

            lambdaQueryChainWrapper.eq(SysUserDO::getPhone, account);

        } else {

            ApiResultVO.errorMsg(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg() + "：" + baseRedisKeyEnum.name() + "，请联系管理员");

        }

    }

    /**
     * 获取：微信，二维码地址
     */
    @SneakyThrows
    @Nullable
    public static GetQrCodeVO getQrCodeUrlWx(@Nullable Long tenantId, boolean getQrCodeUrlFlag,
        ISysQrCodeSceneType iSysQrCodeSceneType) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        Long finalTenantId = tenantId;

        // 执行
        return getQrCodeUrl(tenantId, getQrCodeUrlFlag, SysOtherAppTypeEnum.WX_OFFICIAL_ACCOUNT.getCode(),
            sysOtherAppDO -> {

                String accessToken = WxUtil.getAccessToken(finalTenantId, sysOtherAppDO.getAppId());

                Long qrCodeId = IdGeneratorUtil.nextId();

                String qrCodeUrl = WxUtil.getQrCodeUrl(accessToken, iSysQrCodeSceneType, qrCodeId.toString());

                return new GetQrCodeVO(qrCodeUrl, qrCodeId,
                    System.currentTimeMillis() + ((iSysQrCodeSceneType.getExpireSecond() - 10) * 1000L));

            }, null);

    }

    /**
     * 获取：统一登录微信，二维码地址
     */
    @SneakyThrows
    @Nullable
    public static GetQrCodeVO getQrCodeUrlWxForSingleSignIn(boolean getQrCodeUrlFlag,
        ISysQrCodeSceneType iSysQrCodeSceneType) {

        Long wxSysOtherAppId = singleSignInProperties.getWxSysOtherAppId();

        if (wxSysOtherAppId == null) {
            ApiResultVO.errorMsg("操作失败：暂未配置微信统一登录，请刷新重试");
        }

        // 执行
        return getQrCodeUrl(null, getQrCodeUrlFlag, SysOtherAppTypeEnum.WX_OFFICIAL_ACCOUNT.getCode(),
            sysOtherAppDO -> {

                String accessToken = WxUtil.getAccessToken(sysOtherAppDO.getTenantId(), sysOtherAppDO.getAppId());

                Long qrCodeId = IdGeneratorUtil.nextId();

                String qrCodeUrl = WxUtil.getQrCodeUrl(accessToken, iSysQrCodeSceneType, qrCodeId.toString());

                return new GetQrCodeVO(qrCodeUrl, qrCodeId,
                    System.currentTimeMillis() + ((iSysQrCodeSceneType.getExpireSecond() - 10) * 1000L));

            }, lambdaQueryChainWrapper -> {

                lambdaQueryChainWrapper.eq(SysOtherAppDO::getId, wxSysOtherAppId);

            });

    }

    /**
     * 获取：二维码地址
     */
    @SneakyThrows
    @Nullable
    public static GetQrCodeVO getQrCodeUrl(@Nullable Long tenantId, boolean getQrCodeUrlFlag,
        @Nullable Integer otherAppType, Func1<SysOtherAppDO, GetQrCodeVO> func1,
        @Nullable Consumer<LambdaQueryChainWrapper<SysOtherAppDO>> lambdaQueryChainWrapperConsumer) {

        if (otherAppType == null) {
            otherAppType = SysOtherAppTypeEnum.WX_OFFICIAL_ACCOUNT.getCode();
        }

        LambdaQueryChainWrapper<SysOtherAppDO> lambdaQueryChainWrapper = ChainWrappers
            .lambdaQueryChain(sysOtherAppMapper).eq(SysOtherAppDO::getType, otherAppType)
            .eq(BaseEntityNoId::getEnableFlag, true).eq(tenantId != null, BaseEntityNoIdSuper::getTenantId, tenantId);

        if (lambdaQueryChainWrapperConsumer != null) {

            lambdaQueryChainWrapperConsumer.accept(lambdaQueryChainWrapper);

        }

        Page<SysOtherAppDO> page = lambdaQueryChainWrapper
            .select(SysOtherAppDO::getAppId, BaseEntityNoIdSuper::getTenantId).page(MyPageUtil.getLimit1Page());

        if (CollUtil.isEmpty(page.getRecords())) {
            return null;
        }

        if (!getQrCodeUrlFlag) {
            return new GetQrCodeVO(); // 这里回复一个对象，然后前端可以根据这个值，和前面的 null值进行判断，是否要进一步获取二维码地址，原因：二维码地址获取速度慢
        }

        SysOtherAppDO sysOtherAppDO = page.getRecords().get(0);

        return func1.call(sysOtherAppDO);

    }

    /**
     * 绑定微信
     */
    @NotNull
    public static SysQrCodeSceneBindVO setWx(Long qrCodeId, String code, String codeKey, String currentPassword) {

        // 执行
        return getSysQrCodeSceneBindVoAndHandle(qrCodeId, true, sysQrCodeSceneBindBO -> {

            // 执行
            SignUtil.bindAccount(code, BaseRedisKeyEnum.PRE_WX_OPEN_ID, sysQrCodeSceneBindBO.getOpenId(),
                sysQrCodeSceneBindBO.getAppId(), codeKey, currentPassword);

        });

    }

    /**
     * 绑定微信统一登录
     */
    @NotNull
    public static SysQrCodeSceneBindVO setWxForSingleSignIn(Long qrCodeId, String code, String codeKey,
        String currentPassword) {

        // 执行
        return getSysQrCodeSceneBindVoAndHandleForSingleSignIn(qrCodeId, true, sysQrCodeSceneBindBO -> {

            // 执行
            SignUtil.bindAccount(code, BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_WX, sysQrCodeSceneBindBO.getOpenId(),
                sysQrCodeSceneBindBO.getAppId(), codeKey, currentPassword);

        });

    }

    /**
     * 获取：微信绑定信息
     */
    @SneakyThrows
    @NotNull
    public static SysQrCodeSceneBindVO getSysQrCodeSceneBindVoAndHandle(Long qrCodeId, boolean deleteFlag,
        @Nullable VoidFunc1<SysQrCodeSceneBindBO> voidFunc1) {

        // 执行
        return execGetSysQrCodeSceneBindVoAndHandle(qrCodeId, deleteFlag, BaseRedisKeyEnum.PRE_SYS_WX_QR_CODE_BIND,
            voidFunc1);

    }

    /**
     * 获取：微信统一登录绑定信息
     */
    @SneakyThrows
    @NotNull
    public static SysQrCodeSceneBindVO getSysQrCodeSceneBindVoAndHandleForSingleSignIn(Long qrCodeId,
        boolean deleteFlag, @Nullable VoidFunc1<SysQrCodeSceneBindBO> voidFunc1) {

        // 执行
        return execGetSysQrCodeSceneBindVoAndHandle(qrCodeId, deleteFlag,
            BaseRedisKeyEnum.PRE_SYS_SINGLE_SIGN_IN_SET_WX, voidFunc1);

    }

    /**
     * 获取：微信绑定信息
     */
    @SneakyThrows
    @NotNull
    public static SysQrCodeSceneBindVO execGetSysQrCodeSceneBindVoAndHandle(Long qrCodeId, boolean deleteFlag,
        BaseRedisKeyEnum baseRedisKeyEnum, @Nullable VoidFunc1<SysQrCodeSceneBindBO> voidFunc1) {

        RBucket<SysQrCodeSceneBindBO> rBucket = redissonClient.getBucket(baseRedisKeyEnum.name() + qrCodeId);

        SysQrCodeSceneBindBO sysQrCodeSceneBindBO;

        if (deleteFlag) {

            sysQrCodeSceneBindBO = rBucket.getAndDelete();

        } else {

            sysQrCodeSceneBindBO = rBucket.get();

        }

        SysQrCodeSceneBindVO sysQrCodeSceneBindVO = new SysQrCodeSceneBindVO();

        if (sysQrCodeSceneBindBO == null) {

            sysQrCodeSceneBindVO.setSceneFlag(false);

        } else {

            sysQrCodeSceneBindVO.setSceneFlag(true);

            Long qrCodeUserId = sysQrCodeSceneBindBO.getUserId();

            if (qrCodeUserId == null) { // 如果：不存在用户，则开始绑定

                if (voidFunc1 != null) {

                    voidFunc1.call(sysQrCodeSceneBindBO);

                }

            } else {

                Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

                if (currentUserIdNotAdmin.equals(qrCodeUserId)) {

                    sysQrCodeSceneBindVO.setErrorMsg("操作失败：您已绑定该微信，请勿重复绑定");

                } else {

                    sysQrCodeSceneBindVO.setErrorMsg("操作失败：该微信已被绑定");

                }

            }

        }

        return sysQrCodeSceneBindVO;

    }

    /**
     * 获取：已经绑定了微信的用户。进行扫码操作
     */
    @SneakyThrows
    @NotNull
    public static SysQrCodeSceneBindVO getSysQrCodeSceneBindVoAndHandleForUserId(Long qrCodeId, boolean deleteFlag,
        BaseRedisKeyEnum baseRedisKeyEnum, @Nullable VoidFunc0 voidFunc0) {

        RBucket<Long> bucket = redissonClient.getBucket(baseRedisKeyEnum.name() + qrCodeId);

        Long userId;

        if (deleteFlag) {

            userId = bucket.getAndDelete();

        } else {

            userId = bucket.get();

        }

        SysQrCodeSceneBindVO sysQrCodeSceneBindVO = new SysQrCodeSceneBindVO();

        if (userId == null) {

            sysQrCodeSceneBindVO.setSceneFlag(false);

        } else {

            sysQrCodeSceneBindVO.setSceneFlag(true);

            Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

            if (!userId.equals(currentUserIdNotAdmin)) {

                ApiResultVO.errorMsg("操作失败：扫码用户不是当前用户，请重新进行扫码操作");

            }

            if (voidFunc0 != null) {

                voidFunc0.call();

            }

        }

        return sysQrCodeSceneBindVO;

    }

}
