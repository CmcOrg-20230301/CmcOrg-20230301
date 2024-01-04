package com.cmcorg20230301.be.engine.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.be.engine.security.service.BaseSysUserInfoService;
import org.springframework.stereotype.Service;

@Service
public class BaseSysUserInfoServiceImpl extends ServiceImpl<SysUserInfoMapper, SysUserInfoDO>
        implements BaseSysUserInfoService {

}
