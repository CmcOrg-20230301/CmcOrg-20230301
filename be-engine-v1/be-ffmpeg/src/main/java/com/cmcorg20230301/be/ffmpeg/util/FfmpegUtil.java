package com.cmcorg20230301.be.ffmpeg.util;

import java.io.File;

import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.model.model.constant.SysFileTempPathConstant;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import lombok.SneakyThrows;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

@Component
public class FfmpegUtil {

    /**
     * 视频提取音频
     */
    @SneakyThrows
    public static void videoToAudio(String videoUrl, VoidFunc1<File> voidFunc1) {

        byte[] downloadByteArr = HttpUtil.downloadBytes(videoUrl);

        File videoFile = null;

        File audioFile = null;

        try {

            String uuid = IdUtil.simpleUUID();

            videoFile = FileUtil.touch(SysFileTempPathConstant.FFMPEG_TEMP_PATH + uuid + ".mp4");

            audioFile = FileUtil.touch(SysFileTempPathConstant.FFMPEG_TEMP_PATH + uuid + ".mp3");

            FileUtil.writeBytes(downloadByteArr, videoFile);

            AudioAttributes audio = new AudioAttributes();

            audio.setCodec("libmp3lame");

            audio.setBitRate(128000);

            audio.setChannels(2);

            audio.setSamplingRate(16000);

            EncodingAttributes attrs = new EncodingAttributes();

            attrs.setOutputFormat("mp3");

            attrs.setAudioAttributes(audio);

            Encoder encoder = new Encoder();

            encoder.encode(new MultimediaObject(videoFile), audioFile, attrs);

            // 处理：音频文件
            voidFunc1.call(audioFile);

        } finally {

            FileUtil.del(videoFile);

            FileUtil.del(audioFile);

        }

    }

}
