package com.jiawa.wiki.service;

import com.jiawa.wiki.domain.Content;
import com.jiawa.wiki.domain.Doc;
import com.jiawa.wiki.domain.DocExample;
import com.jiawa.wiki.mapper.ContentMapper;
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
    private DocMapper docMapper;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<DocResp> all() {
        DocExample DocExample = new DocExample();
        DocExample.setOrderByClause("sort asc");
        List<Doc> DocList = docMapper.selectByExample(DocExample);
        List<DocResp> list = CopyUtil.copyList(DocList, DocResp.class);
        return list;
    }

    public DocResp all(Long id) {
        DocExample DocExample = new DocExample();
        Doc doc = docMapper.selectByPrimaryKey(id);
        DocResp docResp = CopyUtil.copy(doc, DocResp.class);
        return docResp;
    }

    public void saveOrUpdate(DocQueryReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);

            content.setId(doc.getId()); // 确保 content 的 id 与 doc 的 id 一致
            contentMapper.insert(content);
        } else {
            docMapper.updateByPrimaryKey(doc);
            int i = contentMapper.updateByPrimaryKeyWithBLOBs(content);// withBLOBS 有关于操作大字段的
            if (i == 0) {
                contentMapper.insert(content);
            }
        }
    }

    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    // 批量删除文档
    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public String finContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        return content.getContent();
    }


}
