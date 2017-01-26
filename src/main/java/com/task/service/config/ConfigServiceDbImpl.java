package com.task.service.config;

import com.task.bean.Config;
import com.task.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by blanke on 17-1-27.
 */
@Service
public class ConfigServiceDbImpl implements ConfigService {
    @Autowired
    ConfigRepository configRepository;

    @Override
    public void addConfig(Config config) {
        configRepository.save(config);
    }

    @Override
    public void deleteConfig(int id) {
        configRepository.delete(id);
    }

    @Override
    public List<Config> getAllConfig() {
        return configRepository.findAll();
    }
}
