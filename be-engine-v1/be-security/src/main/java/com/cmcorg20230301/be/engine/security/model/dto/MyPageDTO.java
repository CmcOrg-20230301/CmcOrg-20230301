package com.cmcorg20230301.be.engine.security.model.dto;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.MyOrderDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页参数，查询所有：pageSize = -1，默认：current = 1，pageSize = 10")
public class MyPageDTO {

    @Schema(description = "第几页")
    private long current = 1;

    @Schema(description = "每页显示条数")
    private long pageSize = 10;

    @Schema(description = "排序字段")
    private MyOrderDTO order;

    /**
     * 判断前端是否，传递了 order字段
     */
    public boolean orderEmpty() {

        return getOrder() == null || StrUtil.isBlank(order.getName());

    }

    /**
     * 分页属性拷贝
     * toUnderlineCaseFlag：一般为 true
     */
    public <T> Page<T> page(boolean toUnderlineCaseFlag) {

        Page<T> page = new Page<>();

        page.setCurrent(getCurrent());
        page.setSize(getPageSize());

        if (orderEmpty()) {
            return page;
        }

        // 添加 orderList里面的排序规则
        page.orders().add(orderToOrderItem(getOrder(), toUnderlineCaseFlag));

        return page;

    }

    /**
     * 自定义的排序规则，转换为 mybatis plus 的排序规则
     * underscoreFlag：是否驼峰转下划线
     */
    private static OrderItem orderToOrderItem(MyOrderDTO order, boolean toUnderlineFlag) {

        OrderItem orderItem = new OrderItem();
        orderItem.setColumn(toUnderlineFlag ? StrUtil.toUnderlineCase(order.getName()) : order.getName());

        if (StrUtil.isNotBlank(order.getValue())) {
            orderItem.setAsc("ascend".equals(order.getValue()));
        }

        return orderItem;

    }

    /**
     * 分页属性拷贝-增加：默认创建时间 倒序排序
     */
    @Schema()
    public <T> Page<T> createTimeDescDefaultOrderPage() {

        Page<T> page = page(false);

        if (orderEmpty()) {
            page.orders().add(new OrderItem("createTime", false));
        }

        return page;

    }

}
