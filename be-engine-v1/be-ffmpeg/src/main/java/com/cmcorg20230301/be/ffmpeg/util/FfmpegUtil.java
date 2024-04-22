package com.cmcorg20230301.be.ffmpeg.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javax.annotation.Nullable;

import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.model.model.constant.SysFileTempPathConstant;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.http.HttpUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

            String cmd = " -i " + videoFile.getName() + " -y -vf subtitles=" + srtFile.getName() + " -c:a copy "
                + videoOutFile.getName();

            // 执行
            handleCmd(cmd);

            if (voidFunc1 != null) {

                // 处理：文件
                voidFunc1.call(videoOutFile);

            }

        } finally {

            FileUtil.del(videoFile);

            FileUtil.del(srtFile);

            FileUtil.del(videoOutFile);

        }

    }

    /**
     * 执行
     */
    @SneakyThrows
    private static void handleCmd(String cmd) {

        DefaultFFMPEGLocator defaultFfmpegLocator = new DefaultFFMPEGLocator();

        String executablePath = defaultFfmpegLocator.getExecutablePath();

        File parentFile = FileUtil.newFile(SysFileTempPathConstant.FFMPEG_TEMP_PATH);

        cmd = executablePath + cmd;

        log.info("执行的命令：{}", cmd);

        Process process = RuntimeUtil.exec(null, parentFile, cmd);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

            String line;

            while ((line = reader.readLine()) != null) {

                log.info("ffmpeg 输出：{}", line);

            }

        }

        // 等待 ffmpeg命令执行完毕
        process.waitFor();

    }

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

            String cmd = " -i " + videoFile.getName() + " -vn -f mp3 -ar 16000 -y " + audioFile.getName();

            // 执行
            handleCmd(cmd);

            if (voidFunc1 != null) {

                // 处理：文件
                voidFunc1.call(audioFile);

            }

        } finally {

            FileUtil.del(videoFile);

            FileUtil.del(audioFile);

        }

    }

}
