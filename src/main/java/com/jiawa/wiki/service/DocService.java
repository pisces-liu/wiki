package com.jiawa.wiki.service;

import com.jiawa.wiki.domain.Doc;
import com.jiawa.wiki.domain.DocExample;
import com.jiawa.wiki.mapper.DocMapper;
import com.jiawa.wiki.req.DocQueryReq;
import com.jiawa.wiki.resp.DocResp;
import com.jiawa.wiki.util.CopyUtil;
import com.jiawa.wiki.util.SnowFlake;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {

    @Resource
    private DocMapper DocMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<DocResp> all() {
        DocExample DocExample = new DocExample();
        DocExample.setOrderByClause("sort asc");
        List<Doc> DocList = DocMapper.selectByExample(DocExample);
        List<DocResp> list = CopyUtil.copyList(DocList, DocResp.class);
        return list;
    }

    public void saveOrUpdate(DocQueryReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            System.out.println("save: doc.id = " + doc.getId());

            doc.setId(snowFlake.nextId());
            DocMapper.insert(doc);
        } else {
            System.out.println("update: doc.id = " + doc.getId());
            DocMapper.updateByPrimaryKey(doc);
        }
    }

    public void delete(Long id) {
        DocMapper.deleteByPrimaryKey(id);
    }


}
