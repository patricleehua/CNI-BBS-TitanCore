package com.titancore.domain.vo.example;

import com.titancore.framework.common.translation.annotation.Translation;
import com.titancore.framework.common.translation.annotation.TranslationIgnore;
import com.titancore.framework.common.translation.enums.TranslationType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单VO - 翻译注解使用示例
 * 展示嵌套对象翻译和集合翻译
 *
 * @author TitanCore
 */
@Data
public class OrderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderId;
    private String orderNo;

    /**
     * 订单状态名称（自动翻译）
     * 会自动推断源字段为 orderStatus
     */
    @Translation(type = TranslationType.AUTO)
    private String orderStatusName;

    /**
     * 订单状态编码
     * 0-待支付, 1-已支付, 2-已发货, 3-已完成, 4-已取消
     */
    private Integer orderStatus;

    /**
     * 支付方式名称（字典翻译）
     */
    @Translation(type = TranslationType.DICTIONARY, dictType = "pay_type", source = "payType")
    private String payTypeName;

    /**
     * 支付方式编码
     */
    private String payType;

    private BigDecimal totalAmount;
    private LocalDateTime createTime;

    /**
     * 用户信息（嵌套对象会自动翻译）
     */
    private UserDetailVo user;

    /**
     * 订单商品列表（集合中的对象会自动翻译）
     */
    private List<OrderItemVo> items;

    /**
     * 内部类：订单商品项
     */
    @Data
    public static class OrderItemVo implements Serializable {
        private static final long serialVersionUID = 1L;

        private String itemId;
        private String productName;

        /**
         * 商品分类名称（自动翻译）
         * 会自动推断源字段为 categoryCode
         */
        @Translation(type = TranslationType.AUTO)
        private String categoryName;

        private String categoryCode;
        private Integer quantity;
        private BigDecimal price;
    }
}
