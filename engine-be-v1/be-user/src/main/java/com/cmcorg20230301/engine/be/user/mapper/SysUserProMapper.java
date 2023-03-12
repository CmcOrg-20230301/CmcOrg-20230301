package com.cmcorg20230301.engine.be.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.engine.be.security.model.entity.SysUserDO;
import com.cmcorg20230301.engine.be.user.model.dto.SysUserPageDTO;
import com.cmcorg20230301.engine.be.user.model.vo.SysUserPageVO;
import org.apache.ibatis.annotations.Param;

public interface SysUserProMapper extends BaseMapper<SysUserDO> {

    // 分页排序查询
    Page<SysUserPageVO> myPage(@Param("page") Page<SysUserPageVO> page, @Param("dto") SysUserPageDTO dto);

}
