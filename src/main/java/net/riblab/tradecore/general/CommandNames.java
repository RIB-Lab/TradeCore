package net.riblab.tradecore.general;

/**
 * コマンドの名前
 */
public enum CommandNames {
    CURRENCY("tccurrency"),
    CURRENCY_SETMONEY("setmoney"),
    CURRENCY_SETTICKET("setticket"),
    GIVE("tcgive"),
    SELL("tcsell"),
    MOBS("tcmobs"),
    MOBS_SPAWN("spawn"),
    MOBS_RESET("reset"),
    SHOP("tcshop"),
    SHOP_ADMIN("admin"),
    SHOP_RESPEC("respec"),
    JOB("tcjob"),
    JOB_SETJOBLV("setjoblv"),
    JOB_RESETJOBLV("resetjoblv"),
    JOB_RESETSKILLLV("resetskilllv"),
    DUNGEON("tcdungeon"),
    DUNGEON_ENTER("enter"),
    DUNGEON_LEAVE("leave"),
    DUNGEON_EVACUATE("evacuate"),
    DUNGEON_LIST("list");
    
    private final String name;

    CommandNames(String name) {
        this.name = name;
    }
    
    public String get(){
        return name;
    }
}
