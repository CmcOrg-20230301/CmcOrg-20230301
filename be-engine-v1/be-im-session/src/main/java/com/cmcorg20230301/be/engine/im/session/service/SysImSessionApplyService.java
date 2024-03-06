package com.cmcorg20230301.be.engine.im.session.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.im.session.model.dto.*;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionApplyDO;
import com.cmcorg20230301.be.engine.im.session.model.vo.SysImSessionApplyPrivateChatApplySelfPageVO;
import com.cmcorg20230301.be.engine.im.session.model.vo.SysImSessionApplyPrivateChatFindNewPageVO;
import com.cmcorg20230301.be.engine.im.session.model.vo.SysImSessionApplyPrivateChatSelfPageVO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;

public interface SysImSessionApplyService extends IService<SysImSessionApplyDO> {

    Page<SysImSessionApplyPrivateChatFindNewPageVO> privateChatFindNewPage(SysImSessionApplyPrivateChatFindNewPageDTO dto);

    Page<SysImSessionApplyPrivateChatApplySelfPageVO> privateChatApplyPageSelf(SysImSessionApplyPrivateChatApplySelfPageDTO dto);

    Page<SysImSessionApplyPrivateChatSelfPageVO> privateChatPageSelf(SysImSessionApplyPrivateChatSelfPageDTO dto);

    String privateChatApply(SysImSessionApplyPrivateChatApplyDTO dto);

    String privateChatAgree(NotEmptyIdSet notEmptyIdSet);

    String privateChatReject(SysImSessionApplyPrivateChatRejectDTO dto);

    String privateChatBlock(NotNullId notNullId);

    String privateChatBlockCancel(NotEmptyIdSet notEmptyIdSet);

    String privateChatApplyCancel(NotNullId notNullId);

    String privateChatApplyHidden(NotNullId notNullId);

    String privateChatDelete(NotNullId notNullId);

}
