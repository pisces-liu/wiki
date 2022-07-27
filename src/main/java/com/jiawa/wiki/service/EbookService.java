package com.jiawa.wiki.service;

import com.jiawa.wiki.domain.Ebook;
import com.jiawa.wiki.domain.EbookExample;
import com.jiawa.wiki.mapper.EbookMapper;
import com.jiawa.wiki.req.EbookReq;
import com.jiawa.wiki.resp.CommonResp;
import com.jiawa.wiki.resp.EbookResp;
import com.jiawa.wiki.util.CopyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    @Autowired
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req) {
        // 这两行是固定写法
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();

        // 当图书名称不为空时，才进行模糊查询，否则查询全部
        if (!ObjectUtils.isEmpty(req.getName())) {
            // 根据图书名称模糊查询
            criteria.andNameLike("%" + req.getName() + "%");
        }
        // 返回查询的 ebook 集合
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        ArrayList<EbookResp> respList = new ArrayList<>();

        /*
        for (Ebook ebook : ebookList) {
            // EbookResp ebookResp = new EbookResp();
            // BeanUtils.copyProperties(ebook,ebookResp);
            //respList.add(ebookResp);

            // 拷贝单个对象
            EbookResp copy = CopyUtil.copy(ebook, EbookResp.class);
            respList.add(copy);
        }*/

        // 拷贝列表
        List<EbookResp> ebookResps = CopyUtil.copyList(ebookList, EbookResp.class);

        return ebookResps;
    }
}
