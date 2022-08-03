package com.jiawa.wiki.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocQueryReq {
    private Long id;

    @NotNull(message = "【电子书】不能为空")
    private Long ebookId;

    @NotNull(message = "【夫文档】不能为空")
    private Long parent;

    @NotNull(message = "【名称】不能为空")
    private String name;

    @NotNull(message = "【顺序】不能为空")
    private Integer sort;

    private Integer viewCount;

    private Integer voteCount;

    /**
     * 该表添加了 content 字段之后，相当于 VO 对象
     */
    @NotNull(message = "【内容】不能为空")
    private String content;

}