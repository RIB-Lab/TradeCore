/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.impl;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.effect.ArcEffect;
import de.slikey.effectlib.util.DynamicLocation;
import net.kyori.adventure.util.Ticks;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.entity.mob.MobUtils;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Objects;

public class WeaponAttributeWand {
    
    private static final int reach = 11;

    public static boolean attack(Player player, double damage) {
        Vector dir = player.getEyeLocation().getDirection().normalize();
        RayTraceResult result = player.getWorld().rayTraceEntities(player.getEyeLocation(), dir, reach, entity -> entity.getType() != EntityType.PLAYER);
        if(Objects.isNull(result) || !(result.getHitEntity() instanceof Mob mob)){
            return false;
        }
        
        ArcEffect effect = new ArcEffect(TradeCore.getEffectManager());
        effect.setDynamicOrigin(new DynamicLocation(player.getEyeLocation().add(0,0.25,0), player));
        effect.setTargetEntity(mob);
        effect.particles = 50;
        effect.height = 0.5f;
        effect.start();
        new BukkitRunnable() {
            @Override
            public void run() {
                effect.cancel();
            }
        }.runTaskLater(TradeCore.getInstance(), Ticks.TICKS_PER_SECOND / 4);

        MobUtils.tryDealDamageByPlayer(mob, damage, null);
        return true;
    }
}
