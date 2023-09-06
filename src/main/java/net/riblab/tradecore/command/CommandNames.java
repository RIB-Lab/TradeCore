/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.command;

/**
 * コマンドの名前
 */
enum CommandNames {
    CURRENCY("currency"),
    CURRENCY_SETMONEY("setmoney"),
    CURRENCY_SETTICKET("setticket"),
    GIVE("tcgive"),
    SELL("sell"),
    MOBS("mobs"),
    MOBS_SPAWN("spawn"),
    MOBS_RESET("reset"),
    SHOP("shop"),
    SHOP_ADMIN("admin"),
    SHOP_RESPEC("respec"),
    JOB("job"),
    JOB_SET_JOBLV("setjoblv"),
    JOB_RESET_JOBLV("resetjoblv"),
    JOB_RESET_SKILLLV("resetskilllv"),
    DUNGEON("dungeon"),
    DUNGEON_ENTER("enter"),
    DUNGEON_LEAVE("leave"),
    DUNGEON_EVACUATE("evacuate"),
    DUNGEON_LIST("list"),
    WIKI("wiki"),
    VERSION("tcver"),
    SHOP_DUNGEON("dungeon"),
    PROJECTILE("projectile"),
    PROJECTILE_RESET("reset");

    private final String name;

    CommandNames(String name) {
        this.name = name;
    }

    public String get() {
        return name;
    }
}
