package com.cmcorg20230301.be.engine.other.app.microsoft.util.captioning;//

// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE.md file in the project root for full license information.
//

import java.time.Instant;

public class CaptionTiming {
    public Instant begin;
    public Instant end;

    public CaptionTiming(Instant begin, Instant end) {
        this.begin = begin;
        this.end = end;
    }
}
