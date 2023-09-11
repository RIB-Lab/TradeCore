/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.config.io;

public enum LootTableIOTags {
    MATERIAL_SET("materialset"),
    TOOL_TYPE("tooltype"),
    HARVEST_LEVEL("harvestlevel"),
    RESULT("result"),
    DROPCHANCE_LEVEL("dropchancelevel"),
    ;

    private final String name;

    LootTableIOTags(String name) {
        this.name = name;
    }

    public String get() {
        return name;
    }
}
