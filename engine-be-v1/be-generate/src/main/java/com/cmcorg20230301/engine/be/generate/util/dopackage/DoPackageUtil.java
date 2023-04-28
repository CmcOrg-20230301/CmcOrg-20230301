package com.cmcorg20230301.engine.be.generate.util.dopackage;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.extra.ssh.Sftp;
import com.cmcorg20230301.engine.be.generate.util.apitest.ApiTestHelper;
import com.jcraft.jsch.Session;
import lombok.SneakyThrows;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * 打包工具类
 */
public class DoPackageUtil {

    private static final String HOST = "43.154.37.130";

    private static final String USER = "root";

    private static final String PRIVATE_KEY_PATH = "/key/key1.pem";

    private static final String VITE_REMOTE_PATH = "/mydata/nginx/node-1/html/h5";

    private static final String SPRING_REMOTE_PATH = "/mydata/springboot";

    private static final String SPRING_REMOTE_EXEC_CMD = "docker restart be-start-node-1";

    /**
     * 打包：前端和后端
     */
    @SneakyThrows
    public static void main(String[] args) {

        String nextLine = ApiTestHelper.getStrFromScanner("请输入：1 全部打包 2 后端打包 3 前端打包");

        int number = Convert.toInt(nextLine, 1);

        int threadCount;

        if (number == 2 || number == 3) {
            threadCount = 1;
        } else {
            threadCount = 2;
        }

        Session session = JschUtil.getSession(HOST, 22, USER, PRIVATE_KEY_PATH, null);

        String projectPath = System.getProperty("user.dir"); // 例如：D:\GitHub\CmcOrg-20230301

        CountDownLatch countDownLatch = ThreadUtil.newCountDownLatch(threadCount);

        if (number == 1 || number == 2) {
            ThreadUtil.execute(() -> doBePackage(projectPath, session, countDownLatch));
        }

        if (number == 1 || number == 3) {
            ThreadUtil.execute(() -> doFePackage(projectPath, session, countDownLatch));
        }

        countDownLatch.await();

        JschUtil.close(session);

        System.exit(0);

    }

    /**
     * 后端打包
     */
    private static void doBePackage(String projectPath, Session session, CountDownLatch countDownLatch) {

        Sftp sftp = JschUtil.createSftp(session);

        try {

            System.out.println("后端打包 ↓");

            long timeNumber = System.currentTimeMillis();

            projectPath = projectPath + "/engine-be-v1";

            RuntimeUtil.execForStr("cmd", "/c", "cd " + projectPath + " && mvn package");

            timeNumber = System.currentTimeMillis() - timeNumber;
            String timeStr = DateUtil.formatBetween(timeNumber);

            System.out.println("后端打包 ↑ 耗时：" + timeStr);

            String jarPath = projectPath + "/be-start/target/be-start-2023.3.1.jar";

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

            System.out.println("后端相关操作执行完毕！====================");

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
    public static void doFePackage(String projectPath, Session session, CountDownLatch countDownLatch) {

        Sftp sftp = JschUtil.createSftp(session);

        try {

            System.out.println("前端打包 ↓");

            long timeNumber = System.currentTimeMillis();

            projectPath = projectPath + "/fe-antd-v1";

            String viteBuildPath = projectPath + "/dist";

            FileUtil.del(viteBuildPath); // 先删除：原来打包的文件夹
            File file = FileUtil.mkdir(viteBuildPath); // 再创建文件夹

            RuntimeUtil.execForStr("cmd", "/c", "cd " + projectPath + " && npm run build");

            timeNumber = System.currentTimeMillis() - timeNumber;
            String timeStr = DateUtil.formatBetween(timeNumber);

            System.out.println("前端打包 ↑ 耗时：" + timeStr);

            long size = FileUtil.size(file);

            if (size == 0) {

                System.out.println("前端打包上传失败，文件大小为 0");
                return;

            }

            System.out.println("前端打包上传 ↓ 大小：" + DataSizeUtil.format(size));

            timeNumber = System.currentTimeMillis();

            sftp.delDir(VITE_REMOTE_PATH); // 先删除，再上传

            sftp.syncUpload(file, VITE_REMOTE_PATH);

            timeNumber = System.currentTimeMillis() - timeNumber;
            timeStr = DateUtil.formatBetween(timeNumber);

            System.out.println("前端打包上传 ↑ 耗时：" + timeStr);

            System.out.println("前端相关操作执行完毕！====================");

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            countDownLatch.countDown();
            JschUtil.close(sftp.getClient());

        }

    }

}
