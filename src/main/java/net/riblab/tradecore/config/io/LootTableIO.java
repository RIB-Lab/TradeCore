package net.riblab.tradecore.config.io;


import net.riblab.tradecore.item.ILootTable;

import java.io.File;
import java.util.Map;

public class LootTableIO implements InterfaceIO<Map<String, ILootTable>> {
    @Override
    public Map<String, ILootTable> deserialize(File fileToLoad) {
        return null;
    }

    @Override
    public void serialize(Map<String, ILootTable> objectToSave, File fileToSave) {

    }
}
