package net.riblab.tradecore.mob;

import de.tr7zw.nbtapi.NBTEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.attribute.AttributeInstance;
import me.gamercoder215.mobchip.ai.attribute.EntityAttribute;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.TradeCore;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Random;

/**
 * カスタムモブの基礎クラス
 */
@RequiredArgsConstructor
public class TCMob {

    /**
     * エンティティの種族
     */
    @Getter
    private final EntityType baseType;

    /**
     * エンティティの名前
     */
    @Getter
    private final Component customName;

    /**
     * エンティティの最大(初期)体力
     */
    @Getter
    private final double baseHealth;

    /**
     * エンティティの内部名称
     */
    @Getter
    private final String internalName;

    /**
     * エンティティのドロップ品
     */
    @Getter
    private final Map<ItemStack, Float> drops;

    private static final int lifetime = 6000;

    /**
     * エンティティがスポーンするときの処理
     *
     * @param mob
     */
    public void spawn(Mob mob) {
        mob.customName(customName);
        mob.setCustomNameVisible(true);

        NBTEntity nbtEntity = new NBTEntity(mob);
        nbtEntity.getPersistentDataContainer().setString("TCID", internalName);

        EntityBrain brain = BukkitBrain.getBrain(mob);
        //TODO:AIを持ったモブ
//        EntityAI target = brain.getTargetAI();
//        target.remove();
//        PathfinderLeapAtTarget pathfinder = new PathfinderLeapAtTarget(entity, 0.5f);
//        target.put(pathfinder, 0);
//        PathfinderNearestAttackableTarget<Player> pathfinder2 = new PathfinderNearestAttackableTarget<>(entity, Player.class, 10, true, false);
//        target.put(pathfinder2, 1);
        AttributeInstance attributeInstance = brain.getAttributeInstance(EntityAttribute.GENERIC_MAX_HEALTH);
        attributeInstance.setBaseValue(baseHealth);
        mob.setHealth(baseHealth);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!mob.isDead())
                    mob.remove();
            }
        }.runTaskLater(TradeCore.getInstance(), lifetime);
    }

    /**
     * カスタムモブが死んだときの処理
     * @param event
     */
    public void onKilledByPlayer(EntityDeathEvent event) {
        Random random = new Random();
        drops.forEach((itemStack, aFloat) -> {
            float rand = random.nextFloat();
            if (rand < aFloat) {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack);
            }
        });
    }

    /**
     * 引数のモブがこのカスタムモブであるかどうか
     */
    public boolean isSimilar(Mob mob) {
        if (mob == null)
            return false;

        NBTEntity nbtEntity = new NBTEntity(mob);
        String ID = nbtEntity.getPersistentDataContainer().getString("TCID");

        if (ID == null)
            return false;

        return ID.equals(internalName);
    }
}
