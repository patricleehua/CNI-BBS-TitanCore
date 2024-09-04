package com.titancore.framework.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "titan.aes")
@Data
public class AESKey {
    private String secretKey;
}
