package com.jiawa.wiki.controller;

import com.jiawa.wiki.req.UserQueryReq;
import com.jiawa.wiki.req.UserSaveReq;
import com.jiawa.wiki.resp.CommonResp;
import com.jiawa.wiki.resp.UserQueryResp;
import com.jiawa.wiki.resp.UserResp;
import com.jiawa.wiki.resp.PageResp;
import com.jiawa.wiki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public CommonResp list(@Valid UserQueryReq req) {
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp saveOrUpdate(@Valid @RequestBody UserSaveReq userSaveReq) {
        userService.saveOrUpdate(userSaveReq);
        return new CommonResp();
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return new CommonResp();
    }

}
