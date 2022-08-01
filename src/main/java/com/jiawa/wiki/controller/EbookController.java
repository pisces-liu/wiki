package com.jiawa.wiki.controller;

import com.jiawa.wiki.req.EbookQueryReq;
import com.jiawa.wiki.req.EbookSaveReq;
import com.jiawa.wiki.resp.CommonResp;
import com.jiawa.wiki.resp.EbookResp;
import com.jiawa.wiki.resp.PageResp;
import com.jiawa.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Autowired
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp getList(@Valid EbookQueryReq ebookReq) {
        CommonResp<PageResp<EbookResp>> resp = new CommonResp<>();
        PageResp<EbookResp> list = ebookService.list(ebookReq);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp saveOrUpdate(@Valid @RequestBody EbookSaveReq ebookSaveReq) {
        ebookService.saveOrUpdate(ebookSaveReq);
        return new CommonResp();
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable("id") Long id) {
        ebookService.delete(id);
        return new CommonResp();
    }

}
