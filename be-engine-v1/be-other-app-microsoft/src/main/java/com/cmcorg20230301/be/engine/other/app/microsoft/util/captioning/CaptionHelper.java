package com.cmcorg20230301.be.engine.other.app.microsoft.util.captioning;//

// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE.md file in the project root for full license information.
//

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.microsoft.cognitiveservices.speech.RecognitionResult;
import com.microsoft.cognitiveservices.speech.ResultReason;

public class CaptionHelper {
    final private Optional<String> _language;
    final private String[] _firstPassTerminators;
    final private String[] _secondPassTerminators;

    final private int _maxWidth;
    final private int _maxHeight;
    final private List<RecognitionResult> _results;

    private Optional<List<Caption>> _captions = Optional.empty();

    static final Instant dotNetEpoch = ZonedDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();

    public static Instant InstantFromTicks(BigInteger ticks) {
        // 10,000 ticks per millisecond.
        long ms = Math.floorDiv(ticks.longValue(), 10000);
        long restTicks = Math.floorMod(ticks.longValue(), 10000);
        long restNanos = restTicks * 100;
        return dotNetEpoch.plusMillis(ms).plusNanos(restNanos);
    }

    public static List<Caption> GetCaptions(Optional<String> language, int maxWidth, int maxHeight,
        List<RecognitionResult> results) {
        CaptionHelper helper = new CaptionHelper(language, maxWidth, maxHeight, results);
        return helper.GetCaptions();
    }

    public List<String> LinesFromText(String text) {
        ArrayList retval = new ArrayList<String>();

        int index = 0;
        while (index < text.length()) {
            index = SkipSkippable(text, index);

            int lineLength = GetBestWidth(text, index);
            retval.add(text.substring(index, index + lineLength).trim());
            index = index + lineLength;
        }

        return retval;
    }

    public CaptionHelper(Optional<String> language, int maxWidth, int maxHeight, List<RecognitionResult> results) {
        this._language = language;

        this._maxHeight = maxHeight;
        this._results = results;

        // consider adapting to use http://unicode.org/reports/tr29/#Sentence_Boundaries
        String iso639 = "";
        if (_language.isPresent()) {
            iso639 = _language.get().split("-")[0];
        }

        switch (iso639) {
            case "zh":
                this._firstPassTerminators = new String[] {"，", "、", "；", "？", "！", "?", "!", ",", ";"};
                this._secondPassTerminators = new String[] {"。", " "};
                break;
            default:
                this._firstPassTerminators = new String[] {"?", "!", ",", ";"};
                this._secondPassTerminators = new String[] {" ", "."};
                break;
        }

        if (maxWidth == UserConfig.defaultMaxLineLengthSBCS && iso639 == "zh") {
            this._maxWidth = UserConfig.defaultMaxLineLengthMBCS;
        } else {
            this._maxWidth = maxWidth;
        }
    }

    public List<Caption> GetCaptions() {
        EnsureCaptions();
        return _captions.get();
    }

    private void EnsureCaptions() {
        if (!_captions.isPresent()) {
            _captions = Optional.of(new ArrayList<Caption>());
            AddCaptionsForAllResults();
        }
    }

    private void AddCaptionsForAllResults() {
        for (RecognitionResult result : _results) {
            if (result.getOffset().longValue() <= 0 || !IsFinalResult(result)) {
                continue;
            }

            Optional<String> text = GetTextOrTranslation(result);
            if (!text.isPresent()) {
                continue;
            }

            AddCaptionsForFinalResult(result, text.get());
        }
    }

    private Optional<String> GetTextOrTranslation(RecognitionResult result) {
        return Optional.of(result.getText());
        // 20220921 We do not use this for now because this sample
        // does not handle TranslationRecognitionResults.
        /*
        if (!_language.isPresent())
        {
            return Optional.of(result.getText());
        }
        
        if (result instanceof TranslationRecognitionResult)
        {
            var result_2 = (TranslationRecognitionResult)result;
            if (result_2.getTranslations().containsKey(_language.get()))
            {
                return Optional.of(result_2.getTranslations().get(_language.get()));
            }
            else
            {
                return Optional.empty();
            }
        }
        else
        {
            return Optional.empty();
        }
        */
    }

