package com.jiawa.wiki.req;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class PageReq {
    @NotNull(message = "[页码]不能为空")
    private int page;

    @NotNull(message = "[每条页数]不能为空")
    @Max(message = "【每条页数】不能超过1000", value = 1000)
    private int size;
}
