package com.cmcorg20230301.engine.be.generate.project.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;
import com.jcraft.jsch.Session;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class DoAdminPackage {

    private static final String HOST = "81.69.58.190";

    private static final String USER = "root";

    private static final String PRIVATE_KEY_PATH = "E:\\demo\\key1.pem";

    private static final String VITE_REMOTE_PATH = "/mydata/nginx/html/h5";

    private static final String SPRING_REMOTE_PATH = "/mydata/springboot";

    private static final String SPRING_REMOTE_EXEC_CMD = "docker restart spring-boot-admin";

    /**
     * 打包：前端和后端
     */
    @SneakyThrows
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入：1 全部打包 2 后端打包 3 前端打包");

        String nextLine = scanner.nextLine();

        scanner.close();

        int number = Convert.toInt(nextLine, 1);

        int threadCount = 2;

        if (number == 2 || number == 3) {
            threadCount = 1;
        }

        Session session = JschUtil.getSession(HOST, 22, USER, PRIVATE_KEY_PATH, null);

        String springPath = System.getProperty("user.dir"); // 例如：E:\Cmc0\Admin-Project\spring

        CountDownLatch countDownLatch = ThreadUtil.newCountDownLatch(threadCount);

        if (number == 1 || number == 2) {
            ThreadUtil.execute(() -> doSpringPackage(springPath, session, countDownLatch));
        }

        if (number == 1 || number == 3) {
            ThreadUtil.execute(() -> doVitePackage(springPath, session, countDownLatch));
        }

        countDownLatch.await();

        JschUtil.close(session);
    }

    /**
     * 后端打包
     */
    private static void doSpringPackage(String springPath, Session session, CountDownLatch countDownLatch) {

        Sftp sftp = JschUtil.createSftp(session);

        try {

            System.out.println("后端打包 ↓");

            long timeNumber = System.currentTimeMillis();

            RuntimeUtil.execForStr("cmd /c cd " + springPath + "&& mvn clean package");

            timeNumber = System.currentTimeMillis() - timeNumber;
            String timeStr = DateUtil.formatBetween(timeNumber);

            System.out.println("后端打包 ↑ 耗时：" + timeStr);

            String jarPath = springPath + "/start/target/start-0.0.1-SNAPSHOT.jar";

            File file = FileUtil.newFile(jarPath);

            System.out.println("后端打包上传 ↓ 大小：" + DataSizeUtil.format(FileUtil.size(file)));

            timeNumber = System.currentTimeMillis();

            sftp.put(jarPath, SPRING_REMOTE_PATH);

            timeNumber = System.currentTimeMillis() - timeNumber;
            timeStr = DateUtil.formatBetween(timeNumber);

            System.out.println("后端打包上传 ↑ 耗时：" + timeStr);

            System.out.println("启动后端 ↓");

            timeNumber = System.currentTimeMillis();

            JschUtil.exec(session, SPRING_REMOTE_EXEC_CMD, CharsetUtil.CHARSET_UTF_8);

            timeNumber = System.currentTimeMillis() - timeNumber;
            timeStr = DateUtil.formatBetween(timeNumber);

            System.out.println("启动后端 ↑ 耗时：" + timeStr);

            System.out.println("后端执行完毕！====================");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
            JschUtil.close(sftp.getClient());
        }
    }

    /**
     * 前端打包
     */
    public static void doVitePackage(String springPath, Session session, CountDownLatch countDownLatch) {

        Sftp sftp = JschUtil.createSftp(session);

        try {

            System.out.println("前端打包 ↓");

            long timeNumber = System.currentTimeMillis();

            String vitePath = StrUtil.subBefore(springPath, "\\", true);

            vitePath = vitePath + "/ant-react";

            RuntimeUtil.execForStr("cmd /c cd " + vitePath + "&& npm run build");

            timeNumber = System.currentTimeMillis() - timeNumber;
            String timeStr = DateUtil.formatBetween(timeNumber);

            System.out.println("前端打包 ↑ 耗时：" + timeStr);

            String viteBuildPath = vitePath + "/dist";

            File file = FileUtil.newFile(viteBuildPath);

            System.out.println("前端打包上传 ↓ 大小：" + DataSizeUtil.format(FileUtil.size(file)));

            timeNumber = System.currentTimeMillis();

            sftp.delDir(VITE_REMOTE_PATH);

            sftp.syncUpload(file, VITE_REMOTE_PATH);

            timeNumber = System.currentTimeMillis() - timeNumber;
            timeStr = DateUtil.formatBetween(timeNumber);

            System.out.println("前端打包上传 ↑ 耗时：" + timeStr);

            System.out.println("前端执行完毕！====================");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
            JschUtil.close(sftp.getClient());
        }
    }

}
