/*
 * Copyright (c) 2023. RIBLaB
 */
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
    CRAFT_RECIPE_EXPORT_FILE(new File(TradeCore.getInstance().getDataFolder(), "/export/crafting-recipes.yml")),
    MATERIAL_SET_DIR(new File(TradeCore.getInstance().getDataFolder(), "material-sets")),
    MATERIAL_SET_EXPORT_FILE(new File(TradeCore.getInstance().getDataFolder(), "/export/material-sets.yml")),
    
    LOOT_TABLE_DIR(new File(TradeCore.getInstance().getDataFolder(), "loot-tables")),
    LOOT_TABLE_EXPORT_FILE(new File(TradeCore.getInstance().getDataFolder(), "/export/loot-tables.yml")),
    ;

    private final File file;

    DataPaths(File file) {
        this.file = file;
    }

    public File get() {
        return file;
    }
}
