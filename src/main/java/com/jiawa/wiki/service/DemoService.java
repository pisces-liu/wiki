package com.jiawa.wiki.service;

import com.jiawa.wiki.domain.Demo;
import com.jiawa.wiki.domain.DemoExample;
import com.jiawa.wiki.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoService {
    @Autowired
    private DemoMapper demoMapper;

    public List<Demo> list() {
        return demoMapper.selectByExample(new DemoExample());
    }
}
