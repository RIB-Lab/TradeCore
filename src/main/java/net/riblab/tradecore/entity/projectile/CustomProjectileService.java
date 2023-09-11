/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.entity.projectile;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public sealed interface CustomProjectileService permits CustomProjectileServiceImpl {

    //TODO:種類が増えたらenum
    ITCProjectile ARROW = new TCProjectile(Arrow.class, "tcarrow");

    static CustomProjectileService getImpl() {
        return CustomProjectileServiceImpl.INSTANCE;
    }

    void spawn(Player player, ITCProjectile projectile, Vector vector, double damage);

    void onCustomProjectileHit(Projectile projectile);

    double getCustomProjectileDamage(Projectile projectile);

    int deSpawnAll();
}
