package com.cmcorg20230301.be.ffmpeg.util;

import java.net.URL;

import lombok.SneakyThrows;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

public class FfmpegUtil {

    /**
     * 通过：URL获取多媒体文件信息
     */
    @SneakyThrows
    public static MultimediaInfo getMultimediaInfoFromUrl(String url) {

        return new MultimediaObject(new URL(url)).getInfo();

    }

}
