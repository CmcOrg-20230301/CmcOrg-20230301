package com.cmcorg20230301.engine.be.electron.util;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 扫描屏幕工具类
 */
@Slf4j
public class ScanScreenUtil {

    private static final String SCAN_BY_IMAGE_PATH = "img/scanByImage-bak1.png";

    public static void main(String[] args) {

        int[] intArr = scanByImage(SCAN_BY_IMAGE_PATH);

        if (intArr != null) {

            log.info("x：{}，y：{}", intArr[0], intArr[1]);

        }

    }

    /**
     * 通过：图片扫描屏幕
     */
    @SneakyThrows
    public static int[] scanByImage(String scanByImagePath) {

        // 电脑屏幕大小
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        Robot robot = new Robot();

        // 截图
        BufferedImage source = robot.createScreenCapture(new Rectangle(screen.width, screen.height));

        // 需要匹配的图片
        BufferedImage target = ImgUtil.read(ResourceUtil.getStream(scanByImagePath));

        long targetValue = 0;
        long gapValue = 0; // 误差
        int width = target.getWidth();
        int height = target.getHeight();

        for (int x = 0; x < width; x++) {

            for (int y = 0; y < height; y++) {

                targetValue = targetValue + target.getRGB(x, y);

            }

        }

        long minTargetValue = targetValue - gapValue;
        long maxTargetValue = targetValue + gapValue;

        for (int x = 0; x < source.getWidth(); x++) {

            for (int y = 0; y < source.getHeight(); y++) {

                if (x + width > source.getWidth()) {
                    continue;
                }

                if (y + height > source.getHeight()) {
                    continue;
                }

                long sum = 0;

                for (int i = x; i < x + width; i++) {

                    for (int j = y; j < y + height; j++) {

                        sum = sum + source.getRGB(i, j);

                    }

                }

                if (sum >= minTargetValue && sum <= maxTargetValue) {

                    return new int[] {x, y};

                }

            }

        }

        return null;

    }

}
