package com.cmcorg20230301.engine.be.pay.google.model.bo;

import lombok.Data;

@Data
public class SysPayGooglePurchasesBO {

    /**
     * 这种类型表示 androidpublisherservice 中的 inappPurchase 对象。
     */
    private String kind;

    /**
     * 订单的购买状态。可能的值包括：0。购买 1. 已取消 2. 待处理
     */
    private Integer purchaseState;

    /**
     * 应用内商品的消耗状态。可能的值包括：0。尚未消耗 1. 已使用
     */
    private Integer consumptionState;

    /**
     * 开发者指定的字符串，其中包含关于订单的补充信息。
     */
    private String developerPayload;

    /**
     * 与购买应用内商品相关的订单 ID。
     */
    private String orderId;

    /**
     * 应用内商品的购买类型。仅当购买交易不是使用标准的应用内购买结算流程完成时，系统才会设置此字段。可能的值包括：0。测试（即从许可测试帐号中购买的服务）1.促销（即使用促销代码购买）2.激励广告（即通过观看视频广告而不是付费）
     */
    private Integer purchaseType;

    /**
     * 应用内商品的确认状态。可能的值包括：0。尚未确认 1. 已确认
     */
    private Integer acknowledgementState;

}
