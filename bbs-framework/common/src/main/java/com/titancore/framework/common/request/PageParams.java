package com.titancore.framework.common.request;

import lombok.Data;

@Data
public class PageParams {
    private int pageNo = 0;//第几页
    private int pageSize = 10;//每页多少个
}
