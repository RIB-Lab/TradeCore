package net.riblab.tradecore.config.io;


import net.riblab.tradecore.item.ILootTable;
import org.codehaus.plexus.util.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class LootTableIO implements InterfaceIO<Map<String, ILootTable>> {
    private final Yaml yaml;

    public LootTableIO() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // フロースタイルを指定
        yaml = new Yaml(options);
    }
    
    @Override
    public Map<String, ILootTable> deserialize(File fileToLoad) {
        return null;
    }

    @Override
    public void serialize(Map<String, ILootTable> objectToSave, File fileToSave) {
        Map<String, Object> lootTableReady = new HashMap<>();
        
        for (Map.Entry<String, ILootTable> entry : objectToSave.entrySet()) {
            Map<String, Object> lootTableParams = new HashMap<>();
            lootTableParams.put("materialset", entry.getValue().getMaterialSetKey());
            lootTableParams.put("tooltype", entry.getValue().getToolType().toString());
            lootTableParams.put("harvestlevel", entry.getValue().getHarvestLevel());
            lootTableParams.put("dropchancelevel", entry.getValue().getDropChanceMap());

            lootTableReady.put(entry.getKey(), lootTableParams);
        }

        if (!fileToSave.getParentFile().exists())
            fileToSave.getParentFile().mkdirs();
        FileUtils.getFile(fileToSave.toString());
        try (FileWriter writer = new FileWriter(fileToSave)) {
            yaml.dump(lootTableReady, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
