package com.cmcorg20230301.be.engine.im.session.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionContentMapper;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionMapper;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentListDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentSendTextDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentSendTextListDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionContentDO;
import com.cmcorg20230301.be.engine.im.session.model.enums.SysImSessionContentTypeEnum;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionContentService;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyPageUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SysImSessionContentServiceImpl extends ServiceImpl<SysImSessionContentMapper, SysImSessionContentDO>
        implements SysImSessionContentService {

    private static SysImSessionMapper sysImSessionMapper;

    @Resource
    public void setSysImSessionMapper(SysImSessionMapper sysImSessionMapper) {
        SysImSessionContentServiceImpl.sysImSessionMapper = sysImSessionMapper;
    }

    /**
     * 发送内容
     */
    @Override
    public String sendText(SysImSessionContentSendTextListDTO dto) {

        // 检查：sessionId是否合法
        Long sessionId = checkSessionId(dto.getSessionId());

        List<SysImSessionContentDO> insertList = new ArrayList<>();

        int type = SysImSessionContentTypeEnum.TEXT.getCode();

        // 创建时间不能低于该值
        long checkTs = System.currentTimeMillis() - BaseConstant.YEAR_30_EXPIRE_TIME;

        for (SysImSessionContentSendTextDTO item : dto.getContentList()) {

            if (StrUtil.isBlank(item.getContent())) {
                continue;
            }

            if (item.getCreateTs() == null || item.getCreateTs() < checkTs) {
                continue;
            }

            Date date = new Date(item.getCreateTs());

            SysImSessionContentDO sysImSessionContentDO = new SysImSessionContentDO();

            sysImSessionContentDO.setSessionId(sessionId);

            sysImSessionContentDO.setContent(item.getContent());

            sysImSessionContentDO.setShowFlag(true);

            sysImSessionContentDO.setType(type);

            sysImSessionContentDO.setEnableFlag(true);

            sysImSessionContentDO.setDelFlag(false);

            sysImSessionContentDO.setRemark("");

            sysImSessionContentDO.setCreateTime(date);

            sysImSessionContentDO.setUpdateTime(date);

            sysImSessionContentDO.setCreateTs(item.getCreateTs());

            insertList.add(sysImSessionContentDO);

        }

        saveBatch(insertList);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 检查：sessionId是否合法
     */
    @NotNull
    public static Long checkSessionId(Long sessionId) {

        if (sessionId == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, null);
        }

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        // 检查：sessionId，是否属于当前租户
        boolean exists = ChainWrappers.lambdaQueryChain(sysImSessionMapper).eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(BaseEntity::getId, sessionId).exists();

        if (!exists) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, sessionId);
        }

        return sessionId;

    }

    /**
     * 查询会话内容
     */
    @Override
    public Page<SysImSessionContentDO> scrollPage(SysImSessionContentListDTO dto) {

        // 检查：sessionId是否合法
        Long sessionId = checkSessionId(dto.getSessionId());

        Long id = dto.getId();

        boolean backwardFlag = BooleanUtil.isTrue(dto.getBackwardFlag());

        if (id == null) {

            if (backwardFlag) { // 最小的 id

                id = Long.MIN_VALUE;

            } else { // 最大的 id

                id = Long.MAX_VALUE;

            }

        }

        if (backwardFlag) { // 往后查询

            return lambdaQuery().gt(BaseEntity::getId, id).page(MyPageUtil.getScrollPage(dto.getPageSize()));

        } else { // 往前查询

            return lambdaQuery().lt(BaseEntity::getId, id).page(MyPageUtil.getScrollPage(dto.getPageSize()));

        }

    }

}
