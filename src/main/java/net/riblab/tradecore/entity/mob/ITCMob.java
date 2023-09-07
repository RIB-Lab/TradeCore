/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.entity.mob;

import net.kyori.adventure.text.Component;
import net.riblab.tradecore.item.base.ITCItem;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public interface ITCMob {
    /**
     * エンティティがスポーンするときの処理
     */
    @ParametersAreNonnullByDefault
    void spawn(Mob mob);

    /**
     * カスタムモブが死んだときの処理
     */
    @ParametersAreNonnullByDefault
    void onKilledByPlayer(Mob instance);

    /**
     * 引数のモブがこのカスタムモブであるかどうか
     */
    boolean isSimilar(@Nullable Mob mob);

    EntityType getBaseType();

    Component getCustomName();

    double getBaseHealth();

    String getInternalName();

    java.util.Map<String, Float> getDrops();
}
