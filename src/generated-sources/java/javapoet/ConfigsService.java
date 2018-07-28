package javapoet;

import java.lang.Long;

interface ConfigsService {
    /**
     * 通过ID获取用户对象
     */
    Configs getConfigs(Long id);

    /**
     * 更新对象
     */
    Configs updateConfigs(Configs configs);

    /**
     * 创建对象
     */
    Configs createConfigs(Configs configs);
}
