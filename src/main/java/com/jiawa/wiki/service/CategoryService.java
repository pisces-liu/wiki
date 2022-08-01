package com.jiawa.wiki.service;

import com.jiawa.wiki.domain.Category;
import com.jiawa.wiki.domain.CategoryExample;
import com.jiawa.wiki.mapper.CategoryMapper;
import com.jiawa.wiki.req.CategoryQueryReq;
import com.jiawa.wiki.resp.CategoryResp;
import com.jiawa.wiki.resp.PageResp;
import com.jiawa.wiki.util.CopyUtil;
import com.jiawa.wiki.util.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;

    /**
     * CategoryResp: 依照 Category 生成，作为返回值输出
     * CategoryQueryReq：依照 Category 生成，作为参数传递
     *
     * @return
     */
    public List<CategoryResp> all() {
        CategoryExample categoryExample = new CategoryExample();
        // 查询全部分类，因为分类字段很少，所以不做模糊查询
        categoryExample.setOrderByClause("sort asc");
        // 保存查询结果
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        // 保存返回结果
        List<CategoryResp> list = CopyUtil.copyList(categoryList, CategoryResp.class);
        return list;
    }

    /**
     * 更新和保存
     *
     * @param req
     */
    public void saveOrUpdate(CategoryQueryReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // 新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        } else {
            // 更新
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }


}
