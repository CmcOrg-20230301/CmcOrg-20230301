package com.cmcorg20230301.be.engine.other.app.microsoft.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.other.app.microsoft.util.captioning.*;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppDO;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppTypeEnum;
import com.cmcorg20230301.be.engine.other.app.service.SysOtherAppService;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioInputStream;
import com.microsoft.cognitiveservices.speech.audio.AudioStreamFormat;
import com.microsoft.cognitiveservices.speech.audio.PullAudioInputStream;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.VoidFunc1;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 微软工具类
 */
@Component
@Slf4j(topic = LogTopicConstant.MICROSOFT)
public class MicrosoftUtil {

    public static void main(String[] args) {

        // Captioning.main(CollUtil.newArrayList("--key", "key", "--region", "region",
        // "--input", "/home/myTempFile/ffmpegUpload/wav.wav", "--srt", "--quiet", "--output",
        // "/home/myTempFile/ffmpegUpload/srt.srt").toArray(new String[0]));

    }

    private static SysOtherAppService sysOtherAppService;

    public MicrosoftUtil(SysOtherAppService sysOtherAppService) {

        MicrosoftUtil.sysOtherAppService = sysOtherAppService;

    }

    /**
     * 获取：配置
     */
    public static SpeechConfig getSpeechConfig(@Nullable Long tenantId, @Nullable String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        if (StrUtil.isBlank(appId)) {

            List<SysOtherAppDO> sysOtherAppDOList =
                sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
                    .select(SysOtherAppDO::getAppId).eq(SysOtherAppDO::getType, SysOtherAppTypeEnum.MICROSOFT)
                    .eq(BaseEntityNoId::getEnableFlag, true).list();

            if (CollUtil.isEmpty(sysOtherAppDOList)) {
                ApiResultVO.error("操作失败：未找到微软相关配置", tenantId);
            }

            // 取第一个，作为本次的 appId
            appId = CollUtil.getFirst(sysOtherAppDOList).getAppId();

        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
            .eq(SysOtherAppDO::getAppId, appId).select(SysOtherAppDO::getSecret)
            .eq(SysOtherAppDO::getType, SysOtherAppTypeEnum.MICROSOFT).eq(BaseEntityNoId::getEnableFlag, true).one();

        return SpeechConfig.fromSubscription(appId, sysOtherAppDO.getSecret());

    }

    /**
     * 识别语音中的文字
     *
     * @param language 音频的语音：en-US 英文 zh-CN 中文
     */
    @SneakyThrows
    public static String conversationTranscriptionFromFile(@Nullable Long tenantId, @Nullable String appId,
        String inputFilePath, String language, @Nullable VoidFunc1<List<Caption>> voidFunc1) {

        SpeechConfig speechConfig = getSpeechConfig(tenantId, appId);

        speechConfig.setSpeechRecognitionLanguage(language);

        AudioStreamFormat waveFormatPcm = AudioStreamFormat.getWaveFormatPCM(16000, (short)16, (short)2);

        BinaryFileReader binaryFileReader = new BinaryFileReader(inputFilePath);

        PullAudioInputStream pullStream = AudioInputStream.createPullStream(binaryFileReader, waveFormatPcm);

        AudioConfig audioConfig = AudioConfig.fromStreamInput(pullStream);

        SpeechRecognizer speechRecognizer = new SpeechRecognizer(speechConfig, audioConfig);

        boolean[] done = new boolean[] {false};

        List<RecognitionResult> offlineResultList = new ArrayList<>();

        speechRecognizer.recognized.addEventListener((s, e) -> {

            if (ResultReason.RecognizedSpeech == e.getResult().getReason() && e.getResult().getText().length() > 0) {

                offlineResultList.add(e.getResult());

            } else if (ResultReason.NoMatch == e.getResult().getReason()) {

                log.info("NOMATCH: Speech could not be recognized.");

            }
        });

        speechRecognizer.canceled.addEventListener((s, e) -> {

            if (CancellationReason.EndOfStream == e.getReason()) {

                log.info("End of stream reached.");

                done[0] = true; // Notify to stop recognition.

            } else if (CancellationReason.CancelledByUser == e.getReason()) {

                log.info("User canceled request.");

                done[0] = true; // Notify to stop recognition.

            } else if (CancellationReason.Error == e.getReason()) {

                // Error output should not be suppressed, even if suppress output flag is set.
                log.info("Encountered error.Error code: {}, Error details: {}", e.getErrorCode(), e.getErrorDetails());

                done[0] = true; // Notify to stop recognition.

            } else {

                log.info("Request was cancelled for an unrecognized reason: {}.", e.getReason());

                done[0] = true; // Notify to stop recognition.

            }

        });

        speechRecognizer.sessionStarted.addEventListener((s, e) -> {

            log.info("Session started.");

        });

        speechRecognizer.sessionStopped.addEventListener((s, e) -> {

            log.info("Session stopped.");

            done[0] = true; // Notify to stop recognition.

        });

        speechRecognizer.startContinuousRecognitionAsync().get();

        while (!done[0]) {

            ThreadUtil.sleep(500);

        }

        speechRecognizer.stopContinuousRecognitionAsync().get();

        List<Caption> captionList =
            CaptionHelper.GetCaptions(Optional.of(language), UserConfig.defaultMaxLineLengthSBCS, 2, offlineResultList);

        // 处理：Caption集合
        Captioning.handleCaptionList(captionList, 1000);

        if (voidFunc1 != null) {

            // 可以进行翻译
            voidFunc1.call(captionList);

        }

        StrBuilder strBuilder = StrBuilder.create();

        for (Caption item : captionList) {

            String str = Captioning.StringFromCaption(item, true);

            strBuilder.append(str);

        }

        // log.info("结果：{}", strBuilder);

        return strBuilder.toString();

    }

}
