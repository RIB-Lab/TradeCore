package net.riblab.tradecore.item.impl;

import lombok.Getter;
import net.riblab.tradecore.entity.projectile.CustomProjectileService;
import net.riblab.tradecore.general.AttackCooldownService;
import net.riblab.tradecore.item.base.IWeaponAttribute;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WeaponAttributeBow implements IWeaponAttribute {


    /**
     * 攻撃速度。逆数がそのままクールダウンになる
     */
    @Getter
    private final double attackSpeed;

    /**
     * 撃つ矢の威力。
     */
    @Getter
    private final double attackDamage;

    /**
     * 矢のスピード
     */
    private final double force = 3;

    public WeaponAttributeBow(double attackSpeed, double attackDamage) {
        this.attackSpeed = attackSpeed;
        this.attackDamage = attackDamage;
    }
    
    private double getAttackCooldown(){
        return 1 / attackSpeed;
    }

    @Override
    public boolean attack(Player player) {
        if(AttackCooldownService.getImpl().getCooldown(player) != 0)
            return false;
        
        Vector direction = player.getLocation().getDirection().multiply(force);
        CustomProjectileService.getImpl().spawn(player, CustomProjectileService.arrow, direction, attackDamage);
        
        AttackCooldownService.getImpl().add(player, getAttackCooldown());
        
        return true;
    }
}
