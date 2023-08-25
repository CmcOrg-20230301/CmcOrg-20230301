package com.cmcorg20230301.be.engine.util.util;

import cn.hutool.core.map.MapUtil;

public class MyMapUtil {

    public static int getInitialCapacity(int size) {

        return (int)(size / MapUtil.DEFAULT_LOAD_FACTOR) + 1;

    }

}
