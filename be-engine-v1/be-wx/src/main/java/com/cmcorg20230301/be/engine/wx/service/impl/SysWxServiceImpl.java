package com.cmcorg20230301.be.engine.wx.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.other.app.mapper.SysOtherAppMapper;
import com.cmcorg20230301.be.engine.other.app.mapper.SysOtherAppOfficialAccountMenuMapper;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppDO;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppOfficialAccountMenuDO;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppOfficialAccountMenuButtonTypeEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityTree;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyTreeUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.wx.model.bo.SysOtherAppOfficialAccountMenuWxBO;
import com.cmcorg20230301.be.engine.wx.model.vo.WxBaseVO;
import com.cmcorg20230301.be.engine.wx.service.SysWxService;
import com.cmcorg20230301.be.engine.wx.util.WxUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j(topic = LogTopicConstant.OTHER_APP_WX)
public class SysWxServiceImpl implements SysWxService {

    @Resource
    SysOtherAppMapper sysOtherAppMapper;

    @Resource
    SysOtherAppOfficialAccountMenuMapper sysOtherAppOfficialAccountMenuMapper;

    /**
     * 微信公众号：更新菜单
     */
    @Override
    public String officialAccountUpdateMenu(NotNullId notNullId) {

        Set<Long> userRefTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        SysOtherAppDO sysOtherAppDO =
            ChainWrappers.lambdaQueryChain(sysOtherAppMapper).in(BaseEntityNoIdFather::getTenantId, userRefTenantIdSet)
                .eq(BaseEntity::getId, notNullId.getId()).one();

        if (sysOtherAppDO == null) {
            return BaseBizCodeEnum.OK;
        }

        String accessToken = WxUtil.getAccessToken(sysOtherAppDO.getTenantId(), sysOtherAppDO.getAppId());

        List<SysOtherAppOfficialAccountMenuDO> sysOtherAppOfficialAccountMenuDOList =
            ChainWrappers.lambdaQueryChain(sysOtherAppOfficialAccountMenuMapper)
                .eq(SysOtherAppOfficialAccountMenuDO::getOtherAppId, notNullId.getId())
                .orderByDesc(BaseEntityTree::getOrderNo).list();

        // 组装成：树结构
        List<SysOtherAppOfficialAccountMenuDO> tree = MyTreeUtil.listToTree(sysOtherAppOfficialAccountMenuDOList);

        List<SysOtherAppOfficialAccountMenuWxBO> buttonList = new LinkedList<>();

        for (SysOtherAppOfficialAccountMenuDO item : tree) {

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

    @NotNull
    private SysOtherAppOfficialAccountMenuWxBO getSysOtherAppOfficialAccountMenuWxBO(
        SysOtherAppOfficialAccountMenuDO item) {

        SysOtherAppOfficialAccountMenuWxBO sysOtherAppOfficialAccountMenuWxBO =
            new SysOtherAppOfficialAccountMenuWxBO();

        sysOtherAppOfficialAccountMenuWxBO.setName(item.getName());

        if (item.getButtonType().equals(SysOtherAppOfficialAccountMenuButtonTypeEnum.VIEW)) {

            sysOtherAppOfficialAccountMenuWxBO
                .setType(SysOtherAppOfficialAccountMenuWxBO.SysOtherAppOfficialAccountMenuWxType.view);

            sysOtherAppOfficialAccountMenuWxBO.setUrl(item.getValue());

        } else {

            sysOtherAppOfficialAccountMenuWxBO
                .setType(SysOtherAppOfficialAccountMenuWxBO.SysOtherAppOfficialAccountMenuWxType.click);

            sysOtherAppOfficialAccountMenuWxBO.setKey(item.getValue());

        }

        return sysOtherAppOfficialAccountMenuWxBO;

    }

}
