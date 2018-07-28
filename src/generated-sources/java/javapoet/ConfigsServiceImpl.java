package javapoet;

import java.lang.Long;

import org.springframework.stereotype.Service;

@Service
class ConfigsServiceImpl implements ConfigsService {
    /**
     * 通过ID获取用户对象
     */
    public Configs getConfigs(Long id) {
        Configs configs = new Configs();
        return configs;
    }

    /**
     * 更新对象
     */
    public Configs updateConfigs(Configs configs) {
        return configs;
    }

    /**
     * 创建对象
     */
    public Configs createConfigs(Configs configs) {
        return configs;
    }
}
