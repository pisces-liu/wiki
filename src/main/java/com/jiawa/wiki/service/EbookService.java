package com.jiawa.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.wiki.domain.Ebook;
import com.jiawa.wiki.domain.EbookExample;
import com.jiawa.wiki.mapper.EbookMapper;
import com.jiawa.wiki.req.EbookReq;
import com.jiawa.wiki.resp.EbookResp;
import com.jiawa.wiki.resp.PageResp;
import com.jiawa.wiki.util.CopyUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    @Resource
    private EbookMapper ebookMapper;

    public PageResp<EbookResp> list(EbookReq req) {
        // 这两行是固定写法
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();

        // 当图书名称不为空时，才进行模糊查询，否则查询全部
        if (!ObjectUtils.isEmpty(req.getName())) {
            // 根据图书名称模糊查询
            criteria.andNameLike("%" + req.getName() + "%");
        }

        // 开启分页
        PageHelper.startPage(req.getPage(), req.getSize());

        // 返回查询的 ebook 集合
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);

        ArrayList<EbookResp> respList = new ArrayList<>();

        // 拷贝列表
        List<EbookResp> ebookResps = CopyUtil.copyList(ebookList, EbookResp.class);

        PageResp<EbookResp> ebookRespPageResp = new PageResp<>();
        ebookRespPageResp.setTotal(pageInfo.getTotal());
        ebookRespPageResp.setList(ebookResps);

        return ebookRespPageResp;
    }
}
