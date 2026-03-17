# 翻译注解使用指南

## 概述

本项目实现了一套类似 Easy-Trans 的翻译注解系统，支持自动翻译、字典翻译、枚举翻译、方法翻译等多种翻译方式。

## 特性

- **自动翻译**：根据字段名自动推断源字段并翻译
- **字典翻译**：基于字典数据进行翻译
- **枚举翻译**：将枚举值翻译为可读文本
- **方法翻译**：通过自定义方法进行翻译
- **递归翻译**：支持嵌套对象和集合的自动翻译
- **零配置**：基于 AOP 切面自动处理，无需额外配置

## 快速开始

### 1. 在 VO/DTO 类中使用注解

```java
@Data
public class UserVo {
    private String userId;
    private String username;

    // 源字段
    private Integer status;  // 0-禁用, 1-启用

    // 自动翻译：字段名以 Name/Text/Desc 等结尾时，自动推断源字段
    @Translation(type = TranslationType.AUTO)
    private String statusName;  // 会自动翻译为"启用"或"禁用"
}
```

### 2. 注册字典数据（可选）

```java
@Configuration
@RequiredArgsConstructor
public class TranslationConfig implements ApplicationRunner {

    private final TranslationService translationService;

    @Override
    public void run(ApplicationArguments args) {
        // 注册用户状态字典
        Map<Object, String> userStatusMap = new HashMap<>();
        userStatusMap.put(0, "禁用");
        userStatusMap.put(1, "启用");
        translationService.registerDictionary("user_status", userStatusMap);
    }
}
```

## 注解详解

### @Translation

标记需要翻译的字段。

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| type | TranslationType | AUTO | 翻译类型 |
| source | String | "" | 源字段名，为空时自动推断 |
| dictType | String | "" | 字典类型（DICTIONARY 时使用） |
| enumClass | Class<?> | Void.class | 枚举类（ENUM 时使用） |
| method | String | "" | 翻译方法（METHOD 时使用） |
| params | String[] | {} | 其他参数 |

### @TranslationIgnore

标记忽略翻译的字段或类。

```java
@TranslationIgnore  // 整个类不参与翻译
public class IgnoreVo {
    private String field1;

    @TranslationIgnore  // 该字段不参与翻译
    private String field2;
}
```

## 翻译类型

### 1. AUTO - 自动翻译

根据字段名自动推断源字段并翻译。

```java
@Data
public class UserVo {
    private Integer status;     // 源字段

    @Translation(type = TranslationType.AUTO)
    private String statusName;  // 自动推断源字段为 status
}
```

自动推断规则：目标字段名去掉以下后缀后作为源字段名：
- Name
- Text
- Desc / Description
- Label
- Title

例如：`orderStatusName` -> `orderStatus`

### 2. DICTIONARY - 字典翻译

基于预注册的字典数据进行翻译。

```java
@Data
public class UserVo {
    private Integer status;

    @Translation(type = TranslationType.DICTIONARY, dictType = "user_status")
    private String statusName;
}
```

### 3. ENUM - 枚举翻译

将枚举值翻译为可读文本。

```java
@Data
public class UserVo {
    private String roleType;

    @Translation(type = TranslationType.ENUM, enumClass = RoleType.class)
    private String roleTypeText;
}
```

支持的枚举格式：
- MyBatis-Plus IEnum 接口实现
- 包含 getValue() / getDesc() / getLabel() 方法的枚举
- 包含 value / desc 字段的枚举

### 4. METHOD - 方法翻译

通过指定的方法进行翻译。

```java
@Data
public class UserVo {
    private Long level;

    @Translation(type = TranslationType.METHOD,
                 method = "com.titancore.service.UserService#getLevelName",
                 source = "level")
    private String levelName;
}
```

方法格式：
- `类全路径#方法名`：调用静态方法
- `beanName.methodName`：调用 Spring Bean 的方法

方法签名要求：
```java
public Object methodName(Object sourceValue)
```

## 高级用法

### 嵌套对象翻译

```java
@Data
public class OrderVo {
    private String orderId;

    // 嵌套的 UserVo 会自动翻译
    private UserVo user;

    // 集合中的对象也会自动翻译
    private List<OrderItemVo> items;
}
```

### 指定源字段

```java
@Data
public class UserVo {
    private Integer userStatus;  // 源字段名与目标字段名不匹配

    // 通过 source 属性指定源字段
    @Translation(type = TranslationType.DICTIONARY,
                 dictType = "user_status",
                 source = "userStatus")
    private String statusText;
}
```

### 自定义翻译处理器

实现 `TranslationHandler` 接口：

```java
@Component
public class MyTranslationHandler implements TranslationHandler {

    @Override
    public boolean supports(String type) {
        return "myType".equals(type);
    }

    @Override
    public Object translate(Object sourceValue, Translation translation, Class<?> targetFieldType) {
        // 自定义翻译逻辑
        return "翻译结果";
    }
}
```

## API 示例

启动项目后，访问以下接口查看翻译效果：

```bash
# 用户详情翻译演示
GET http://localhost:8080/demo/translation/user

# 订单嵌套翻译演示
GET http://localhost:8080/demo/translation/order

# 用户列表翻译演示
GET http://localhost:8080/demo/translation/users
```

## 注意事项

1. 翻译功能默认对所有 Controller 返回的结果生效
2. 支持递归翻译嵌套对象和集合
3. 翻译失败不会抛出异常，会记录警告日志
4. 建议在开发阶段关闭翻译缓存以便调试

## 依赖

本功能已集成在 `bbs-framework/common` 模块中，无需额外依赖。
