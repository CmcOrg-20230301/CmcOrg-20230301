package com.cmcorg20230301.engine.be.xxl.job.task;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProgramIsRunningTask {

    @XxlJob("programIsRunningTask")
    public void programIsRunningTask() {
        XxlJobHelper.handleSuccess("项目运行中 (*^▽^*)");
    }

}
