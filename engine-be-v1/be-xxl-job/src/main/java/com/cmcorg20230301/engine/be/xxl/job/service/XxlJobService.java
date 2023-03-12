package com.cmcorg20230301.engine.be.xxl.job.service;

import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.xxl.job.dto.XxlJobInsertDTO;

public interface XxlJobService {

    Long insert(XxlJobInsertDTO dto);

    void deleteById(NotNullId notNullId);

}
