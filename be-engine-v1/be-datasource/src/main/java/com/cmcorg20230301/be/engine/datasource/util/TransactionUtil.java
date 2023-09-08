package com.cmcorg20230301.be.engine.datasource.util;

import cn.hutool.core.lang.func.VoidFunc0;
import lombok.SneakyThrows;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.function.Supplier;

@Component
public class TransactionUtil {

    private static DataSourceTransactionManager dataSourceTransactionManager;
    private static TransactionDefinition transactionDefinition;

    public TransactionUtil(DataSourceTransactionManager dataSourceTransactionManager,
        TransactionDefinition transactionDefinition) {

        TransactionUtil.dataSourceTransactionManager = dataSourceTransactionManager;
        TransactionUtil.transactionDefinition = transactionDefinition;

    }

    /**
     * 携带事务，执行方法
     */
    @SneakyThrows
    public static void exec(VoidFunc0 voidFunc0) {

        // 开启事务
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);

        try {

            voidFunc0.call();

            dataSourceTransactionManager.commit(transactionStatus); // 提交

        } catch (Exception e) {

            dataSourceTransactionManager.rollback(transactionStatus); // 回滚
            throw e;

        }

    }

    /**
     * 携带事务，执行方法
     */
    public static <T> T exec(Supplier<T> supplier) {

        // 开启事务
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);

        try {

            T resObject = supplier.get();

            dataSourceTransactionManager.commit(transactionStatus); // 提交

            return resObject;

        } catch (Exception e) {

            dataSourceTransactionManager.rollback(transactionStatus); // 回滚
            throw e;

        }

    }

}
