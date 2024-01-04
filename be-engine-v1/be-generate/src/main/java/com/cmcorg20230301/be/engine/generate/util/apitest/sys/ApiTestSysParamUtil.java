package com.cmcorg20230301.be.engine.generate.util.apitest.sys;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.generate.util.apitest.ApiTestHelper;
import com.cmcorg20230301.be.engine.generate.util.apitest.sign.ApiTestSignSignInNameUtil;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.param.model.dto.SysParamInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.param.model.dto.SysParamPageDTO;
import com.cmcorg20230301.be.engine.security.model.entity.SysParamDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 系统参数相关接口测试工具
 */
@Slf4j
public class ApiTestSysParamUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    //    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    public static void main(String[] args) {

        // 执行
        exec(API_ENDPOINT, ApiTestHelper.ADMIN_SIGN_IN_NAME, ApiTestHelper.ADMIN_PASSWORD, ApiTestHelper.RSA_PUBLIC_KEY,
                IdUtil.simpleUUID());

    }

    /**
     * 执行
     */
    private static void exec(String apiEndpoint, String adminSignInName, String adminPassword, String rsaPublicKey,
                             String sysParamName) {

        // 登录名-用户名账号密码登录
        SignInVO signInVO =
                ApiTestSignSignInNameUtil.signInNameSignIn(apiEndpoint, adminSignInName, adminPassword, rsaPublicKey);

        String jwt = signInVO.getJwt();

        // 系统参数-新增/修改
        SysParamInsertOrUpdateDTO dto = sysParamInsertOrUpdate(apiEndpoint, jwt, sysParamName);

        // 系统参数-分页排序查询
        Page<SysParamDO> sysParamDOPage = sysParamPage(apiEndpoint, jwt, dto);

        SysParamDO sysParamDOByPage = null;

        for (SysParamDO item : sysParamDOPage.getRecords()) {

            if (item.getName().equals(dto.getName())) {

                sysParamDOByPage = item;
                break;

            }

        }

        if (sysParamDOByPage == null) {
            log.info("sysParamDOByPage 等于null，结束");
            return;
        }

        Long id = sysParamDOByPage.getId();

        // 系统参数-通过主键id，查看详情
        SysParamDO sysParamDOById = sysParamInfoById(apiEndpoint, jwt, id);

        if (sysParamDOById == null) {
            log.info("sysParamDOById 等于null，结束");
            return;
        }

        // 系统参数-批量删除
        sysParamDeleteByIdSet(apiEndpoint, jwt, CollUtil.newHashSet(id));

    }

    /**
     * 系统参数-批量删除
     */
    private static void sysParamDeleteByIdSet(String apiEndpoint, String jwt, Set<Long> idSet) {

        long currentTs = System.currentTimeMillis();

        NotEmptyIdSet notEmptyIdSet = new NotEmptyIdSet();
        notEmptyIdSet.setIdSet(idSet);

        String bodyStr =
                HttpRequest.post(apiEndpoint + "/sys/param/deleteByIdSet").body(JSONUtil.toJsonStr(notEmptyIdSet))
                        .header("Authorization", jwt).execute().body();

        log.info("系统参数-批量删除：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 系统参数-通过主键id，查看详情
     */
    private static SysParamDO sysParamInfoById(String apiEndpoint, String jwt, Long id) {

        long currentTs = System.currentTimeMillis();

        NotNullId notNullId = new NotNullId();
        notNullId.setId(id);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/param/infoById").body(JSONUtil.toJsonStr(notNullId))
                .header("Authorization", jwt).execute().body();

        log.info("系统参数-通过主键id，查看详情：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        ApiResultVO<SysParamDO> apiResultVO = JSONUtil.toBean(bodyStr, new TypeReference<ApiResultVO<SysParamDO>>() {
        }, false);

        return apiResultVO.getData();

    }

    /**
     * 系统参数-分页排序查询
     */
    private static Page<SysParamDO> sysParamPage(String apiEndpoint, String jwt, SysParamInsertOrUpdateDTO dto) {

        long currentTs = System.currentTimeMillis();

        SysParamPageDTO pageDTO = new SysParamPageDTO();
        pageDTO.setName(dto.getName());

        String bodyStr =
                HttpRequest.post(apiEndpoint + "/sys/param/page").body(JSONUtil.toJsonStr(dto)).header("Authorization", jwt)
                        .execute().body();

        log.info("系统参数-分页排序查询：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        ApiResultVO<Page<SysParamDO>> apiResultVO =
                JSONUtil.toBean(bodyStr, new TypeReference<ApiResultVO<Page<SysParamDO>>>() {
                }, false);

        return apiResultVO.getData();

    }

    /**
     * 系统参数-新增/修改
     */
    private static SysParamInsertOrUpdateDTO sysParamInsertOrUpdate(String apiEndpoint, String jwt,
                                                                    String sysParamName) {

        long currentTs = System.currentTimeMillis();

        SysParamInsertOrUpdateDTO dto = new SysParamInsertOrUpdateDTO();
        dto.setName(sysParamName);
        dto.setValue(IdUtil.simpleUUID());
        dto.setRemark("");
        dto.setEnableFlag(true);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/param/insertOrUpdate").body(JSONUtil.toJsonStr(dto))
                .header("Authorization", jwt).execute().body();

        log.info("系统参数-新增/修改：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        return dto;

    }

}
