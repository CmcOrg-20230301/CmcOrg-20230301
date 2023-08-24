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
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.role.model.dto.SysRoleInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.role.model.dto.SysRolePageDTO;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.entity.SysMenuDO;
import com.cmcorg20230301.engine.be.security.model.entity.SysRoleDO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.user.model.dto.SysUserInsertOrUpdateDTO;
import com.cmcorg20230301.engine.be.user.model.vo.SysUserPageVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色相关接口测试工具
 */
@Slf4j
public class ApiTestSysRoleUtil {

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
        String sysRoleName) {

        // 登录名-用户名账号密码登录
        String jwt =
            ApiTestSignSignInNameUtil.signInNameSignIn(apiEndpoint, adminSignInName, adminPassword, rsaPublicKey);

        // 角色-新增/修改
        SysRoleInsertOrUpdateDTO dto = sysRoleInsertOrUpdate(apiEndpoint, jwt, sysRoleName);

        // 角色-分页排序查询
        Page<SysRoleDO> sysRoleDOPage = sysRolePage(apiEndpoint, jwt, dto);

        SysRoleDO sysRoleDOByPage = null;

        for (SysRoleDO item : sysRoleDOPage.getRecords()) {

            if (item.getName().equals(dto.getName())) {

                sysRoleDOByPage = item;
                break;

            }

        }

        if (sysRoleDOByPage == null) {
            log.info("sysRoleDOByPage 等于null，结束");
            return;
        }

        Long id = sysRoleDOByPage.getId();

        // 角色-通过主键id，查看详情
        SysRoleDO sysRoleDOById = sysRoleInfoById(apiEndpoint, jwt, id);

        if (sysRoleDOById == null) {
            log.info("sysRoleDOById 等于null，结束");
            return;
        }

        // 角色-批量删除
        sysRoleDeleteByIdSet(apiEndpoint, jwt, CollUtil.newHashSet(id));

    }

    /**
     * 角色-批量删除
     */
    private static void sysRoleDeleteByIdSet(String apiEndpoint, String jwt, Set<Long> idSet) {

        long currentTs = System.currentTimeMillis();

        NotEmptyIdSet notEmptyIdSet = new NotEmptyIdSet();
        notEmptyIdSet.setIdSet(idSet);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sys/role/deleteByIdSet").body(JSONUtil.toJsonStr(notEmptyIdSet))
                .header("Authorization", jwt).execute().body();

        log.info("角色-批量删除：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 角色-通过主键id，查看详情
     */
    private static SysRoleDO sysRoleInfoById(String apiEndpoint, String jwt, Long id) {

        long currentTs = System.currentTimeMillis();

        NotNullId notNullId = new NotNullId();
        notNullId.setId(id);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/role/infoById").body(JSONUtil.toJsonStr(notNullId))
            .header("Authorization", jwt).execute().body();

        log.info("角色-通过主键id，查看详情：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        ApiResultVO<SysRoleDO> apiResultVO = JSONUtil.toBean(bodyStr, new TypeReference<ApiResultVO<SysRoleDO>>() {
        }, false);

        return apiResultVO.getData();

    }

    /**
     * 角色-分页排序查询
     */
    public static Page<SysRoleDO> sysRolePage(String apiEndpoint, String jwt, SysRoleInsertOrUpdateDTO dto) {

        long currentTs = System.currentTimeMillis();

        SysRolePageDTO pageDTO = new SysRolePageDTO();
        pageDTO.setName(dto.getName());

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sys/role/page").body(JSONUtil.toJsonStr(dto)).header("Authorization", jwt)
                .execute().body();

        log.info("角色-分页排序查询：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        ApiResultVO<Page<SysRoleDO>> apiResultVO =
            JSONUtil.toBean(bodyStr, new TypeReference<ApiResultVO<Page<SysRoleDO>>>() {
            }, false);

        return apiResultVO.getData();

    }

    /**
     * 角色-新增/修改
     */
    private static SysRoleInsertOrUpdateDTO sysRoleInsertOrUpdate(String apiEndpoint, String jwt, String sysRoleName) {

        Page<SysMenuDO> sysMenuDOPage =
            ApiTestSysMenuUtil.sysMenuPage(apiEndpoint, jwt, new SysMenuInsertOrUpdateDTO());

        Set<Long> menuIdSet = sysMenuDOPage.getRecords().stream().map(BaseEntity::getId).collect(Collectors.toSet());

        Page<SysUserPageVO> sysUserPageVOPage =
            ApiTestSysUserUtil.sysUserPage(apiEndpoint, jwt, new SysUserInsertOrUpdateDTO());

        Set<Long> userIdSet =
            sysUserPageVOPage.getRecords().stream().map(SysUserPageVO::getId).collect(Collectors.toSet());

        long currentTs = System.currentTimeMillis();

        SysRoleInsertOrUpdateDTO dto = new SysRoleInsertOrUpdateDTO();
        dto.setName(sysRoleName);
        dto.setMenuIdSet(menuIdSet);
        dto.setUserIdSet(userIdSet);
        dto.setDefaultFlag(false);
        dto.setEnableFlag(true);
        dto.setRemark("");

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/role/insertOrUpdate").body(JSONUtil.toJsonStr(dto))
            .header("Authorization", jwt).execute().body();

        log.info("角色-新增/修改：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        return dto;

    }

}
