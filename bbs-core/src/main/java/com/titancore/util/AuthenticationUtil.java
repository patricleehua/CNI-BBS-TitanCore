package com.titancore.util;

import cn.dev33.satoken.stp.StpUtil;
import com.titancore.enums.ResponseCodeEnum;
import com.titancore.enums.RoleType;
import com.titancore.framework.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;

public class AuthenticationUtil {

    public static void checkUserId(String userId){
        if(StringUtils.isEmpty(userId)){
            throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_IS_MISSED);
        }else{
            if(!StpUtil.getLoginId().equals(userId)){
                if(!StpUtil.hasRole(RoleType.SUPERPOWER_USER.getValue())){
                    throw new BizException(ResponseCodeEnum.AUTH_ACCOUNT_IS_DIFFERENT);
                }
            }
        }
    }


}
