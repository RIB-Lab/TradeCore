package net.riblab.tradecore.item.weapon;

import lombok.Getter;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.mob.CustomMobService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

public class WeaponAttributeBattleAxe implements IWeaponAttribute{

    /**
     * 攻撃速度。4が既定値なので減らしたい場合はそこから引く。
     */
    @Getter
    private final double attackSpeed = -3.6d;

    /**
     * 攻撃の威力。
     */
    @Getter
    private final double attackDamage;

    public WeaponAttributeBattleAxe(double attackDamage) {
        this.attackDamage = attackDamage;
    }

    @Override
    public boolean attack(Player player){
        boolean isHit = false;
        Collection<Entity> nearbyEntities = player.getLocation().clone().add(player.getEyeLocation().getDirection().clone().multiply(2)).getNearbyEntities(2,1,2);//プレイヤーの視線の先2mに大きさ2mの箱を作ってスキャンする
        for (Entity nearbyEntity : nearbyEntities) {
            Vector diff = nearbyEntity.getBoundingBox().getCenter().clone().subtract(player.getEyeLocation().toVector()); //プレイヤーから見た敵の相対座標

            if(!(nearbyEntity instanceof Mob livingEntity))
                continue;

            livingEntity.damage(attackDamage);
            livingEntity.setVelocity(diff.normalize().multiply(2f));
            TradeCore.getInstance().getCustomMobService().setLootableTag(livingEntity, true);
            isHit = true;
        }

        return isHit;
    }
}
