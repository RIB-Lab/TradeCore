/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.entity.mob;

import de.tr7zw.nbtapi.NBTEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.attribute.AttributeInstance;
import me.gamercoder215.mobchip.ai.attribute.EntityAttribute;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.general.ChanceFloat;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.base.TCItemRegistry;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * カスタムモブの基礎クラス
 */
@RequiredArgsConstructor
class TCMob implements ITCMob {

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
    private final Map<String, ChanceFloat> drops;

    /**
     * モブの寿命
     */
    private static final int lifetime = 6000;

    @Override
    @ParametersAreNonnullByDefault
    public void spawn(Mob mob) {
        mob.customName(customName);
        mob.setCustomNameVisible(false);

        NBTEntity nbtEntity = new NBTEntity(mob);
        nbtEntity.getPersistentDataContainer().setString(NBTTagNames.MOBID.get(), internalName);

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
                if (!mob.isDead()) {
                    MobUtils.setLootableTag(mob, false);
                    mob.remove();
                }
            }
        }.runTaskLater(TradeCore.getInstance(), lifetime);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onKilledByPlayer(Mob instance) {
        Random random = new Random();
        drops.forEach((string, aFloat) -> {
            float rand = random.nextFloat();
            if (rand < aFloat.get()) {
                instance.getWorld().dropItemNaturally(instance.getLocation(), TCItemRegistry.INSTANCE.commandToTCItem(string).orElseThrow().getItemStack());
            }
        });
    }

    @Override
    public boolean isSimilar(Mob mob) {
        if (Objects.isNull(mob))
            return false;

        NBTEntity nbtEntity = new NBTEntity(mob);
        String id = nbtEntity.getPersistentDataContainer().getString(NBTTagNames.MOBID.get());

        if (Objects.isNull(id))
            return false;

        return id.equals(internalName);
    }
}
