package com.cmcorg20230301.engine.be.file.base.service.impl;

import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.engine.be.file.base.mapper.SysFileMapper;
import com.cmcorg20230301.engine.be.file.base.model.entity.SysFileDO;
import com.cmcorg20230301.engine.be.file.base.service.SysFileService;
import com.cmcorg20230301.engine.be.file.base.util.SysFileUtil;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.model.model.vo.LongObjectMapVO;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.model.dto.SysFileUploadDTO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFileDO> implements SysFileService {

    /**
     * 上传文件：共有和私有
     */
    @Override
    public String upload(SysFileUploadDTO dto) {

        SysFileUtil.upload(dto);

        return BaseBizCodeEnum.OK;

    }

    /**
     * 下载文件：私有
     */
    @SneakyThrows
    @Override
    public void privateDownload(NotNullId notNullId, HttpServletResponse response) {

        InputStream inputStream = SysFileUtil.privateDownload(notNullId.getId());

        if (inputStream == null) {
            ApiResultVO.error("操作失败：文件流获取失败");
        }

        ServletOutputStream outputStream = response.getOutputStream();

        IoUtil.copy(inputStream, outputStream);

        outputStream.flush();

        IoUtil.close(inputStream);

        IoUtil.close(outputStream);

    }

    /**
     * 批量删除文件：共有和私有
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




