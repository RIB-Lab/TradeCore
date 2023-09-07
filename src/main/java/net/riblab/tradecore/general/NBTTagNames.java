/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.general;

/**
 * このプラグインで使われているNBTタグの名前を集約したもの
 * 安易にいじるとデータが全部飛ぶ
 */
public enum NBTTagNames {
    ITEMID("TCID"),
    MOBID("TCMobID"),
    MOB_ISLOOTABLE("Lootable"),
    ARMOR_TRIM("Trim"),
    ARMOR_MATERIAL("material"),
    ARMOR_PATTERN("pattern"),
    ARMOR_HIDEFLAGS("HideFlags"),
    PROJECTILEID("TCProjectileID"),
    PROJECTILEDAMAGE("ProjectileDamage"),
    DURABILITY("durability"),
    ITEMMOD("ItemMod");

    private final String name;

    NBTTagNames(String name) {
        this.name = name;
    }

    public String get() {
        return name;
    }
}
