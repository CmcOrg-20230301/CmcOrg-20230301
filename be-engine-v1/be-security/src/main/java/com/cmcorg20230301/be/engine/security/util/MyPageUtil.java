package com.cmcorg20230301.be.engine.security.util;

public class MyPageUtil {

    /**
     * 将页数和每页条目数转换为开始位置<br>
     * 此方法用于不包括结束位置的分页方法<br>
     * 例如：
     * <p>
     * 页码：0，每页10 =》 0
     * 页码：1，每页10 =》 10
     *
     * @param pageNo   页码（从0计数）
     * @param pageSize 每页条目数
     * @return 开始位置
     */
    public static long getOffset(long pageNo, long pageSize) {

        if (pageNo < 0) {
            pageNo = 0;
        }

        if (pageSize < 1) {
            pageSize = 0;
        }

        return pageNo * pageSize;

    }

}
