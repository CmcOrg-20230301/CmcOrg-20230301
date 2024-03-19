package com.cmcorg20230301.be.engine.other.app.wx.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.other.app.mapper.SysOtherAppMapper;
import com.cmcorg20230301.be.engine.other.app.mapper.SysOtherAppOfficialAccountMenuMapper;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppDO;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppOfficialAccountMenuDO;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppOfficialAccountMenuButtonTypeEnum;
import com.cmcorg20230301.be.engine.other.app.wx.model.bo.SysOtherAppOfficialAccountMenuWxBO;
import com.cmcorg20230301.be.engine.other.app.wx.model.vo.WxBaseVO;
import com.cmcorg20230301.be.engine.other.app.wx.service.SysWxService;
import com.cmcorg20230301.be.engine.other.app.wx.util.WxUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityTree;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyTreeUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = LogTopicConstant.OTHER_APP_WX)
public class SysWxServiceImpl implements SysWxService {

    @Resource
    SysOtherAppMapper sysOtherAppMapper;

    @Resource
    SysOtherAppOfficialAccountMenuMapper sysOtherAppOfficialAccountMenuMapper;

    /**
     * 微信公众号：同步菜单
     */
    @Override
    public String officialAccountUpdateMenu(NotNullId notNullId) {

        Set<Long> userRefTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        SysOtherAppDO sysOtherAppDO = ChainWrappers.lambdaQueryChain(sysOtherAppMapper)
            .in(BaseEntityNoIdSuper::getTenantId, userRefTenantIdSet).eq(BaseEntity::getId, notNullId.getId())
            .select(BaseEntityNoIdSuper::getTenantId, SysOtherAppDO::getAppId).one();

        if (sysOtherAppDO == null) {
            return BaseBizCodeEnum.OK;
        }

        String accessToken = WxUtil.getAccessToken(sysOtherAppDO.getTenantId(), sysOtherAppDO.getAppId());

        List<SysOtherAppOfficialAccountMenuDO> sysOtherAppOfficialAccountMenuDOList =
            ChainWrappers.lambdaQueryChain(sysOtherAppOfficialAccountMenuMapper)
                .eq(SysOtherAppOfficialAccountMenuDO::getOtherAppId, notNullId.getId())
                .eq(BaseEntityNoId::getEnableFlag, true).orderByDesc(BaseEntityTree::getOrderNo).list();

        // 组装成：树结构
        List<SysOtherAppOfficialAccountMenuDO> tree = MyTreeUtil.listToTree(sysOtherAppOfficialAccountMenuDOList);

        List<SysOtherAppOfficialAccountMenuWxBO> buttonList = new LinkedList<>();

        for (SysOtherAppOfficialAccountMenuDO item : tree) {

            // 获取：SysOtherAppOfficialAccountMenuWxBO 对象
            SysOtherAppOfficialAccountMenuWxBO sysOtherAppOfficialAccountMenuWxBO =
                getSysOtherAppOfficialAccountMenuWxBO(item);

            if (CollUtil.isNotEmpty(item.getChildren())) {

                sysOtherAppOfficialAccountMenuWxBO.setSubButton(new LinkedList<>());

                for (SysOtherAppOfficialAccountMenuDO subItem : item.getChildren()) {

                    // 添加：子级菜单
                    sysOtherAppOfficialAccountMenuWxBO.getSubButton()
                        .add(getSysOtherAppOfficialAccountMenuWxBO(subItem));

                }

            }

            buttonList.add(sysOtherAppOfficialAccountMenuWxBO);

        }

        JSONObject body = JSONUtil.createObj();

        body.set("button", buttonList);

        // 组装成：微信菜单结构
        String result = HttpRequest.post("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken)
            .body(JSONUtil.toJsonStr(body)).execute().body();

        WxBaseVO wxBaseVO = JSONUtil.toBean(result, WxBaseVO.class);

        // 检查：微信回调 vo对象
        if (!WxUtil.checkWxVO(wxBaseVO)) {

            ApiResultVO.error("同步失败，原因：" + wxBaseVO.getErrmsg(), wxBaseVO.getErrcode());

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 获取：SysOtherAppOfficialAccountMenuWxBO 对象
     */
    @NotNull
    private SysOtherAppOfficialAccountMenuWxBO
        getSysOtherAppOfficialAccountMenuWxBO(SysOtherAppOfficialAccountMenuDO item) {

        SysOtherAppOfficialAccountMenuWxBO sysOtherAppOfficialAccountMenuWxBO =
            new SysOtherAppOfficialAccountMenuWxBO();

        sysOtherAppOfficialAccountMenuWxBO.setName(item.getName());

        if (SysOtherAppOfficialAccountMenuButtonTypeEnum.VIEW.equals(item.getButtonType())) {

            sysOtherAppOfficialAccountMenuWxBO
                .setType(SysOtherAppOfficialAccountMenuWxBO.SysOtherAppOfficialAccountMenuWxType.view);

            sysOtherAppOfficialAccountMenuWxBO.setUrl(item.getValue());

        } else if (SysOtherAppOfficialAccountMenuButtonTypeEnum.MINIPROGRAM.equals(item.getButtonType())) {

            sysOtherAppOfficialAccountMenuWxBO
                .setType(SysOtherAppOfficialAccountMenuWxBO.SysOtherAppOfficialAccountMenuWxType.click);

            sysOtherAppOfficialAccountMenuWxBO.setAppid(item.getValue());
            sysOtherAppOfficialAccountMenuWxBO.setUrl("http://mp.weixin.qq.com");
            sysOtherAppOfficialAccountMenuWxBO.setPagepath(item.getPagepath());

        } else {

            sysOtherAppOfficialAccountMenuWxBO
                .setType(SysOtherAppOfficialAccountMenuWxBO.SysOtherAppOfficialAccountMenuWxType.click);

            sysOtherAppOfficialAccountMenuWxBO.setKey(item.getValue());

        }

        return sysOtherAppOfficialAccountMenuWxBO;

    }

}
