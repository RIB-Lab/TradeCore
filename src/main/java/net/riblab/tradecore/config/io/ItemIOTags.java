/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.config.io;

public enum ItemIOTags {
    NAME("name"),
    MATERIAL("material"),
    CUSTOMMODELDATA("model"),
    DEFAULTMODS("mods");
    
    private final String name;

    ItemIOTags(String name) {
        this.name = name;
    }
    
    public final String get(){
        return name;
    }
}
