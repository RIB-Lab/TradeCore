package net.riblab.tradecore.entity.projectile;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public interface CustomProjectileService {

    //TODO:種類が増えたらenum
    ITCProjectile arrow = new TCProjectile(Arrow.class, "tcarrow");
    
    static CustomProjectileService getImpl(){
        return CustomProjectileServiceImpl.INSTANCE;
    }
    
    void spawn(Player player, ITCProjectile projectile, Vector vector, double damage);

    void onCustomProjectileHit(Projectile projectile);

    double getCustomProjectileDamage(Projectile projectile);

    int deSpawnAll();
}
