package net.riblab.tradecore.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.item.base.AttackDamageSpread;
import net.riblab.tradecore.item.base.TCDeserializedItemHolder;
import net.riblab.tradecore.item.base.TCItem;
import net.riblab.tradecore.item.mod.ModDefaultAttackDamageI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
    
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

    /**
     * アイテムデータの保存フォルダ
     */
    private final File itemDir;

    public DataServiceImpl(File dataFolder) {
        saveDir = new File(dataFolder, "/saves");
        currencyDataFile = new File(saveDir, "/currency.json");
        jobsDataFile = new File(saveDir, "/jobs.json");
        itemDir = new File(dataFolder, "items");
    }

    @Override
    public void save() {
        save(currencyData, currencyDataFile);
        save(jobDatas, jobsDataFile);
    }
    
    private void save(Object dataInstance, File file){
        String str = gson.toJson(dataInstance);
        try {
            FileUtils.forceMkdir(new File(file.getParent()));
            FileUtils.fileWrite(file, str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load() {
        currencyData =  load(currencyDataFile, CurrencyData.class);
        jobDatas = load(jobsDataFile, JobDatas.class);
        loadItems();
    }
    
    private <T> T load(File dataFile, Class<T> dataType){
        String str = null;
        try {
            str =  FileUtils.fileRead(dataFile);
        } catch (IOException ignored) {
        }
        T dataInstance = gson.fromJson(str, dataType);
        if(dataInstance == null) {
            try {
                dataInstance = dataType.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return dataInstance;
    }
    
    private void loadItems(){
        List<File> itemFiles;
        try {
            itemFiles = FileUtils.getFiles(itemDir,null,null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        itemFiles.forEach(TCDeserializedItemHolder.INSTANCE::deserialize);

        //SAVE TEST
        TCDeserializedItemHolder.SerializedTCItems items = new TCDeserializedItemHolder.SerializedTCItems();
        items.getMap().put("nextpage" ,new TCItem(Component.text("次のページ"), Material.ARROW, "nextpage", 1, List.of()));
        items.getMap().put("cursedHead" ,new TCItem(Component.text("カースドヘッド"), Material.SKELETON_SKULL, "cursedHead", 0, List.of()));
    }
}