package net.riblab.tradecore.config;

import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;

import java.io.File;
import java.nio.file.Path;

/**
 * コンフィグ管理システム
 */
class ConfigServiceImpl implements ConfigService {
    
    /**
     * 保存するコンフィグの実体
     */
    @Getter
    private CurrencyData currencyData;
    @Getter
    private JobDatas jobDatas;

    /**
     * コンフィグの保存Path
     */
    private final Path currencyConfigFile;

    /**
     * コンフィグの保存Path
     */
    private final Path jobsConfigFile;

    public ConfigServiceImpl(File dataFolder){
        currencyConfigFile = new File(dataFolder, "currency.yml").toPath();
        jobsConfigFile = new File(dataFolder, "jobs.yml").toPath();
    }
    
    @Override
    public void save() {
        YamlConfigurations.save(currencyConfigFile, CurrencyData.class, currencyData);
        YamlConfigurations.save(jobsConfigFile, JobDatas.class, jobDatas);
    }

    @Override
    public void load() {
        // Load a new instance from the configuration file
        currencyData = YamlConfigurations.update(currencyConfigFile, CurrencyData.class);
        jobDatas = YamlConfigurations.update(jobsConfigFile, JobDatas.class);
    }
}