package net.riblab.tradecore.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * コンフィグ管理システム
 */
final class DataServiceImpl implements DataService {

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
    private final File currencyDataFile;

    /**
     * コンフィグの保存Path
     */
    private final File jobsDataFile;

    /**
     * セーブ全般の保存フォルダ
     */
    private final File saveDir;

    public DataServiceImpl(File dataFolder) {
        saveDir = new File(dataFolder, "/saves");
        currencyDataFile = new File(saveDir, "/currency.json");
        jobsDataFile = new File(saveDir, "/jobs.json");
    }

    @Override
    public void save() {
//        YamlConfigurations.save(currencyDataFile, CurrencyData.class, currencyData);
//        YamlConfigurations.save(jobsDataFile, JobDatas.class, jobDatas);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String currencyStr = gson.toJson(currencyData);
        try {
            FileUtils.forceMkdir(saveDir);
            FileUtils.fileWrite(currencyDataFile, currencyStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String jobsStr = gson.toJson(jobDatas);
        try {
            FileUtils.forceMkdir(saveDir);
            FileUtils.fileWrite(jobsDataFile, jobsStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load() {
        // Load a new instance from the configuration file
//        currencyData = YamlConfigurations.update(currencyDataFile, CurrencyData.class);
//        jobDatas = YamlConfigurations.update(jobsDataFile, JobDatas.class);
        String currencyStr = null;
        try {
            currencyStr =  FileUtils.fileRead(currencyDataFile);
        } catch (IOException ignored) {
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        currencyData = gson.fromJson(currencyStr, CurrencyData.class);
        if(currencyData == null)
            currencyData = new CurrencyData();

        String jobsStr = null;
        try {
            jobsStr =  FileUtils.fileRead(jobsDataFile);
        } catch (IOException ignored) {
        }
        jobDatas = gson.fromJson(jobsStr, JobDatas.class);
        if(jobDatas == null)
            jobDatas = new JobDatas();
    }
}