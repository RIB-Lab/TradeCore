package net.riblab.tradecore.item.impl;

import lombok.Getter;
import net.riblab.tradecore.item.base.IWeaponAttribute;
import org.bukkit.entity.Arrow;
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

    @Override
    public boolean attack(Player player) {
        Arrow arrow = player.launchProjectile(Arrow.class);
        Vector direction = player.getLocation().getDirection();
        arrow.setVelocity(direction.normalize().multiply(force));
        
        return true;
    }
}
