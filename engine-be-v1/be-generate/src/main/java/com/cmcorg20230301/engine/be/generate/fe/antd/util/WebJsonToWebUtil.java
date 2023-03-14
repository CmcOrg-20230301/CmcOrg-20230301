package com.cmcorg20230301.engine.be.generate.fe.antd.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.cmcorg.engine.web.model.model.constant.LogTopicConstant;
import com.cmcorg.generate.page.h5.util.model.dto.WebDTO;
import com.cmcorg.generate.page.h5.util.util.JavaToWebJsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j(topic = LogTopicConstant.JAVA_TO_WEB)
public class WebJsonToWebUtil {

    /**
     * 生成前端页面
     */
    public static void exec(String path) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入要生成的 fileName，为空，则生成所有，备注：api文件始终会自动生成，这里限制的是 page页面的生成");

        String fileName = scanner.nextLine();

        log.info("WebJsonToWeb：执行开始 =====================>");
        TimeInterval timer = DateUtil.timer();

        // 把 java的接口，转换为 json格式
        WebDTO webDTO = JavaToWebJsonUtil.doJavaToWebJson();

        if (webDTO != null) {
            doExec(webDTO, fileName, path); // 生成前端页面
        }

        long interval = timer.interval();
        log.info("WebJsonToWeb：执行结束 =====================> 耗时：{}毫秒", interval);
    }

    /**
     * 生成前端页面
     */
    private static void doExec(WebDTO webDTO, String fileName, String path) {

        com.cmcorg.generate.page.h5.util.WebJsonToWebGenerateApiUtil.WEB_SRC_API_PATH = path + "src/api/";
        // 生成 api文件
        com.cmcorg.generate.page.h5.util.WebJsonToWebGenerateApiUtil.generateApi(webDTO);

        com.cmcorg.generate.page.h5.util.WebJsonToWebGenerateAdminUtil.PAGE_PATH = path + "src/page/";
        // 生成 admin相关文件
        com.cmcorg.generate.page.h5.util.WebJsonToWebGenerateAdminUtil.generateAdmin(webDTO, fileName);

    }

}
