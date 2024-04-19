package com.cmcorg20230301.be.ffmpeg.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javax.annotation.Nullable;

import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.model.model.constant.SysFileTempPathConstant;
import com.cmcorg20230301.be.engine.security.util.MyExceptionUtil;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ws.schild.jave.process.ProcessWrapper;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;

@Component
@Slf4j(topic = LogTopicConstant.FFMPEG)
public class FfmpegUtil {

    /**
     * 视频添加字幕
     */
    @SneakyThrows
    public static void videoAddSrt(String videoUrl, String srt, @Nullable VoidFunc1<File> voidFunc1) {

        byte[] downloadByteArr = HttpUtil.downloadBytes(videoUrl);

        File videoFile = null;

        File srtFile = null;

        File videoOutFile = null;

        try {

            String uuid = IdUtil.simpleUUID();

            videoFile = FileUtil.touch(SysFileTempPathConstant.FFMPEG_TEMP_PATH + uuid + ".mp4");

            srtFile = FileUtil.touch(SysFileTempPathConstant.FFMPEG_TEMP_PATH + uuid + ".srt");

            videoOutFile = FileUtil.touch(SysFileTempPathConstant.FFMPEG_TEMP_PATH + uuid + "_out.mp4");

            FileUtil.writeBytes(downloadByteArr, videoFile);

            FileUtil.writeUtf8String(srt, srtFile);

            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();

            ffmpeg.addArgument("-i");

            ffmpeg.addArgument(videoFile.getAbsolutePath());

            ffmpeg.addArgument("-vf");

            ffmpeg.addArgument("-y");

            ffmpeg.addArgument("\"subtitles=" + srtFile.getAbsoluteFile() + "\"");

            ffmpeg.addArgument(videoOutFile.getAbsolutePath());

            // 执行
            doExecute(ffmpeg);

            if (voidFunc1 != null) {

                // 处理：音频文件
                voidFunc1.call(videoOutFile);

            }

        } finally {

            // FileUtil.del(videoFile);
            //
            // FileUtil.del(srtFile);
            //
            // FileUtil.del(videoOutFile);

        }

    }

    // public static void main(String[] args) {
    //
    // videoToAudio("https://ai.lxjjai.com/be-public-bucket/temp-file/7c9dc8b7d4a32db7e1049e365efdc514.mp4", null);
    //
    // }

    /**
     * 视频提取音频
     */
    @SneakyThrows
    public static void videoToAudio(String videoUrl, @Nullable VoidFunc1<File> voidFunc1) {

        byte[] downloadByteArr = HttpUtil.downloadBytes(videoUrl);

        File videoFile = null;

        File audioFile = null;

        try {

            String uuid = IdUtil.simpleUUID();

            videoFile = FileUtil.touch(SysFileTempPathConstant.FFMPEG_TEMP_PATH + uuid + ".mp4");

            audioFile = FileUtil.touch(SysFileTempPathConstant.FFMPEG_TEMP_PATH + uuid + ".mp3");

            FileUtil.writeBytes(downloadByteArr, videoFile);

            ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();

            ffmpeg.addArgument("-i");

            ffmpeg.addArgument(videoFile.getAbsolutePath());

            ffmpeg.addArgument("-vn");

            ffmpeg.addArgument("-f");

            ffmpeg.addArgument("mp3");

            ffmpeg.addArgument("-ar");

            ffmpeg.addArgument("16000");

            ffmpeg.addArgument("-y");

            ffmpeg.addArgument(audioFile.getAbsolutePath());

            // 执行
            doExecute(ffmpeg);

            if (voidFunc1 != null) {

                // 处理：音频文件
                voidFunc1.call(audioFile);

            }

        } finally {

            FileUtil.del(videoFile);

            FileUtil.del(audioFile);

        }

    }

    /**
     * 执行
     */
    public static void doExecute(ProcessWrapper ffmpeg) {

        try {

            ffmpeg.execute();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {

                blockFfmpeg(br);

            }

        } catch (Exception e) {

            MyExceptionUtil.printError(e);

        }

    }

    /**
     * 等待命令执行成功，退出
     */
    @SneakyThrows
    private static void blockFfmpeg(BufferedReader br) {

        String line;

        // 该方法阻塞线程，直至合成成功
        while ((line = br.readLine()) != null) {

            handleLine(line);

        }

    }

    /**
     * 处理日志
     */
    private static void handleLine(String line) {

        log.info("ffmpeg 输出：{}", line);

    }

}
