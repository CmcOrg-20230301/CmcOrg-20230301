package com.cmcorg20230301.engine.be.electron.util;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

/**
 * awt工具类
 */
@Slf4j
public class AwtUtil {

    private static final String SCAN_SCREEN_BY_IMAGE = "/test/img/scanScreenByImage.png";

    @SneakyThrows
    public static void main(String[] args) {

        log.info("开始匹配");

        while (true) {

            // 通过：图片扫描屏幕
            ScanResult scanResult = scanScreenByImage(SCAN_SCREEN_BY_IMAGE);

            if (scanResult != null) {

                log.info("scanResult：{}，getCenterX()：{}，getCenterY：{}", JSONUtil.toJsonStr(scanResult),
                    scanResult.getCenterX(), scanResult.getCenterY());

                Robot robot = new Robot();

                // 鼠标移动到某一点
                robot.mouseMove(scanResult.getCenterX(), scanResult.getCenterY());

                // 模拟鼠标按下左键
                robot.mousePress(InputEvent.BUTTON1_MASK);

                // 模拟鼠标松开左键
                robot.mouseRelease(InputEvent.BUTTON1_MASK);

                return;

            } else {

                log.info("未匹配");
                ThreadUtil.safeSleep(2000);

            }

        }

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScanResult {

        private int x;

        private int y;

        private int targetWidth;

        private int targetHeight;

        public int getCenterX() {
            return getX() + (getTargetWidth() / 2);
        }

        public int getCenterY() {
            return getY() + (getTargetHeight() / 2);
        }

    }

    /**
     * 通过：图片扫描屏幕
     */
    @SneakyThrows
    public static ScanResult scanScreenByImage(String scanScreenByImagePath) {

        // 电脑屏幕大小
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        Robot robot = new Robot();

        // 截图
        BufferedImage source = robot.createScreenCapture(new Rectangle(screen.width, screen.height));

        // 需要匹配的图片
        BufferedImage target = ImgUtil.read(scanScreenByImagePath);

        long targetValue = 0;
        long gapValue = 1; // 误差
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

                if (sum > minTargetValue && sum < maxTargetValue) {

                    return new ScanResult(x, y, width, height);

                }

            }

        }

        return null;

    }

}
