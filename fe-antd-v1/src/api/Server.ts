export interface ServerWorkInfoVO {
    diskAvailable?: string // 磁盘可以使用总量（字节），format：int64
    cpuUsed?: string // CPU使用率（0-100），format：int64
    memoryAvailable?: string // 系统可用内存（字节），format：int64
    memoryTotal?: string // 系统总内存（字节），format：int64
    diskUsed?: string // 磁盘已经使用总量（字节），format：int64
    jvmTotalMemory?: string // JVM中内存总大小（字节），format：int64
    diskTotal?: string // 磁盘总量（字节），format：int64
    jvmFreeMemory?: string // JVM中内存剩余大小（字节），format：int64
    memoryUsed?: string // 系统已经使用内存（字节），format：int64
    cpuFree?: string // CPU空闲率（0-100），format：int64
    jvmUsedMemory?: string // JVM中内存已经使用大小（字节），format：int64
}
