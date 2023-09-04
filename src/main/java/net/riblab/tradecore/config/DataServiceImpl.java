package net.riblab.tradecore.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCDeserializedItemHolder;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * コンフィグ管理システム
 */
enum DataServiceImpl implements DataService {
    INSTANCE;

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
    
    private final File itemExportFile;

    DataServiceImpl() {
        saveDir = new File(TradeCore.getInstance().getDataFolder(), "/saves");
        currencyDataFile = new File(saveDir, "/currency.json");
        jobsDataFile = new File(saveDir, "/jobs.json");
        itemDir = new File(TradeCore.getInstance().getDataFolder(), "items");
        itemExportFile = new File(TradeCore.getInstance().getDataFolder(), "/itemexport/exportedItem.yml");
    }

    @Override
    public void saveAll() {
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
    public void loadAll() {
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

    /**
     * アイテムをデータフォルダから読み込む
     */
    public void loadItems(){
        TCDeserializedItemHolder.INSTANCE.getDeserializedItems().clear();
        
        List<File> itemFiles;
        try {
            itemFiles = FileUtils.getFiles(itemDir,null,null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (File itemFile : itemFiles) {
            List<ITCItem> deserializedItems = ItemIOUtils.deserialize(itemFile);
            TCDeserializedItemHolder.INSTANCE.getDeserializedItems().addAll(deserializedItems);
        }
    }

    /**
     * アイテムを既定のファイルにエクスポートする
     */
    public void exportItem(ITCItem item){
        ItemIOUtils.saveItem(item, itemExportFile);
    }
}