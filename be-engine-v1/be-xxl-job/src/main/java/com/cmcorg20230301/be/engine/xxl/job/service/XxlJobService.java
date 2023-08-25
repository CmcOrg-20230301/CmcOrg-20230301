package com.cmcorg20230301.be.engine.xxl.job.service;

import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.xxl.job.model.dto.XxlJobInsertDTO;

public interface XxlJobService {

    Long insert(XxlJobInsertDTO dto);

    void deleteById(NotNullId notNullId);

}
