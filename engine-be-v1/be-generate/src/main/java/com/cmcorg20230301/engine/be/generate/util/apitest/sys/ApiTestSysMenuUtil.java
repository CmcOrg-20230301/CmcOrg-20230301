package com.cmcorg20230301.engine.be.generate.util.apitest.sys;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.engine.be.generate.util.apitest.ApiTestHelper;
import com.cmcorg20230301.engine.be.generate.util.apitest.sign.ApiTestSignSignInNameUtil;
import com.cmcorg20230301.engine.be.menu.model.dto.SysMenuInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.menu.model.dto.SysMenuPageDTO;
import com.cmcorg20230301.engine.be.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.security.model.entity.SysMenuDO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 菜单相关接口测试工具
 */
@Slf4j
public class ApiTestSysMenuUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    //    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    // 菜单名
    private static final String SYS_DICT_NAME = IdUtil.simpleUUID();

    public static void main(String[] args) {

        // 执行
        exec(API_ENDPOINT, ApiTestHelper.ADMIN_SIGN_IN_NAME, ApiTestHelper.ADMIN_PASSWORD, ApiTestHelper.RSA_PUBLIC_KEY,
            SYS_DICT_NAME);

    }

    /**
     * 执行
     */
    private static void exec(String apiEndpoint, String adminSignInName, String adminPassword, String rsaPublicKey,
        String sysMenuName) {

        // 登录名-用户名账号密码登录
        String jwt =
            ApiTestSignSignInNameUtil.signInNameSignIn(apiEndpoint, adminSignInName, adminPassword, rsaPublicKey);

        // 菜单-新增/修改
        SysMenuInsertOrUpdateDTO dto = sysMenuInsertOrUpdate(apiEndpoint, jwt, sysMenuName);

        // 菜单-分页排序查询
        Page<SysMenuDO> sysMenuDOPage = sysMenuPage(apiEndpoint, jwt, dto);

        SysMenuDO sysMenuDOByPage = null;

        for (SysMenuDO item : sysMenuDOPage.getRecords()) {

            if (item.getName().equals(dto.getName())) {

                sysMenuDOByPage = item;
                break;

            }

        }

        if (sysMenuDOByPage == null) {
            log.info("sysMenuDOByPage 等于null，结束");
            return;
        }

        // 查询：树结构
        sysMenuTree(apiEndpoint, jwt, dto);

        Long id = sysMenuDOByPage.getId();

        // 菜单-通过主键id，查看详情
        SysMenuDO sysMenuDOById = sysMenuInfoById(apiEndpoint, jwt, id);

        if (sysMenuDOById == null) {
            log.info("sysMenuDOById 等于null，结束");
            return;
        }

        // 通过主键 idSet，加减排序号
        sysMenuAddOrderNo(apiEndpoint, jwt, CollUtil.newHashSet(id));

        // 菜单-通过主键id，查看详情
        sysMenuInfoById(apiEndpoint, jwt, id);

        // 菜单-批量删除
        sysMenuDeleteByIdSet(apiEndpoint, jwt, CollUtil.newHashSet(id));

    }

    /**
     * 通过主键 idSet，加减排序号
     */
    private static void sysMenuAddOrderNo(String apiEndpoint, String jwt, Set<Long> idSet) {

        long currentTs = System.currentTimeMillis();

        ChangeNumberDTO changeNumberDTO = new ChangeNumberDTO();
        changeNumberDTO.setNumber(100L);
        changeNumberDTO.setIdSet(idSet);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sys/menu/addOrderNo").body(JSONUtil.toJsonStr(changeNumberDTO))
                .header("Authorization", jwt).execute().body();

        log.info("菜单-通过主键 idSet，加减排序号：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 查询：树结构
     */
    private static void sysMenuTree(String apiEndpoint, String jwt, SysMenuInsertOrUpdateDTO dto) {

        long currentTs = System.currentTimeMillis();

        SysMenuPageDTO pageDTO = new SysMenuPageDTO();
        pageDTO.setName(dto.getName());

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sys/menu/tree").body(JSONUtil.toJsonStr(dto)).header("Authorization", jwt)
                .execute().body();

        log.info("菜单-查询：树结构：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 菜单-批量删除
     */
    private static void sysMenuDeleteByIdSet(String apiEndpoint, String jwt, Set<Long> idSet) {

        long currentTs = System.currentTimeMillis();

        NotEmptyIdSet notEmptyIdSet = new NotEmptyIdSet();
        notEmptyIdSet.setIdSet(idSet);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sys/menu/deleteByIdSet").body(JSONUtil.toJsonStr(notEmptyIdSet))
                .header("Authorization", jwt).execute().body();

        log.info("菜单-批量删除：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 菜单-通过主键id，查看详情
     */
    private static SysMenuDO sysMenuInfoById(String apiEndpoint, String jwt, Long id) {

        long currentTs = System.currentTimeMillis();

        NotNullId notNullId = new NotNullId();
        notNullId.setId(id);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/menu/infoById").body(JSONUtil.toJsonStr(notNullId))
            .header("Authorization", jwt).execute().body();

        log.info("菜单-通过主键id，查看详情：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        ApiResultVO<SysMenuDO> apiResultVO = JSONUtil.toBean(bodyStr, new TypeReference<ApiResultVO<SysMenuDO>>() {
        }, false);

        return apiResultVO.getData();

    }

    /**
     * 菜单-分页排序查询
     */
    private static Page<SysMenuDO> sysMenuPage(String apiEndpoint, String jwt, SysMenuInsertOrUpdateDTO dto) {

        long currentTs = System.currentTimeMillis();

        SysMenuPageDTO pageDTO = new SysMenuPageDTO();
        pageDTO.setName(dto.getName());

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sys/menu/page").body(JSONUtil.toJsonStr(dto)).header("Authorization", jwt)
                .execute().body();

        log.info("菜单-分页排序查询：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        ApiResultVO<Page<SysMenuDO>> apiResultVO =
            JSONUtil.toBean(bodyStr, new TypeReference<ApiResultVO<Page<SysMenuDO>>>() {
            }, false);

        return apiResultVO.getData();

    }

    /**
     * 菜单-新增/修改
     */
    private static SysMenuInsertOrUpdateDTO sysMenuInsertOrUpdate(String apiEndpoint, String jwt, String sysMenuName) {

        long currentTs = System.currentTimeMillis();

        SysMenuInsertOrUpdateDTO dto = new SysMenuInsertOrUpdateDTO();
        dto.setParentId(0L);
        dto.setName(sysMenuName);
        dto.setPath("");
        dto.setRouter("");
        dto.setIcon("");
        dto.setAuths("");
        dto.setAuthFlag(false);
        dto.setRoleIdSet(null);
        dto.setEnableFlag(true);
        dto.setFirstFlag(false);
        dto.setOrderNo(100);
        dto.setShowFlag(true);
        dto.setRedirect("");
        dto.setRemark("");

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/menu/insertOrUpdate").body(JSONUtil.toJsonStr(dto))
            .header("Authorization", jwt).execute().body();

        log.info("菜单-新增/修改：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        return dto;

    }

}
