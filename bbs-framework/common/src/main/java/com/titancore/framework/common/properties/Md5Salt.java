package com.titancore.framework.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "titan.md5")
@Data
public class Md5Salt {
    private String salt;
}
