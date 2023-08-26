package net.riblab.tradecore.mob;

import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDeathEvent;

public interface ITCMob {
    /**
     * エンティティがスポーンするときの処理
     *
     * @param mob
     */
    void spawn(Mob mob);

    /**
     * カスタムモブが死んだときの処理
     *
     * @param event
     */
    void onKilledByPlayer(EntityDeathEvent event);

    /**
     * 引数のモブがこのカスタムモブであるかどうか
     */
    boolean isSimilar(Mob mob);

    org.bukkit.entity.EntityType getBaseType();

    net.kyori.adventure.text.Component getCustomName();

    double getBaseHealth();

    String getInternalName();

    java.util.Map<org.bukkit.inventory.ItemStack, Float> getDrops();
}
