package net.riblab.tradecore.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.job.data.IJobData;
import net.riblab.tradecore.job.data.JobData;
import net.riblab.tradecore.job.skill.IJobSkill;
import net.riblab.tradecore.job.skill.JobSkill;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private static final Path currencyConfigFile = new File(TradeCore.getInstance().getDataFolder(), "currency.yml").toPath();

    /**
     * コンフィグの保存Path
     */
    private static final Path jobsConfigFile = new File(TradeCore.getInstance().getDataFolder(), "jobs.yml").toPath();

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