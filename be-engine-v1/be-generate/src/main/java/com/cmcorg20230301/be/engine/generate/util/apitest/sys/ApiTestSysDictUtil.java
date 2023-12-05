package com.cmcorg20230301.be.engine.generate.util.apitest.sys;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.dict.model.dto.SysDictInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.dict.model.dto.SysDictListByDictKeyDTO;
import com.cmcorg20230301.be.engine.dict.model.dto.SysDictPageDTO;
import com.cmcorg20230301.be.engine.generate.util.apitest.ApiTestHelper;
import com.cmcorg20230301.be.engine.generate.util.apitest.sign.ApiTestSignSignInNameUtil;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.security.model.entity.SysDictDO;
import com.cmcorg20230301.be.engine.security.model.enums.SysDictTypeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * 字典相关接口测试工具
 */
@Slf4j
public class ApiTestSysDictUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    //    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    public static void main(String[] args) {

        for (int i = 0; i < 1; i++) {

            ThreadUtil.execute(() -> {

                // 执行
                exec(API_ENDPOINT, ApiTestHelper.ADMIN_SIGN_IN_NAME, ApiTestHelper.ADMIN_PASSWORD,
                    ApiTestHelper.RSA_PUBLIC_KEY, IdUtil.simpleUUID(), IdUtil.simpleUUID());

            });

        }

    }

    /**
     * 执行
     */
    private static void exec(String apiEndpoint, String adminSignInName, String adminPassword, String rsaPublicKey,
        String sysDictName, String sysDictKey) {

        // 登录名-用户名账号密码登录
        SignInVO signInVO =
            ApiTestSignSignInNameUtil.signInNameSignIn(apiEndpoint, adminSignInName, adminPassword, rsaPublicKey);

        // 字典-新增/修改
        SysDictInsertOrUpdateDTO dictDTO = sysDictInsertOrUpdate(apiEndpoint, signInVO.getJwt(),
            getSysDictInsertOrUpdateDTO(sysDictName, sysDictKey, SysDictTypeEnum.DICT,
                RandomUtil.randomInt(1, 50000000)));

        SysDictInsertOrUpdateDTO dictItemDTO = sysDictInsertOrUpdate(apiEndpoint, signInVO.getJwt(),
            getSysDictInsertOrUpdateDTO(IdUtil.simpleUUID(), sysDictKey, SysDictTypeEnum.DICT_ITEM,
                RandomUtil.randomInt(1, 50000000)));

        // 字典-分页排序查询
        Page<SysDictDO> sysDictDOPage = sysDictPage(apiEndpoint, signInVO.getJwt(), dictDTO);

        SysDictDO sysDictDOByPage = null;

        for (SysDictDO item : sysDictDOPage.getRecords()) {

            if (item.getName().equals(dictDTO.getName())) {

                sysDictDOByPage = item;
                break;

            }

        }

        if (sysDictDOByPage == null) {
            log.info("sysDictDOByPage 等于null，结束");
            return;
        }

        // 字典-分页排序查询
        sysDictDOPage = sysDictPage(apiEndpoint, signInVO.getJwt(), dictItemDTO);

        SysDictDO sysDictDOItemByPage = null;

        for (SysDictDO item : sysDictDOPage.getRecords()) {

            if (item.getName().equals(dictItemDTO.getName())) {

                sysDictDOItemByPage = item;
                break;

            }

        }

        if (sysDictDOItemByPage == null) {
            log.info("sysDictDOItemByPage 等于null，结束");
            return;
        }

        // 查询：树结构
        sysDictTree(apiEndpoint, signInVO.getJwt(), dictItemDTO);

        Long id = sysDictDOByPage.getId();

        // 字典-通过主键id，查看详情
        SysDictDO sysDictDOById = sysDictInfoById(apiEndpoint, signInVO.getJwt(), id);

        if (sysDictDOById == null) {
            log.info("sysDictDOById 等于null，结束");
            return;
        }

        Long itemId = sysDictDOItemByPage.getId();

        // 字典-通过主键id，查看详情
        SysDictDO sysDictDOItemById = sysDictInfoById(apiEndpoint, signInVO.getJwt(), itemId);

        if (sysDictDOItemById == null) {
            log.info("sysDictDOItemById 等于null，结束");
            return;
        }

        // 通过主键 idSet，加减排序号
        sysDictAddOrderNo(apiEndpoint, signInVO.getJwt(), CollUtil.newHashSet(id, itemId));

        // 字典-通过主键id，查看详情
        sysDictInfoById(apiEndpoint, signInVO.getJwt(), id);

        // 字典-通过主键id，查看详情
        sysDictInfoById(apiEndpoint, signInVO.getJwt(), itemId);

        // 通过：dictKey获取字典项集合，备注：会进行缓存
        for (int i = 0; i < 3; i++) {
            sysDictListByDictKey(apiEndpoint, signInVO.getJwt(), sysDictKey);
        }

        // 字典-批量删除
        sysDictDeleteByIdSet(apiEndpoint, signInVO.getJwt(), CollUtil.newHashSet(id, itemId));

    }

    /**
     * 通过：dictKey获取字典项集合，备注：会进行缓存
     */
    private static void sysDictListByDictKey(String apiEndpoint, String jwt, String sysDictKey) {

        long currentTs = System.currentTimeMillis();

        SysDictListByDictKeyDTO dto = new SysDictListByDictKeyDTO();
        dto.setDictKey(sysDictKey);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/dict/listByDictKey").body(JSONUtil.toJsonStr(dto))
            .header("Authorization", jwt).execute().body();

        log.info("通过：dictKey获取字典项集合，备注：会进行缓存：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 通过主键 idSet，加减排序号
     */
    private static void sysDictAddOrderNo(String apiEndpoint, String jwt, Set<Long> idSet) {

        long currentTs = System.currentTimeMillis();

        ChangeNumberDTO changeNumberDTO = new ChangeNumberDTO();
        changeNumberDTO.setNumber(100L);
        changeNumberDTO.setIdSet(idSet);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sys/dict/addOrderNo").body(JSONUtil.toJsonStr(changeNumberDTO))
                .header("Authorization", jwt).execute().body();

        log.info("字典-通过主键 idSet，加减排序号：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 查询：树结构
     */
    private static Set<SysDictDO> sysDictTree(String apiEndpoint, String jwt, SysDictInsertOrUpdateDTO dto) {

        long currentTs = System.currentTimeMillis();

        SysDictPageDTO pageDTO = new SysDictPageDTO();
        pageDTO.setName(dto.getName());

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/dict/tree").body(JSONUtil.toJsonStr(pageDTO))
            .header("Authorization", jwt).execute().body();

        log.info("字典-查询：树结构：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        ApiResultVO<Set<SysDictDO>> apiResultVO =
            JSONUtil.toBean(bodyStr, new TypeReference<ApiResultVO<Set<SysDictDO>>>() {
            }, false);

        return apiResultVO.getData();

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
        }, true);

        return apiResultVO.getData();

    }

    /**
     * 字典-分页排序查询
     */
    private static Page<SysDictDO> sysDictPage(String apiEndpoint, String jwt, SysDictInsertOrUpdateDTO dto) {

        long currentTs = System.currentTimeMillis();

        SysDictPageDTO pageDTO = new SysDictPageDTO();
        pageDTO.setName(dto.getName());

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/dict/page").body(JSONUtil.toJsonStr(pageDTO))
            .header("Authorization", jwt).execute().body();

        log.info("字典-分页排序查询：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        ApiResultVO<Page<SysDictDO>> apiResultVO =
            JSONUtil.toBean(bodyStr, new TypeReference<ApiResultVO<Page<SysDictDO>>>() {
            }, false);

        return apiResultVO.getData();

    }

    /**
     * 字典-新增/修改
     */
    private static SysDictInsertOrUpdateDTO sysDictInsertOrUpdate(String apiEndpoint, String jwt,
        SysDictInsertOrUpdateDTO dto) {

        long currentTs = System.currentTimeMillis();

        Map<String, Object> map = BeanUtil.beanToMap(dto);

        map.put("type", dto.getType().getCode());

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/dict/insertOrUpdate").body(JSONUtil.toJsonStr(map))
            .header("Authorization", jwt).execute().body();

        log.info("字典-新增/修改：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        return dto;

    }

    /**
     * 获取：对象
     */
    @NotNull
    private static SysDictInsertOrUpdateDTO getSysDictInsertOrUpdateDTO(String sysDictName, String sysDictKey,
        SysDictTypeEnum type, int value) {

        SysDictInsertOrUpdateDTO dto = new SysDictInsertOrUpdateDTO();
        dto.setDictKey(sysDictKey);
        dto.setName(sysDictName);
        dto.setType(type);

        if (SysDictTypeEnum.DICT.equals(type)) {
            dto.setValue(-1);
        } else {
            dto.setValue(value);
        }

        dto.setOrderNo(RandomUtil.randomInt(1000, 90000000));
        dto.setEnableFlag(true);

        return dto;

    }

}
