package com.titancore.controller;

import com.titancore.domain.vo.example.OrderVo;
import com.titancore.domain.vo.example.UserDetailVo;
import com.titancore.framework.common.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 翻译注解演示控制器
 * 展示 @Translation 注解的使用效果
 *
 * @author TitanCore
 */
//@RestController
@RequestMapping("/demo/translation")
@Tag(name = "翻译注解演示", description = "展示 @Translation 注解的自动翻译功能")
@Slf4j
public class TranslationDemoController {

    /**
     * 演示自动翻译
     */
    @GetMapping("/user")
    @Operation(summary = "用户详情自动翻译演示")
    public Response<UserDetailVo> getUserDetail() {
        UserDetailVo user = new UserDetailVo();
        user.setUserId("123456");
        user.setUsername("zhangsan");
        user.setNickname("张三");
        user.setRole("admin_user");                    // 角色编码
        user.setRoleType("superpower_user");           // 角色类型编码
        user.setStatus(1);                              // 状态：1-启用
        user.setLevel(3);                               // 等级
        user.setEmail("zhangsan@example.com");
        user.setAvatar("https://example.com/avatar.jpg");

        // 注意：roleName, roleTypeText, statusText, levelName 会被自动翻译
        return Response.success(user);
    }

    /**
     * 演示嵌套对象和集合的自动翻译
     */
    @GetMapping("/order")
    @Operation(summary = "订单嵌套翻译演示")
    public Response<OrderVo> getOrderDetail() {
        OrderVo order = new OrderVo();
        order.setOrderId("ORDER001");
        order.setOrderNo("NO20240317001");
        order.setOrderStatus(1);                        // 已支付
        order.setPayType("alipay");                     // 支付宝
        order.setTotalAmount(new BigDecimal("299.99"));
        order.setCreateTime(LocalDateTime.now());

        // 设置用户信息（嵌套对象）
        UserDetailVo user = new UserDetailVo();
        user.setUserId("123456");
        user.setUsername("zhangsan");
        user.setNickname("张三");
        user.setRole("normal_user");
        user.setRoleType("normal_user");
        user.setStatus(1);
        user.setLevel(2);
        order.setUser(user);

        // 设置订单商品列表
        List<OrderVo.OrderItemVo> items = new ArrayList<>();

        OrderVo.OrderItemVo item1 = new OrderVo.OrderItemVo();
        item1.setItemId("ITEM001");
        item1.setProductName("Spring Boot 实战");
        item1.setCategoryCode("book");
        item1.setQuantity(1);
        item1.setPrice(new BigDecimal("99.99"));
        items.add(item1);

        OrderVo.OrderItemVo item2 = new OrderVo.OrderItemVo();
        item2.setItemId("ITEM002");
        item2.setProductName("Java 编程思想");
        item2.setCategoryCode("book");
        item2.setQuantity(2);
        item2.setPrice(new BigDecimal("100.00"));
        items.add(item2);

        order.setItems(items);

        return Response.success(order);
    }

    /**
     * 演示列表翻译
     */
    @GetMapping("/users")
    @Operation(summary = "用户列表自动翻译演示")
    public Response<List<UserDetailVo>> getUserList() {
        List<UserDetailVo> users = new ArrayList<>();

        UserDetailVo user1 = new UserDetailVo();
        user1.setUserId("123456");
        user1.setUsername("zhangsan");
        user1.setNickname("张三");
        user1.setRole("admin_user");
        user1.setRoleType("admin_user");
        user1.setStatus(1);
        users.add(user1);

        UserDetailVo user2 = new UserDetailVo();
        user2.setUserId("123457");
        user2.setUsername("lisi");
        user2.setNickname("李四");
        user2.setRole("normal_user");
        user2.setRoleType("normal_user");
        user2.setStatus(0);  // 禁用状态
        users.add(user2);

        return Response.success(users);
    }
}
