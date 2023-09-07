/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.impl;

import net.riblab.tradecore.entity.mob.MobUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class WeaponAttributeDagger {

    private static final double angle = 60;

    public static boolean attack(Player player, double damage) {
        boolean isHit = false;
        List<Entity> nearbyEntities = player.getNearbyEntities(1.5d, 1.5d, 1.5d);
        for (Entity nearbyEntity : nearbyEntities) {
            Vector diff = nearbyEntity.getBoundingBox().getCenter().clone().subtract(player.getEyeLocation().toVector()); //プレイヤーから見た敵の相対座標

            Vector playerDir = player.getEyeLocation().getDirection(); //プレイヤーの視線

            if (!(playerDir.angle(diff) * 180 / Math.PI < angle)) //敵がプレイヤーの視線を軸としたコーン状の範囲内にいるか判定
                continue;

            if (!(nearbyEntity instanceof Mob livingEntity))
                continue;

            MobUtils.tryDealDamageByPlayer(livingEntity, damage, diff.normalize().multiply(0.5f));
            isHit = true;
        }

        return isHit;
    }
}
