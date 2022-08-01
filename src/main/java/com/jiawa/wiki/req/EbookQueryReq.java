package com.jiawa.wiki.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EbookQueryReq extends PageReq {
    private Long id;

    private String name;

    private Long category1Id;

    private Long category2Id;

    private String description;

    private Integer docCount;

    private Integer viewCount;

    private Integer voteCount;

    private String cover;

}