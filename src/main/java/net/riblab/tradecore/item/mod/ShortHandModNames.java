package net.riblab.tradecore.item.mod;

import lombok.Getter;

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
    RESOURCECHANCE(ModAddResouceChanceI.class),
    SELLPRICE(ModSellPriceI.class),
    TOOLSTATS(ModToolStatsI.class),
    ADDWALKSPEED(ModAddWalkSpeedI.class),
    ADDWATERBREATH(ModAddWaterBreathI.class),
    WEAPONATTRIBUTE(ModWeaponAttribute.class),
    ZeroHandAttackDamage(ModZeroHandAttackDamageI.class)
    ;
    
    @Getter
    private final Class<? extends ItemMod> modClass;

    ShortHandModNames(Class<? extends ItemMod> modClass) {
        this.modClass = modClass;
    }
}
