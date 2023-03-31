package com.cmcorg20230301.engine.be.generate.util.apitest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.engine.be.dict.model.dto.SysDictInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.dict.model.dto.SysDictPageDTO;
import com.cmcorg20230301.engine.be.dict.model.entity.SysDictDO;
import com.cmcorg20230301.engine.be.dict.model.enums.SysDictTypeEnum;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 字典相关接口测试工具
 */
@Slf4j
public class ApiTestSysDictUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    //    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    // 字典名
    private static final String SYS_DICT_NAME = IdUtil.simpleUUID();

    // 字典 key
    private static final String SYS_DICT_KEY = IdUtil.simpleUUID();

    public static void main(String[] args) {

        // 执行
        exec(API_ENDPOINT, ApiTestHelper.ADMIN_SIGN_IN_NAME, ApiTestHelper.ADMIN_PASSWORD, ApiTestHelper.RSA_PUBLIC_KEY,
            SYS_DICT_NAME, SYS_DICT_KEY);

    }

    /**
     * 执行
     */
    private static void exec(String apiEndpoint, String rootSignInName, String rootPassword, String rsaPublicKey,
        String sysDictName, String sysDictKey) {

        // 登录名-用户名账号密码登录
        String jwt =
            ApiTestSignSignInNameUtil.signInNameSignIn(apiEndpoint, rootSignInName, rootPassword, rsaPublicKey);

        // 字典-新增/修改
        SysDictInsertOrUpdateDTO dto =
            sysDictInsertOrUpdate(apiEndpoint, jwt, sysDictName, sysDictKey, SysDictTypeEnum.DICT, 1);

        // 字典-分页排序查询
        Page<SysDictDO> sysDictDOPage = sysDictPage(apiEndpoint, jwt, dto);

        SysDictDO sysDictDOByPage = null;

        for (SysDictDO item : sysDictDOPage.getRecords()) {

            if (item.getName().equals(dto.getName())) {

                sysDictDOByPage = item;
                break;

            }

        }

        if (sysDictDOByPage == null) {
            log.info("sysDictDOByPage 等于null，结束");
            return;
        }

        Long id = sysDictDOByPage.getId();

        // 字典-通过主键id，查看详情
        SysDictDO sysDictDOById = sysDictInfoById(apiEndpoint, jwt, id);

        if (sysDictDOById == null) {
            log.info("sysDictDOById 等于null，结束");
            return;
        }

        // 字典-批量删除
        sysDictDeleteByIdSet(apiEndpoint, jwt, CollUtil.newHashSet(id));

    }

    /**
     * 字典-批量删除
     */
    private static void sysDictDeleteByIdSet(String apiEndpoint, String jwt, Set<Long> idSet) {

        long currentTs = System.currentTimeMillis();

        NotEmptyIdSet notEmptyIdSet = new NotEmptyIdSet();
        notEmptyIdSet.setIdSet(idSet);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sys/dict/deleteByIdSet").body(JSONUtil.toJsonStr(notEmptyIdSet))
                .header("Authorization", jwt).execute().body();

        log.info("字典-批量删除：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 字典-通过主键id，查看详情
     */
    private static SysDictDO sysDictInfoById(String apiEndpoint, String jwt, Long id) {

        long currentTs = System.currentTimeMillis();

        NotNullId notNullId = new NotNullId();
        notNullId.setId(id);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/dict/infoById").body(JSONUtil.toJsonStr(notNullId))
            .header("Authorization", jwt).execute().body();

        log.info("字典-通过主键id，查看详情：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        ApiResultVO<SysDictDO> apiResultVO = JSONUtil.toBean(bodyStr, new TypeReference<ApiResultVO<SysDictDO>>() {
        }, false);

        return apiResultVO.getData();

    }

    /**
     * 字典-分页排序查询
     */
    private static Page<SysDictDO> sysDictPage(String apiEndpoint, String jwt, SysDictInsertOrUpdateDTO dto) {

        long currentTs = System.currentTimeMillis();

        SysDictPageDTO pageDTO = new SysDictPageDTO();
        pageDTO.setName(dto.getName());

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sys/dict/page").body(JSONUtil.toJsonStr(dto)).header("Authorization", jwt)
                .execute().body();

        log.info("字典-分页排序查询：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        ApiResultVO<Page<SysDictDO>> apiResultVO =
            JSONUtil.toBean(bodyStr, new TypeReference<ApiResultVO<Page<SysDictDO>>>() {
            }, false);

        return apiResultVO.getData();

    }

    /**
     * 字典-新增/修改
     */
    private static SysDictInsertOrUpdateDTO sysDictInsertOrUpdate(String apiEndpoint, String jwt, String sysDictName,
        String sysDictKey, SysDictTypeEnum type, int value) {

        long currentTs = System.currentTimeMillis();

        SysDictInsertOrUpdateDTO dto = new SysDictInsertOrUpdateDTO();
        dto.setDictKey(sysDictKey);
        dto.setName(sysDictName);
        dto.setType(type);

        if (SysDictTypeEnum.DICT.equals(type)) {
            dto.setValue(-1);
        } else {
            dto.setValue(value);
        }

        dto.setEnableFlag(true);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/dict/insertOrUpdate").body(JSONUtil.toJsonStr(dto))
            .header("Authorization", jwt).execute().body();

        log.info("字典-新增/修改：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        return dto;

    }

}
