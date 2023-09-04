package net.riblab.tradecore.item.impl;

import net.riblab.tradecore.entity.projectile.CustomProjectileService;
import net.riblab.tradecore.general.AttackCooldownService;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WeaponAttributeBow {

    /**
     * 矢のスピード
     */
    private static final double force = 3;
    
    public static boolean attack(Player player, double damage) {
        if(AttackCooldownService.getImpl().getCooldown(player) != 0)
            return false;
        
        Vector direction = player.getLocation().getDirection().multiply(force);
        CustomProjectileService.getImpl().spawn(player, CustomProjectileService.ARROW, direction, damage);
        
        return true;
    }
}
