/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.base;


import net.kyori.adventure.text.Component;
import net.riblab.tradecore.entity.mob.TCMobs;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.mod.*;
import net.riblab.tradecore.modifier.IToolStatsModifier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * 旧ハードコードアイテムレジストリ TODO:MARKED FOR REMOVAL
 */
public enum TCItems {
    //原始時代
    NEXT_PAGE(new TCItem(Component.text("次のページ"), Material.ARROW, "nextpage", 1, List.of())),
    PREVIOUS_PAGE(new TCItem(Component.text("前のページ"), Material.ARROW, "previouspage", 2, List.of())),
    STONE_SPEAR(new TCItem(Component.text("石の槍"), Material.STONE_SWORD, "stone_spear", 1, List.of(new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE), new ModWeaponAttribute(ModWeaponAttribute.WeaponType.SPEAR), new ModDefaultAttackDamageI(new ModDefaultAttackDamageI.DamageData(3.5, AttackDamageSpread.STONE))))),
    STONE_DAGGER(new TCItem(Component.text("石の短剣"), Material.STONE_SWORD, "stone_dagger", 2, List.of(new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE), new ModWeaponAttribute(ModWeaponAttribute.WeaponType.DAGGER), new ModDefaultAttackDamageI(new ModDefaultAttackDamageI.DamageData(4, AttackDamageSpread.STONE))))),
    STONE_BATTLEAXE(new TCItem(Component.text("石の大斧"), Material.STONE_SWORD, "stone_battleaxe", 3, List.of(new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE), new ModWeaponAttribute(ModWeaponAttribute.WeaponType.BATTLEAXE), new ModDefaultAttackDamageI(new ModDefaultAttackDamageI.DamageData(8, AttackDamageSpread.STONE))))),
    FUEL_BALL(new TCItem(Component.text("燃料玉"), Material.HAY_BLOCK, "fuel_ball", 1, List.of(new ModSellPriceI(0.10d))))
    ;

    private final ITCItem tcItem;

    TCItems(ITCItem tcItem) {
        this.tcItem = tcItem;
    }

    public ITCItem get() {
        return tcItem;
    }

    /**
     * アイテムが固有アイテムであった場合その実体を固有アイテムクラスに変換する<br>
     * この際実体特有のNBTなどは失われる
     *
     * @param itemStack 変換したいアイテム
     * @return 変換された固有アイテム
     */
    public static Optional<ITCItem> toTCItem(ItemStack itemStack) {
        if (Objects.isNull(itemStack) || itemStack.getType() == Material.AIR)
            return Optional.empty();

        Optional<String> id = new ItemCreator(itemStack).getStrNBT(NBTTagNames.ITEMID.get());
        return id.flatMap(s -> Arrays.stream(TCItems.values()).filter(e -> e.get().isSimilar(s)).findFirst().map(TCItems::get));

    }
}
