package javapoet;

import java.lang.Long;

interface ConfigService {
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
}
