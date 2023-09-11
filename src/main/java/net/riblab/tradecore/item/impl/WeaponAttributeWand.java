/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.impl;

import net.riblab.tradecore.entity.mob.MobUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
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

        MobUtils.tryDealDamageByPlayer(mob, damage, null);
        return true;
    }
}
