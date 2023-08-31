package net.riblab.tradecore.entity.projectile;

import org.bukkit.entity.*;
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

    @Override
    public void deSpawnAll() {
        spawnedProjectiles.forEach(Entity::remove);
        spawnedProjectiles.clear();
    }
}
