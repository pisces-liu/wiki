package com.jiawa.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiawa.wiki.domain.Ebook;
import com.jiawa.wiki.domain.EbookExample;
import com.jiawa.wiki.mapper.EbookMapper;
import com.jiawa.wiki.req.EbookQueryReq;
import com.jiawa.wiki.req.EbookSaveReq;
import com.jiawa.wiki.resp.EbookResp;
import com.jiawa.wiki.resp.PageResp;
import com.jiawa.wiki.util.CopyUtil;
import com.jiawa.wiki.util.SnowFlake;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    @Resource
    private EbookMapper ebookMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<EbookResp> list(EbookQueryReq req) {
        // 这两行是固定写法
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();

        // 当图书名称不为空时，才进行模糊查询，否则查询全部
        if (!ObjectUtils.isEmpty(req.getName())) {
            // 根据图书名称模糊查询
            criteria.andNameLike("%" + req.getName() + "%");
        }

        // 根据分类名称进行查询
        if (!ObjectUtils.isEmpty(req.getCategory2Id())) {
            criteria.andCategory2IdEqualTo(req.getCategory2Id());
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

    /**
     * 保存或新增电子书操作
     *
     * @param ebookSaveReq
     */
    public void saveOrUpdate(EbookSaveReq ebookSaveReq) {
        // 由于 mapper 操作的对象是 Ebook，所以使用 CopyUtil 进行对象拷贝
        Ebook ebook = CopyUtil.copy(ebookSaveReq, Ebook.class);
        // 如果 id 为空，则进行增加操作，否则进行修改操作
        if (ObjectUtils.isEmpty(ebook.getId())) {
            // 新增
            // 使用雪花算法生成 id
            ebook.setId(snowFlake.nextId());
            ebookMapper.insert(ebook);
        } else {
            // 修改
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

    public void delete(Long id) {
        ebookMapper.deleteByPrimaryKey(id);
    }
}
