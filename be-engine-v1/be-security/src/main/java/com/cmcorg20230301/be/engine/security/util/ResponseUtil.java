package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.io.IoUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseException;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import lombok.SneakyThrows;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
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

        out(response, msg, HttpServletResponse.SC_OK);

    }

    @SneakyThrows
    public static void out(HttpServletResponse response, String msg, int status) {

        response.setContentType("application/json;charset=utf-8");

        ServletOutputStream servletOutputStream = response.getOutputStream();

        response.setStatus(status);

        try {

            ApiResultVO.errorMsg(msg); // 这里肯定会抛出 BaseException异常

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

    /**
     * 把流复制给 response，然后返回给调用者
     */
    @SneakyThrows
    public static void flush(HttpServletResponse response, InputStream inputStream) {

        ServletOutputStream outputStream = response.getOutputStream();

        IoUtil.copy(inputStream, outputStream);

        outputStream.flush();

        IoUtil.close(inputStream);

        IoUtil.close(outputStream);

    }

}
