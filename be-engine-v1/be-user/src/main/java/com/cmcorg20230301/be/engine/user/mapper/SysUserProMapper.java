package com.cmcorg20230301.be.engine.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.user.model.dto.SysUserPageDTO;
import com.cmcorg20230301.be.engine.user.model.vo.SysUserPageVO;

@Mapper
public interface SysUserProMapper extends BaseMapper<SysUserDO> {

    // 分页排序查询
    Page<SysUserPageVO> myPage(@Param("page") Page<SysUserPageVO> page, @Param("dto") SysUserPageDTO dto);

}
