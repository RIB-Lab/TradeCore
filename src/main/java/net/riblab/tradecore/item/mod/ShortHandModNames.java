package net.riblab.tradecore.item.mod;

import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;

/**
 * modの種類と、その短縮形の名前を記録するenum
 */
public enum ShortHandModNames {
    ADDARMOR(ModAddArmorI.class),
    CUSTOMARMOR(ModCustomArmor.class),
    DEFAULTATTACKDAMAGE(ModDefaultAttackDamageI.class),
    DEFAULTMAXDURABILITY(ModDefaultMaxDurabilityI.class),
    DEFAULTMININGSPEED(ModDefaultMiningSpeedI.class),
    ECOLOGY(ModEcologyI.class),
    DUNGEONMAP(ModDungeonMapI.class),
    MONSTERSPAWN(ModMonsterSpawnI.class),
    PLACEABLE(ModPlaceableI.class),
    RANDOMATTACKDAMAGE(ModRandomAttackDamageI.class),
    RANDOMDURABILITY(ModRandomDurabilityI.class),
    RANDOMMININGSPEED(ModRandomMiningSpeedI.class),
    ADDCRAFTCOST(ModAddCraftCostI.class),
    ADDRESOURCECHANCE(ModAddResouceChanceI.class),
    SELLPRICE(ModSellPriceI.class),
    TOOLSTATS(ModToolStatsI.class),
    ADDWALKSPEED(ModAddWalkSpeedI.class),
    ADDWATERBREATH(ModAddWaterBreathI.class),
    WEAPONATTRIBUTE(ModWeaponAttribute.class),
    ZEROHANDATTACKDAMAGE(ModZeroHandAttackDamageI.class)
    ;
    
    @Getter
    private final Class<? extends IItemMod<?>> modClass;

    ShortHandModNames(Class<? extends IItemMod<?>> modClass) {
        this.modClass = modClass;
    }

    /**
     * クラスからそ短縮形の名前(小文字)を取得する
     */
    public static String getShortHandNameFromClass(Class<? extends IItemMod<?>> clazz){
        ShortHandModNames names = Arrays.stream(ShortHandModNames.values()).filter(shortHandModNames -> shortHandModNames.getModClass().equals(clazz)).findFirst().orElse(null);
        return names != null ? names.name().toLowerCase(Locale.ROOT) : null;
    }

    /**
     * クラスの短縮形の名前(小文字)からクラスを取得する
     */
    public static Class<? extends IItemMod<?>> getClassFromShortHandName(String shortHandName){
        String capitalName = shortHandName.toUpperCase(Locale.ROOT);
        ShortHandModNames names = Arrays.stream(ShortHandModNames.values()).filter(shortHandModNames -> shortHandModNames.name().equals(capitalName)).findFirst().orElse(null);
        return names != null ? names.getModClass() : null;
    }
}
