package com.jiawa.wiki.controller;

import com.jiawa.wiki.req.DocQueryReq;
import com.jiawa.wiki.resp.DocResp;
import com.jiawa.wiki.resp.CommonResp;
import com.jiawa.wiki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocController {

    @Resource
    private DocService docService;

    @RequestMapping("/all")
    public CommonResp all() {
        CommonResp<List<DocResp>> commonResp = new CommonResp<>();
        List<DocResp> all = docService.all();
        commonResp.setContent(all);
        return commonResp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody DocQueryReq req) {
        CommonResp commonResp = new CommonResp();
        docService.saveOrUpdate(req);
        return commonResp;
    }

/*    @DeleteMapping("/delete/{id}")
    public CommonResp del(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp();
        docService.delete(id);
        return commonResp;
    }*/

    @DeleteMapping("/delete/{idsStr}")
    public CommonResp del(@PathVariable String idsStr) {
        CommonResp commonResp = new CommonResp();

        List<String> asList = Arrays.asList(idsStr.split(","));
        docService.delete(asList);
        return commonResp;
    }
}
