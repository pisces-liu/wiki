package com.jiawa.wiki.resp;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PageResp<T> {

    private Long total;

    private List<T> list;
}
