package com.jiawa.wiki.resp;

import lombok.Data;

@Data
public class EbookResp {
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