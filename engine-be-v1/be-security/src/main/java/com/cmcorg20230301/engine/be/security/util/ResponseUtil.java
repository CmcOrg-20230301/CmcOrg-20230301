package com.cmcorg20230301.engine.be.security.util;

import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.exception.BaseException;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import lombok.SneakyThrows;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;

public class ResponseUtil {

    @SneakyThrows
    public static void out(HttpServletResponse response, BaseBizCodeEnum baseBizCodeEnum) {

        response.setContentType("application/json;charset=utf-8");

        ServletOutputStream servletOutputStream = response.getOutputStream();

        try {

            ApiResultVO.error(baseBizCodeEnum); // 这里肯定会抛出 BaseException异常

        } catch (BaseException e) {

            servletOutputStream.write(e.getMessage().getBytes()); // json字符串，输出给前端
            servletOutputStream.flush();
            servletOutputStream.close();

        }

    }

    @SneakyThrows
    public static void out(HttpServletResponse response, String msg) {

        response.setContentType("application/json;charset=utf-8");

        ServletOutputStream servletOutputStream = response.getOutputStream();

        try {

            ApiResultVO.error(msg); // 这里肯定会抛出 BaseException异常

        } catch (BaseException e) {

            servletOutputStream.write(e.getMessage().getBytes()); // json字符串，输出给前端
            servletOutputStream.flush();
            servletOutputStream.close();

        }

    }

    /**
     * 获取 excel下载的 OutputStream
     */
    @SneakyThrows
    public static OutputStream getExcelOutputStream(HttpServletResponse response, String fileName) {

        response.setContentType("application/vnd.ms-excel;charset=utf-8");

        response
            .setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");

        return response.getOutputStream();

    }

}
