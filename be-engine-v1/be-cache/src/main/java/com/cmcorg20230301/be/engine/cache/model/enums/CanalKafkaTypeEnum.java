package com.cmcorg20230301.be.engine.cache.model.enums;

import cn.hutool.core.collection.CollUtil;

import java.util.Set;

/**
 * canal的消息类型枚举
 */
public enum CanalKafkaTypeEnum {

    INSERT, // 新增数据
    UPDATE, // 修改数据
    DELETE, // 删除数据，清空表
    CREATE, // 创建表
    ALTER, // 修改表结构：新增，删除，修改 字段，删除外键（新增外键没有）
    ERASE, // 删除表
    QUERY, // 新建，删除，数据库
    TRUNCATE, // 截断表
    RENAME, // 修改表名称
    CINDEX, // 创建索引
    DINDEX, // 删除索引
    GTID, //
    XACOMMIT, // 事务相关的暂时没有
    XAROLLBACK, //
    MHEARTBEAT, //

    ;

    private static final Set<CanalKafkaTypeEnum> DATE_UPDATE_SET =
        CollUtil.newHashSet(INSERT, UPDATE, DELETE, ALTER, ERASE, TRUNCATE, RENAME, CREATE);

    public boolean dateUpdateFlag() {
        return DATE_UPDATE_SET.contains(this);
    }

}
