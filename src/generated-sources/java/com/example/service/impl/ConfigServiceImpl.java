package com.example.service.impl;

import com.example.entity.Config;
import com.example.service.ConfigService;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConfigServiceImpl implements ConfigService {
    /**
     * 通过ID获取用户对象
     */
    @Override
    public Config getConfig(Long id) {
        Config config = new Config();
        return config;
    }

    /**
     * 更新对象
     */
    @Override
    public Config updateConfig(Config config) {
        return config;
    }

    /**
     * 创建对象
     */
    @Override
    public Config createConfig(Config config) {
        return config;
    }

    /**
     * 对象分页列表
     */
    @Override
    public List<Config> paginateConfigs(Integer page, Integer size) {
        return null;
    }
}
