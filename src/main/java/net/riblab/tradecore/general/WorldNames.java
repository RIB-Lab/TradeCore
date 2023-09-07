/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.general;

/**
 * ワールドの名前レジストリ
 */
public enum WorldNames {
    OVERWORLD("world"),
    NETHER("nether"),
    END("end"),
    RESOURCE_OVERWORLD("sigen-over");

    private final String name;

    WorldNames(String name) {
        this.name = name;
    }

    public String get() {
        return name;
    }
}
