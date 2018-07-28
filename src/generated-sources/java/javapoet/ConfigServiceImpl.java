package javapoet;

import java.lang.Long;
import org.springframework.stereotype.Service;

@Service
class ConfigServiceImpl implements ConfigService {
    /**
     * 通过ID获取用户对象
     */
    public Config getConfig(Long id) {
        Config config = new Config();
        return config;
    }

    /**
     * 更新对象
     */
    public Config updateConfig(Config config) {
        return config;
    }

    /**
     * 创建对象
     */
    public Config createConfig(Config config) {
        return config;
    }
}
