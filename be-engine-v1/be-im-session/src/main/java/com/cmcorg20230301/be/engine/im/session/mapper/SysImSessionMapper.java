package com.cmcorg20230301.be.engine.im.session.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionUserSelfPageDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysImSessionMapper extends BaseMapper<SysImSessionDO> {

    // 分页排序查询
    Page<SysImSessionDO> myPageSelf(@Param("page") Page<SysImSessionDO> page, @Param("dto") SysImSessionUserSelfPageDTO dto);

}
