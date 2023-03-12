package com.cmcorg20230301.engine.be.xxl.job.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class XxlJobInsertDTO {

    @Schema(description = "任务描述")
    private String jobDesc;

    @Schema(description = "发送时间（本系统定义），会转换为 Cron 表达式，并赋值给 scheduleConf")
    private Date proSendTime;

    @Schema(description = "JobHandler")
    private String executorHandler;

    @Schema(description = "任务参数，注意：这里必须是 json格式")
    private String executorParam = "{}";

    // 分割线，上面是建议必须设置的值 ↑ 下面是一般情况都不会填的值 ↓ =========

    @Schema(description = "任务 id，备注：新增时不要设值，用于启动 任务时用")
    private Integer id;

    @Schema(description = "执行器 id，备注：不指定时，自动获取第一个，如果一个都没有，则会自动创建")
    private String jobGroup;

    @Schema(description = "负责人")
    private String author = "admin";

    @Schema(description = "报警邮件")
    private String alarmEmail = "dimensional_logic@qq.com";

    @Schema(description = "调度类型")
    private String scheduleType = "CRON";

    @Schema(description = "Cron 表达式")
    private String scheduleConf;

    @Schema(description = "运行模式")
    private String glueType = "BEAN";

    @Schema(description = "路由策略")
    private String executorRouteStrategy = "FAILOVER"; // FAILOVER 故障转移

    @Schema(description = "调度过期策略")
    private String misfireStrategy = "DO_NOTHING"; // DO_NOTHING 忽略 FIRE_ONCE_NOW 立即执行一次

    /**
     * {@link com.xxl.job.core.enums.ExecutorBlockStrategyEnum}
     */
    @Schema(description = "阻塞处理策略")
    private String executorBlockStrategy = "SERIAL_EXECUTION"; // SERIAL_EXECUTION 单机串行

    @Schema(description = "任务超时时间，单位 秒，大于零时生效")
    private int executorTimeout;

    @Schema(description = "失败重试次数")
    private int executorFailRetryCount;

    @Schema(description = "不用设值", hidden = true)
    private String glueRemark = "GLUE代码初始化";

}
