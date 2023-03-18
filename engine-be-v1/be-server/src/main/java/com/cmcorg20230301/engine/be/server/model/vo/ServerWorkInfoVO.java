package com.cmcorg20230301.engine.be.server.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ServerWorkInfoVO {

    @Schema(description = "JVM中内存总大小（字节）")
    private Long jvmTotalMemory;

    @Schema(description = "JVM中内存剩余大小（字节）")
    private Long jvmFreeMemory;

    @Schema(description = "JVM中内存已经使用大小（字节）")
    private Long jvmUsedMemory;

    @Schema(description = "系统总内存（字节）")
    private Long memoryTotal;

    @Schema(description = "系统可用内存（字节）")
    private Long memoryAvailable;

    @Schema(description = "系统已经使用内存（字节）")
    private Long memoryUsed;

    @Schema(description = "CPU空闲率（0-100）")
    private Long cpuFree;

    @Schema(description = "CPU使用率（0-100）")
    private Long cpuUsed;

    @Schema(description = "磁盘总量（字节）")
    private Long diskTotal;

    @Schema(description = "磁盘可以使用总量（字节）")
    private Long diskAvailable;

    @Schema(description = "磁盘已经使用总量（字节）")
    private Long diskUsed;

}
