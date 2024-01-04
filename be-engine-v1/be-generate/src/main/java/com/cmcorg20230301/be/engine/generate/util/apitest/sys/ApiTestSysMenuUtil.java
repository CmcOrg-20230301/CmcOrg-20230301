package com.cmcorg20230301.be.engine.generate.util.apitest.sys;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.generate.util.apitest.ApiTestHelper;
import com.cmcorg20230301.be.engine.generate.util.apitest.sign.ApiTestSignSignInNameUtil;
import com.cmcorg20230301.be.engine.menu.model.dto.SysMenuInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.menu.model.dto.SysMenuPageDTO;
import com.cmcorg20230301.be.engine.model.model.dto.ChangeNumberDTO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.role.model.dto.SysRoleInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.SysMenuDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysRoleDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单相关接口测试工具
 */
@Slf4j
public class ApiTestSysMenuUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    //    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    public static void main(String[] args) {

        // 执行
        exec(API_ENDPOINT, ApiTestHelper.ADMIN_SIGN_IN_NAME, ApiTestHelper.ADMIN_PASSWORD, ApiTestHelper.RSA_PUBLIC_KEY,
                IdUtil.simpleUUID());

    }

    /**
     * 执行
     */
    private static void exec(String apiEndpoint, String adminSignInName, String adminPassword, String rsaPublicKey,
                             String sysMenuName) {

        // 登录名-用户名账号密码登录
        SignInVO signInVO =
                ApiTestSignSignInNameUtil.signInNameSignIn(apiEndpoint, adminSignInName, adminPassword, rsaPublicKey);

        String jwt = signInVO.getJwt();

        // 菜单-新增/修改
        SysMenuInsertOrUpdateDTO parentDTO =
                sysMenuInsertOrUpdate(apiEndpoint, jwt, getSysMenuInsertOrUpdateDTO(apiEndpoint, jwt, sysMenuName, 0L));

        // 菜单-分页排序查询
        Page<SysMenuDO> sysMenuDOPage = sysMenuPage(apiEndpoint, jwt, parentDTO);

        SysMenuDO sysMenuDOParentByPage = null;

        for (SysMenuDO item : sysMenuDOPage.getRecords()) {

            if (item.getName().equals(parentDTO.getName())) {

                sysMenuDOParentByPage = item;
                break;

            }

        }

        if (sysMenuDOParentByPage == null) {
            log.info("sysMenuDOParentByPage 等于null，结束");
            return;
        }

        // 菜单-新增/修改
        SysMenuInsertOrUpdateDTO childrenDTO = sysMenuInsertOrUpdate(apiEndpoint, jwt,
                getSysMenuInsertOrUpdateDTO(apiEndpoint, jwt, IdUtil.simpleUUID(), sysMenuDOParentByPage.getId()));

        // 菜单-分页排序查询
        sysMenuDOPage = sysMenuPage(apiEndpoint, jwt, childrenDTO);

        SysMenuDO sysMenuDOChildrenByPage = null;

        for (SysMenuDO item : sysMenuDOPage.getRecords()) {

            if (item.getName().equals(childrenDTO.getName())) {

                sysMenuDOChildrenByPage = item;
                break;

            }

        }

        if (sysMenuDOChildrenByPage == null) {
            log.info("sysMenuDOChildrenByPage 等于null，结束");
            return;
        }

        // 查询：树结构
        sysMenuTree(apiEndpoint, jwt, childrenDTO);

        Long parentId = sysMenuDOParentByPage.getId();

        // 菜单-通过主键id，查看详情
        SysMenuDO sysMenuDOParentById = sysMenuInfoById(apiEndpoint, jwt, parentId);

        if (sysMenuDOParentById == null) {
            log.info("sysMenuDOParentById 等于null，结束");
            return;
        }

        Long childrenId = sysMenuDOChildrenByPage.getId();

        // 字典-通过主键id，查看详情
        SysMenuDO sysMenuChildrenDOById = sysMenuInfoById(apiEndpoint, jwt, childrenId);

        if (sysMenuChildrenDOById == null) {
            log.info("sysMenuChildrenDOById 等于null，结束");
            return;
        }

        // 通过主键 idSet，加减排序号
        sysMenuAddOrderNo(apiEndpoint, jwt, CollUtil.newHashSet(parentId, childrenId));

        // 菜单-通过主键id，查看详情
        sysMenuInfoById(apiEndpoint, jwt, parentId);

        // 菜单-通过主键id，查看详情
        sysMenuInfoById(apiEndpoint, jwt, childrenId);

        // 菜单-获取：当前用户绑定的菜单
        sysMenuUserSelfMenuList(apiEndpoint, jwt);

        // 菜单-批量删除
        sysMenuDeleteByIdSet(apiEndpoint, jwt, CollUtil.newHashSet(childrenId));
        sysMenuDeleteByIdSet(apiEndpoint, jwt, CollUtil.newHashSet(parentId));

    }

    /**
     * 菜单-获取：当前用户绑定的菜单
     */
    public static void sysMenuUserSelfMenuList(String apiEndpoint, String jwt) {

        long currentTs = System.currentTimeMillis();

        String bodyStr =
                HttpRequest.post(apiEndpoint + "/sys/menu/userSelfMenuList").header("Authorization", jwt).execute().body();

        log.info("菜单-获取：当前用户绑定的菜单：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

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
    public static Page<SysMenuDO> sysMenuPage(String apiEndpoint, String jwt, SysMenuInsertOrUpdateDTO dto) {

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
    private static SysMenuInsertOrUpdateDTO sysMenuInsertOrUpdate(String apiEndpoint, String jwt,
                                                                  SysMenuInsertOrUpdateDTO dto) {

        long currentTs = System.currentTimeMillis();

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/menu/insertOrUpdate").body(JSONUtil.toJsonStr(dto))
                .header("Authorization", jwt).execute().body();

        log.info("菜单-新增/修改：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        return dto;

    }

    /**
     * 获取：对象
     */
    @NotNull
    private static SysMenuInsertOrUpdateDTO getSysMenuInsertOrUpdateDTO(String apiEndpoint, String jwt,
                                                                        String sysMenuName, long parentId) {

        Page<SysRoleDO> sysRoleDOPage =
                ApiTestSysRoleUtil.sysRolePage(apiEndpoint, jwt, new SysRoleInsertOrUpdateDTO());

        Set<Long> roleIdSet = sysRoleDOPage.getRecords().stream().map(BaseEntity::getId).collect(Collectors.toSet());

        SysMenuInsertOrUpdateDTO dto = new SysMenuInsertOrUpdateDTO();

        dto.setParentId(parentId);
        dto.setName(sysMenuName);
        dto.setPath("/" + IdUtil.simpleUUID());
        dto.setRouter(IdUtil.simpleUUID());
        dto.setIcon("");
        dto.setAuths("");
        dto.setAuthFlag(false);
        dto.setRoleIdSet(roleIdSet);
        dto.setEnableFlag(true);
        dto.setFirstFlag(false);
        dto.setOrderNo(RandomUtil.randomInt(1000, 90000000));
        dto.setShowFlag(true);
        dto.setRedirect("");
        dto.setRemark("");

        return dto;

    }

}
