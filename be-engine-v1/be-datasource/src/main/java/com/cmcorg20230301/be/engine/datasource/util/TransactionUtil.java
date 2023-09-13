package com.cmcorg20230301.be.engine.datasource.util;

import cn.hutool.core.lang.func.VoidFunc0;
import com.baomidou.dynamic.datasource.tx.LocalTxUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class TransactionUtil {

    /**
     * 携带事务，执行方法
     */
    @SneakyThrows
    public static void exec(VoidFunc0 voidFunc0) {

        // 开启事务
        String xid = LocalTxUtil.startTransaction();

        try {

            voidFunc0.call();

            LocalTxUtil.commit(xid); // 提交

        } catch (Exception e) {

            LocalTxUtil.rollback(xid); // 回滚

            throw e;

        }

    }

    /**
     * 携带事务，执行方法
     */
    @SneakyThrows
    public static <T> T exec(Supplier<T> supplier) {

        // 开启事务
        String xid = LocalTxUtil.startTransaction();

        try {

            T resObject = supplier.get();

            LocalTxUtil.commit(xid); // 提交

            return resObject;

        } catch (Exception e) {

            LocalTxUtil.rollback(xid); // 回滚

            throw e;

        }

    }

}
