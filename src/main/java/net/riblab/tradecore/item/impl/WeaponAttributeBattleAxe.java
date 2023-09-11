/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.item.impl;

import net.riblab.tradecore.entity.mob.MobUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

public class WeaponAttributeBattleAxe {

    public static boolean attack(Player player, double damage) {
        boolean isHit = false;
        Collection<Entity> nearbyEntities = player.getLocation().clone().add(player.getEyeLocation().getDirection().clone().multiply(2)).getNearbyEntities(2, 1, 2);//プレイヤーの視線の先2mに大きさ2mの箱を作ってスキャンする
        for (Entity nearbyEntity : nearbyEntities) {
            Vector diff = nearbyEntity.getBoundingBox().getCenter().clone().subtract(player.getEyeLocation().toVector()); //プレイヤーから見た敵の相対座標

            if (!(nearbyEntity instanceof Mob livingEntity))
                continue;

            MobUtils.tryDealDamageByPlayer(livingEntity, damage, diff.normalize().multiply(2));
            isHit = true;
        }

        return isHit;
    }
}