    private void AddCaptionsForFinalResult(RecognitionResult result, String text) {
        int captionStartsAt = 0;
        ArrayList captionLines = new ArrayList<String>();

        int index = 0;
        while (index < text.length()) {
            index = SkipSkippable(text, index);

            int lineLength = GetBestWidth(text, index);
            captionLines.add(text.substring(index, index + lineLength).trim());
            index = index + lineLength;

            boolean isLastCaption = index >= text.length();
            boolean maxCaptionLines = captionLines.size() >= _maxHeight;

            boolean addCaption = isLastCaption || maxCaptionLines;
            if (addCaption) {
                String captionText = String.join("\n", captionLines);
                captionLines.clear();

                int captionSequence = _captions.get().size() + 1;
                boolean isFirstCaption = captionStartsAt == 0;

                CaptionTiming captionTiming = isFirstCaption && isLastCaption ? GetFullResultCaptionTiming(result)
                    : GetPartialResultCaptionTiming(result, text, captionText, captionStartsAt,
                        index - captionStartsAt);

                Caption caption =
                    new Caption(_language, captionSequence, captionTiming.begin, captionTiming.end, captionText);
                _captions.get().add(caption);

                captionStartsAt = index;
            }
        }
    }

    private int GetBestWidth(String text, int startIndex) {
        int remaining = text.length() - startIndex;
        int bestWidth = remaining < _maxWidth ? remaining : FindBestWidth(_firstPassTerminators, text, startIndex);

        if (bestWidth < 0) {
            bestWidth = FindBestWidth(_secondPassTerminators, text, startIndex);
        }

        if (bestWidth < 0) {
            bestWidth = _maxWidth;
        }

        return bestWidth;
    }

    private int FindBestWidth(String[] terminators, String text, int startAt) {
        int remaining = text.length() - startAt;
        int checkChars = Math.min(remaining, _maxWidth);

        int bestWidth = -1;
        for (String terminator : terminators) {
            // We need to get the last index of the terminator,
            // but only searching from startAt to startAt + checkChars.
            // So we take a substring of text from startAt to
            // startAt + checkChars.
            // Afterward, we need to re-add startAt to the resulting
            // index (which is from the substring) to align it with
            // the text from which the substring was taken.
            int index = text.substring(startAt, startAt + checkChars).lastIndexOf(terminator) + startAt;
            int width = index - startAt;
            if (width > bestWidth) {
                bestWidth = width + terminator.length();
            }
        }

        return bestWidth;
    }

    private int SkipSkippable(String text, int startIndex) {
        int index = startIndex;
        while (text.length() > index && text.charAt(index) == ' ') {
            index++;
        }

        return index;
    }

    private CaptionTiming GetFullResultCaptionTiming(RecognitionResult result) {
        Instant resultBegin = CaptionHelper.InstantFromTicks(result.getOffset());
        Instant resultEnd = CaptionHelper.InstantFromTicks(result.getOffset().add(result.getDuration()));
        return new CaptionTiming(resultBegin, resultEnd);
    }

    private CaptionTiming GetPartialResultCaptionTiming(RecognitionResult result, String text, String captionText,
        int captionStartsAt, int captionLength) {
        CaptionTiming captionTiming = GetFullResultCaptionTiming(result);
        Duration resultDuration = Duration.between(captionTiming.begin, captionTiming.end);
        int textLength = text.length();
        // TODO2 Consider something more precise than ms.
        Instant partialBegin = captionTiming.begin.plusMillis(resultDuration.toMillis() * captionStartsAt / textLength);
        Instant partialEnd =
            captionTiming.begin.plusMillis(resultDuration.toMillis() * (captionStartsAt + captionLength) / textLength);
        return new CaptionTiming(partialBegin, partialEnd);
    }

    private static boolean IsFinalResult(RecognitionResult result) {
        return result.getReason() == ResultReason.RecognizedSpeech
            || result.getReason() == ResultReason.RecognizedIntent
            || result.getReason() == ResultReason.TranslatedSpeech;
    }
}
