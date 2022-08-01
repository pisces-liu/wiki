package com.jiawa.wiki.controller;

import com.jiawa.wiki.req.CategoryQueryReq;
import com.jiawa.wiki.resp.CategoryResp;
import com.jiawa.wiki.resp.CommonResp;
import com.jiawa.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @RequestMapping("/all")
    public CommonResp all() {
        // 创建统一保存结果变量，泛型为 List<CategoryResp> 保存查询结果
        CommonResp<List<CategoryResp>> commonResp = new CommonResp<>();

        // 保存 service 查询结果
        List<CategoryResp> all = categoryService.all();
        // 设置返回值
        commonResp.setContent(all);

        return commonResp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody CategoryQueryReq req) {
        CommonResp commonResp = new CommonResp();
        categoryService.saveOrUpdate(req);
        return commonResp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp del(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp();
        categoryService.delete(id);
        return commonResp;
    }


}
