package com.titancore.framework.cloud.manager.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File implements Serializable {
    private String fileName;
    private String fileSize;
    private String uploadTime;

}
