package com.titancore.framework.cloud.manager.domain.vo;

import com.titancore.framework.cloud.manager.domain.entity.File;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FileListVo implements Serializable {
    //用户ID
    private String userId;
    //文件所属的用户名
    private String userName;
    //文件列表
    private List<File> files;

}
