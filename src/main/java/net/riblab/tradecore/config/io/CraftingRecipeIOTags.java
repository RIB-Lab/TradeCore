package net.riblab.tradecore.config.io;

enum CraftingRecipeIOTags {
    CATEGORY("category"),
    FEE("fee"),
    INGREDIENTS("ingredients"),
    RESULT("result"),
    RESULTAMOUNT("resultAmount"),
    ;
    
    private final String name;

    CraftingRecipeIOTags(String name) {
        this.name = name;
    }
    
    public String get(){
        return name;
    }
}
