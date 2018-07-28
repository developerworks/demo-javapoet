package com.example.service;

import com.example.entity.Config;
import java.lang.Integer;
import java.lang.Long;
import java.util.List;

public interface ConfigService {
    /**
     * 通过ID获取用户对象
     */
    Config getConfig(Long id);

    /**
     * 更新对象
     */
    Config updateConfig(Config config);

    /**
     * 创建对象
     */
    Config createConfig(Config config);

    /**
     * 对象分页列表
     */
    List<Config> paginateConfig(Integer page, Integer size);
}
