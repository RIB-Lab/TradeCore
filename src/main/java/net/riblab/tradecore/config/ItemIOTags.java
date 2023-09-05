package net.riblab.tradecore.config;

public enum ItemIOTags {
    NAME("name"),
    MATERIAL("material"),
    CUSTOMMODELDATA("model"),
    DEFAULTMODS("mod");
    
    private final String name;

    ItemIOTags(String name) {
        this.name = name;
    }
    
    public final String get(){
        return name;
    }
}