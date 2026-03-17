package com.titancore.domain.vo.example;

import com.titancore.enums.RoleType;
import com.titancore.enums.StatusEnum;
import com.titancore.framework.common.translation.annotation.Translation;
import com.titancore.framework.common.translation.enums.TranslationType;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户详情VO - 翻译注解使用示例
 *
 * @author TitanCore
 */
@Data
public class UserDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String username;
    private String nickname;

    /**
     * 自动翻译示例
     * 字段名以 Name/Text/Desc/Description/Label/Title 结尾时
     * 会自动推断源字段并翻译
     * 例如：roleName 会自动找到 role 字段的值进行翻译
     */
    @Translation(type = TranslationType.AUTO)
    private String roleName;

    /**
     * 角色编码（源字段）
     */
    private String role;

    /**
     * 枚举翻译示例
     * 使用枚举类进行翻译
     */
    @Translation(type = TranslationType.ENUM, enumClass = RoleType.class)
    private String roleTypeText;

    /**
     * 角色类型编码（源字段）
     */
    private String roleType;

    /**
     * 字典翻译示例
     * 指定字典类型进行翻译
     */
    @Translation(type = TranslationType.DICTIONARY, dictType = "user_status", source = "status")
    private String statusText;

    /**
     * 状态编码（源字段）
     * 0-禁用, 1-启用
     */
    private Integer status;

    /**
     * 方法翻译示例
     * 通过指定的方法进行翻译
     */
    @Translation(type = TranslationType.METHOD, method = "com.titancore.service.UserService#getUserLevelName", source = "level")
    private String levelName;

    /**
     * 等级编码（源字段）
     */
    private Integer level;

    private String avatar;
    private String email;
    private String phone;
}
