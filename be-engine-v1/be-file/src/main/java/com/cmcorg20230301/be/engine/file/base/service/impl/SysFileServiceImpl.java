package com.cmcorg20230301.be.engine.file.base.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.file.base.mapper.SysFileMapper;
import com.cmcorg20230301.be.engine.file.base.model.bo.SysFileUploadBO;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFilePageDTO;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFilePageSelfDTO;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFileUploadDTO;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileDO;
import com.cmcorg20230301.be.engine.file.base.service.SysFileService;
import com.cmcorg20230301.be.engine.file.base.util.SysFileUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.LongObjectMapVO;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.model.enums.SysUserTenantEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.ResponseUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.util.util.CallBack;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFileDO> implements SysFileService {

    /**
     * 上传文件：公有和私有
     */
    @Override
    public Long upload(SysFileUploadDTO dto) {

        Long currentUserId = UserUtil.getCurrentUserId();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SysFileUploadBO sysFileUploadBO = new SysFileUploadBO();

        sysFileUploadBO.setFile(dto.getFile());
        sysFileUploadBO.setUploadType(dto.getUploadType());
        sysFileUploadBO.setRemark(dto.getRemark());
        sysFileUploadBO.setExtraJson(dto.getExtraJson());

        sysFileUploadBO.setRefId(dto.getRefId());

        sysFileUploadBO.setUserId(currentUserId);
        sysFileUploadBO.setTenantId(currentTenantIdDefault);

        // 执行：上传
        return SysFileUtil.upload(sysFileUploadBO);

    }

    /**
     * 下载文件：私有
     */
    @SneakyThrows
    @Override
    public void privateDownload(NotNullId notNullId, HttpServletResponse response) {

        CallBack<String> fileNameCallBack = new CallBack<>();

        InputStream inputStream = SysFileUtil.privateDownload(notNullId.getId(), fileNameCallBack);

        if (inputStream == null) {
            ApiResultVO.errorMsg("操作失败：文件流获取失败");
        }

        ResponseUtil.getOutputStream(response, fileNameCallBack.getValue());

        // 推送
        ResponseUtil.flush(response, inputStream);

    }

    /**
     * 批量删除文件：公有和私有
     */
    @Override
    public String removeByFileIdSet(NotEmptyIdSet notEmptyIdSet) {

        if (CollUtil.isEmpty(notEmptyIdSet.getIdSet())) {
            return BaseBizCodeEnum.OK;
        }

        SysFileUtil.removeByFileIdSet(notEmptyIdSet.getIdSet(), true);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 批量获取：公开文件的 url
     */
    @Override
    public LongObjectMapVO<String> getPublicUrl(NotEmptyIdSet notEmptyIdSet) {

        return new LongObjectMapVO<>(SysFileUtil.getPublicUrl(notEmptyIdSet.getIdSet()));

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysFileDO> myPage(SysFilePageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        return lambdaQuery()
            .like(StrUtil.isNotBlank(dto.getOriginFileName()), SysFileDO::getOriginFileName, dto.getOriginFileName())

            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())

            .eq(dto.getBelongId() != null, SysFileDO::getBelongId, dto.getBelongId())

            .eq(dto.getUploadType() != null, SysFileDO::getUploadType, dto.getUploadType())

            .eq(dto.getStorageType() != null, SysFileDO::getStorageType, dto.getStorageType())

            .eq(dto.getPublicFlag() != null, SysFileDO::getPublicFlag, dto.getPublicFlag())

            .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())

            .eq(dto.getRefId() != null, SysFileDO::getRefId, dto.getRefId())

            .ne(SysUserTenantEnum.USER.equals(dto.getSysUserTenantEnum()), SysFileDO::getBelongId,
                BaseConstant.TENANT_USER_ID) //

            .eq(SysUserTenantEnum.TENANT.equals(dto.getSysUserTenantEnum()), SysFileDO::getBelongId,
                BaseConstant.TENANT_USER_ID) //

            .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //

            .select(BaseEntity::getId, BaseEntityNoIdFather::getTenantId, BaseEntityNoId::getEnableFlag,
                BaseEntityNoId::getRemark, BaseEntityNoIdFather::getCreateId, BaseEntityNoIdFather::getCreateTime,
                BaseEntityNoIdFather::getUpdateId, BaseEntityNoIdFather::getUpdateTime, SysFileDO::getOriginFileName,
                SysFileDO::getBelongId, SysFileDO::getUploadType, SysFileDO::getStorageType, SysFileDO::getPublicFlag,
                SysFileDO::getFileSize, SysFileDO::getExtraJson).orderByDesc(BaseEntity::getUpdateTime)

            .page(dto.page(true));

    }

    /**
     * 分页排序查询-自我
     */
    @Override
    public Page<SysFileDO> myPageSelf(SysFilePageSelfDTO dto) {

        SysFilePageDTO sysFilePageDTO = BeanUtil.copyProperties(dto, SysFilePageDTO.class);

        Long currentUserId = UserUtil.getCurrentUserId();

        sysFilePageDTO.setBelongId(currentUserId); // 设置为：当前用户

        // 执行
        return myPage(sysFilePageDTO);

    }

    /**
     * 分页排序查询-租户
     */
    @Override
    public Page<SysFileDO> myPageTenant(SysFilePageSelfDTO dto) {

        SysFilePageDTO sysFilePageDTO = BeanUtil.copyProperties(dto, SysFilePageDTO.class);

        sysFilePageDTO.setBelongId(BaseConstant.TENANT_USER_ID); // 设置为：租户用户 id

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        sysFilePageDTO.setTenantIdSet(CollUtil.newHashSet(currentTenantIdDefault)); // 设置为：当前租户

        // 执行
        return myPage(sysFilePageDTO);

    }

}




