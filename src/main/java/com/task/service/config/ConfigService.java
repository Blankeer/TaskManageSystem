package com.task.service.config;

import com.task.bean.Config;

import java.util.List;

/**
 * Created by blanke on 17-1-27.
 */
public interface ConfigService {
    void addConfig(Config config);

    void deleteConfig(int id);

    List<Config> getAllConfig();
}
