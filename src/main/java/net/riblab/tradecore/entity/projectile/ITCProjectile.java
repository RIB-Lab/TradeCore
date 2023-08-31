package net.riblab.tradecore.entity.projectile;

import org.bukkit.entity.Projectile;

public interface ITCProjectile {
    void onSpawn(Projectile projectile, double damage);

    boolean isSimilar(Projectile projectile);

    Class<? extends Projectile> getBaseClass();

    String getInternalName();
    
    public double getDamage(Projectile projectile);
}
