package com.cmcorg20230301.engine.be.generate.util.apitest;

import java.util.Scanner;

/**
 * api测试帮助类
 */
public class ApiTestHelper {

    public static final String RSA_PUBLIC_KEY =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDadmaCaffN63JC5QsMK/+le5voCB4DzOsV9xOBZgGJyqnizh9/UcFkIoRae5rebdWUtnPO4CTgdJbuSvu/TtIIPj9De5/wiJilFAWd1Ve7qGaxxTxqWwFNp7p/FLr0YpMeBjOylds9GyA1cnjIqruNdYv+qRZnseE0Sq2WEZus9QIDAQAB";

    /**
     * 计算花费的时间
     */
    public static long calcCostMs(long currentTs) {

        return System.currentTimeMillis() - currentTs;

    }

    /**
     * 从控制台的输入里面获取字符串
     */
    public static String getStringFromScanner(String tip) {

        System.out.println(tip);

        Scanner scanner = new Scanner(System.in);

        return scanner.nextLine();

    }

}
