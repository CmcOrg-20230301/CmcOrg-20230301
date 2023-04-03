package com.cmcorg20230301.engine.be.generate.util.apitest.sys;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.engine.be.generate.util.apitest.ApiTestHelper;
import com.cmcorg20230301.engine.be.generate.util.apitest.sign.ApiTestSignSignInNameUtil;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.role.model.dto.SysRoleInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.entity.SysRoleDO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.util.MyRsaUtil;
import com.cmcorg20230301.engine.be.user.model.dto.SysUserInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.user.model.dto.SysUserPageDTO;
import com.cmcorg20230301.engine.be.user.model.vo.SysUserInfoByIdVO;
import com.cmcorg20230301.engine.be.user.model.vo.SysUserPageVO;
import com.cmcorg20230301.engine.be.util.util.NicknameUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户相关接口测试工具
 */
@Slf4j
public class ApiTestSysUserUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    //    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    public static void main(String[] args) {

        // 执行
        exec(API_ENDPOINT, ApiTestHelper.ADMIN_SIGN_IN_NAME, ApiTestHelper.ADMIN_PASSWORD, ApiTestHelper.RSA_PUBLIC_KEY,
            NicknameUtil.getRandomNickname(), RandomUtil.randomString(2) + RandomUtil.randomStringUpper(18));

    }

    /**
     * 执行
     */
    private static void exec(String apiEndpoint, String adminSignInName, String adminPassword, String rsaPublicKey,
        String sysUserName, String password) {

        // 登录名-用户名账号密码登录
        String jwt =
            ApiTestSignSignInNameUtil.signInNameSignIn(apiEndpoint, adminSignInName, adminPassword, rsaPublicKey);

        // 用户-新增/修改
        SysUserInsertOrUpdateDTO dto = sysUserInsertOrUpdate(apiEndpoint, jwt, sysUserName, password, rsaPublicKey);

        // 登录名-用户名账号密码登录
        ApiTestSignSignInNameUtil.signInNameSignIn(apiEndpoint, sysUserName, password, rsaPublicKey);

        // 用户-分页排序查询
        Page<SysUserPageVO> sysUserDOPage = sysUserPage(apiEndpoint, jwt, dto);

        if (CollUtil.isEmpty(sysUserDOPage.getRecords())) {

            log.info("sysUserDOPage.getRecords() 为空，结束");
            return;

        }

        SysUserPageVO sysUserDOByPage = sysUserDOPage.getRecords().get(0);

        Long id = sysUserDOByPage.getId();

        // 用户-通过主键id，查看详情
        SysUserInfoByIdVO sysUserInfoByIdVO = sysUserInfoById(apiEndpoint, jwt, id);

        if (sysUserInfoByIdVO == null) {
            log.info("sysUserInfoByIdVO 等于null，结束");
            return;
        }

        // 用户-批量删除
        sysUserDeleteByIdSet(apiEndpoint, jwt, CollUtil.newHashSet(id));

    }

    /**
     * 用户-批量删除
     */
    private static void sysUserDeleteByIdSet(String apiEndpoint, String jwt, Set<Long> idSet) {

        long currentTs = System.currentTimeMillis();

        NotEmptyIdSet notEmptyIdSet = new NotEmptyIdSet();
        notEmptyIdSet.setIdSet(idSet);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sys/user/deleteByIdSet").body(JSONUtil.toJsonStr(notEmptyIdSet))
                .header("Authorization", jwt).execute().body();

        log.info("用户-批量删除：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 用户-通过主键id，查看详情
     */
    private static SysUserInfoByIdVO sysUserInfoById(String apiEndpoint, String jwt, Long id) {

        long currentTs = System.currentTimeMillis();

        NotNullId notNullId = new NotNullId();
        notNullId.setId(id);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/user/infoById").body(JSONUtil.toJsonStr(notNullId))
            .header("Authorization", jwt).execute().body();

        log.info("用户-通过主键id，查看详情：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        ApiResultVO<SysUserInfoByIdVO> apiResultVO =
            JSONUtil.toBean(bodyStr, new TypeReference<ApiResultVO<SysUserInfoByIdVO>>() {
            }, false);

        return apiResultVO.getData();

    }

    /**
     * 用户-分页排序查询
     */
    private static Page<SysUserPageVO> sysUserPage(String apiEndpoint, String jwt, SysUserInsertOrUpdateDTO dto) {

        long currentTs = System.currentTimeMillis();

        SysUserPageDTO pageDTO = new SysUserPageDTO();
        pageDTO.setSignInName(dto.getSignInName());

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sys/user/page").body(JSONUtil.toJsonStr(dto)).header("Authorization", jwt)
                .execute().body();

        log.info("用户-分页排序查询：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        ApiResultVO<Page<SysUserPageVO>> apiResultVO =
            JSONUtil.toBean(bodyStr, new TypeReference<ApiResultVO<Page<SysUserPageVO>>>() {
            }, false);

        return apiResultVO.getData();

    }

    /**
     * 用户-新增/修改
     */
    private static SysUserInsertOrUpdateDTO sysUserInsertOrUpdate(String apiEndpoint, String jwt, String sysUserName,
        String passwordTemp, String rsaPublicKey) {

        String originPassword = MyRsaUtil.rsaEncrypt(passwordTemp, rsaPublicKey);

        String password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(passwordTemp)));

        password = MyRsaUtil.rsaEncrypt(password, rsaPublicKey);

        Page<SysRoleDO> sysRoleDOPage =
            ApiTestSysRoleUtil.sysRolePage(apiEndpoint, jwt, new SysRoleInsertOrUpdateDTO());

        Set<Long> roleIdSet = sysRoleDOPage.getRecords().stream().map(BaseEntity::getId).collect(Collectors.toSet());

        long currentTs = System.currentTimeMillis();

        SysUserInsertOrUpdateDTO dto = new SysUserInsertOrUpdateDTO();
        dto.setSignInName(sysUserName);
        dto.setEmail(null);
        dto.setPhone(null);
        dto.setPassword(password);
        dto.setOriginPassword(originPassword);
        dto.setNickname(NicknameUtil.getRandomNickname());
        dto.setBio(IdUtil.simpleUUID());
        dto.setEnableFlag(true);
        dto.setRoleIdSet(roleIdSet);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/user/insertOrUpdate").body(JSONUtil.toJsonStr(dto))
            .header("Authorization", jwt).execute().body();

        log.info("用户-新增/修改：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        return dto;

    }

}
