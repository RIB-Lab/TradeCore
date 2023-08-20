package net.riblab.tradecore;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobSkill;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * コンフィグ管理システム
 */
public class ConfigManager {

    /**
     * 保存するコンフィグの型
     */
    @Configuration
    public static class CurrencyData {
        @Comment({"所持金"})
        public Map<UUID, Double> playerBank = new HashMap<>();
        @Comment({"所持プレイチケット数"})
        public Map<UUID, Integer> playerTickets = new HashMap<>();
    }

    @Configuration
    public static class JobDatas {
        @Comment({"職業"})
        public Map<UUID, List<JobData>> playerJobs = new HashMap<>();

        @Comment({"職業スキル"})
        public Map<UUID, List<JobSkill>> playerJobSkills = new HashMap<>();
    }

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

    /**
     * コンフィグを保存する
     */
    public void save() {
        YamlConfigurations.save(currencyConfigFile, CurrencyData.class, currencyData);
        YamlConfigurations.save(jobsConfigFile, JobDatas.class, jobDatas);
    }

    /**
     * コンフィグをロードする
     */
    public void load() {
        // Load a new instance from the configuration file
        currencyData = YamlConfigurations.update(currencyConfigFile, CurrencyData.class);
        jobDatas = YamlConfigurations.update(jobsConfigFile, JobDatas.class);
    }
}