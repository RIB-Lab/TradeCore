package net.riblab.tradecore.mob;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

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

    EntityType getBaseType();

    Component getCustomName();

    double getBaseHealth();

    String getInternalName();

    java.util.Map<ItemStack, Float> getDrops();
}
