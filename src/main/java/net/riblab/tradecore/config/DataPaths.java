package net.riblab.tradecore.config;

import net.riblab.tradecore.TradeCore;

import java.io.File;

enum DataPaths {
    SAVE_DIR(new File(TradeCore.getInstance().getDataFolder(), "/saves")),
    CURRENCY_DATA_FILE(new File(SAVE_DIR.get(), "/currency.json")),
    JOBS_DATA_FILE(new File(SAVE_DIR.get(), "/jobs.json")),
    ITEM_DIR(new File(TradeCore.getInstance().getDataFolder(), "items")),
    ITEM_EXPORT_FILE(new File(TradeCore.getInstance().getDataFolder(), "/export/items.yml")),
    CRAFT_RECIPE_DIR(new File(TradeCore.getInstance().getDataFolder(), "crafting-recipes")),
    CRAFT_RECIPE_EXPORT_FILE(new File(TradeCore.getInstance().getDataFolder(), "/export/recipes.yml")),
    ;
    
    private final File file;

    DataPaths(File file) {
        this.file = file;
    }
    
    public File get(){
        return file;
    }
}
