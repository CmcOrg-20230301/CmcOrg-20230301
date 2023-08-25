package com.cmcorg20230301.be.engine.file.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.file.base.mapper.SysFileMapper;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFileUploadDTO;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileDO;
import com.cmcorg20230301.be.engine.file.base.service.SysFileService;
import com.cmcorg20230301.be.engine.file.base.util.SysFileUtil;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.LongObjectMapVO;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.ResponseUtil;
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

        return SysFileUtil.upload(dto);

    }

    /**
     * 下载文件：私有
     */
    @SneakyThrows
    @Override
    public void privateDownload(NotNullId notNullId, HttpServletResponse response) {

        InputStream inputStream = SysFileUtil.privateDownload(notNullId.getId());

        if (inputStream == null) {
            ApiResultVO.errorMsg("操作失败：文件流获取失败");
        }

        // 推送
        ResponseUtil.flush(response, inputStream);

    }

    /**
     * 批量删除文件：公有和私有
     */
    @Override
    public String removeByFileIdSet(NotEmptyIdSet notEmptyIdSet) {

        SysFileUtil.removeByFileIdSet(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.OK;

    }

    /**
     * 批量获取：公开文件的 url
     */
    @Override
    public LongObjectMapVO<String> getPublicUrl(NotEmptyIdSet notEmptyIdSet) {

        return new LongObjectMapVO<>(SysFileUtil.getPublicUrl(notEmptyIdSet.getIdSet()));

    }

}




