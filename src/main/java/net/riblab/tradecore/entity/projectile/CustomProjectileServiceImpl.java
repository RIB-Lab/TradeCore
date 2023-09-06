/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.entity.projectile;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

enum CustomProjectileServiceImpl implements CustomProjectileService {
    INSTANCE;

    private final List<Projectile> spawnedProjectiles = new ArrayList<>();

    @Override
    public void spawn(Player player, ITCProjectile type, Vector vector, double damage) {
        Projectile projectile = player.launchProjectile(type.getBaseClass(), vector);
        type.onSpawn(projectile, damage);

        spawnedProjectiles.add(projectile);
    }

    @Override
    public void onCustomProjectileHit(Projectile projectile) {
        if(!spawnedProjectiles.contains(projectile)){
            return;
        }
        
        spawnedProjectiles.remove(projectile);
        
        projectile.remove();
    }

    @Override //TODO:これもprojectileの種類が増えたらEnumに移行
    public double getCustomProjectileDamage(Projectile projectile) {
        //既にHitイベントでremoveされている可能性を考慮してcontainsチェックはしない
        return CustomProjectileService.ARROW.getDamage(projectile);
    }

    @Override
    public int deSpawnAll() {
        int size = spawnedProjectiles.size();
        spawnedProjectiles.forEach(Entity::remove);
        spawnedProjectiles.clear();
        return size;
    }
}
