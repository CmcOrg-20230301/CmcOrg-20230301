package com.cmcorg20230301.be.engine.security.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class MyPageUtil {

    /**
     * 将页数和每页条目数转换为开始位置<br> 此方法用于不包括结束位置的分页方法<br> 例如：
     * <p>
     * 页码：1，每页10 =》 10 页码：2，每页10 =》 20
     *
     * @param pageNo   页码（从 1计数）
     * @param pageSize 每页条目数
     * @return 开始位置
     */
    public static long getOffset(long pageNo, long pageSize) {

        if (pageNo < 1) {
            pageNo = 1;
        }

        if (pageSize < 1) {
            pageSize = 1;
        }

        return (pageNo - 1) * pageSize;

    }

    /**
     * 获取：只有一个数据的 page对象
     */
    public static <T> Page<T> getLimit1Page() {

        return getLimitPage(1);

    }

    /**
     * 获取：没有偏移数据的分页对象
     */
    public static <T> Page<T> getLimitPage(long size) {

        return new Page<>(0, size, 0, false);

    }

    /**
     * 获取：滚动查询的 page对象 作用：根据 最后一个 id查询数据
     */
    public static <T> Page<T> getScrollPage(Integer size) {

        if (size == null) {
            size = 20;
        }

        return new Page<>(0, size, 0, true);

    }

}
