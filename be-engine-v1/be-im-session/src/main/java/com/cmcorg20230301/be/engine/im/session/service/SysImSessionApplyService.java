package com.cmcorg20230301.be.engine.im.session.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionApplyPrivateChatApplyDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionApplyPrivateChatApplyUserPageDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionApplyPrivateChatRejectDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionApplyPrivateChatSelfPageDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionApplyDO;
import com.cmcorg20230301.be.engine.im.session.model.vo.SysImSessionApplyPrivateChatApplyUserPageVO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;

public interface SysImSessionApplyService extends IService<SysImSessionApplyDO> {

    Page<SysImSessionApplyPrivateChatApplyUserPageVO> privateChatApplyUserPage(SysImSessionApplyPrivateChatApplyUserPageDTO dto);

    Page<SysImSessionApplyDO> privateChatMyPageSelf(SysImSessionApplyPrivateChatSelfPageDTO dto);

    String privateChatApply(SysImSessionApplyPrivateChatApplyDTO dto);

    String privateChatAgree(NotEmptyIdSet notEmptyIdSet);

    String privateChatReject(SysImSessionApplyPrivateChatRejectDTO dto);

    String privateChatBlock(NotNullId notNullId);

    String privateChatBlockCancel(NotEmptyIdSet notEmptyIdSet);

    String privateChatApplyCancel(NotNullId notNullId);

    String privateChatApplyHidden(NotNullId notNullId);

    String privateChatDelete(NotNullId notNullId);

}
