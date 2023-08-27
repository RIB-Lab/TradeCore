package net.riblab.tradecore.item.weapon;

import lombok.Getter;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.mob.MobUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class WeaponAttributeSword implements IWeaponAttribute {

    /**
     * 攻撃速度。4が既定値なので減らしたい場合はそこから引く。
     */
    @Getter
    private final double attackSpeed = -2.7d;

    /**
     * 攻撃の威力。
     */
    @Getter
    private final double attackDamage;
    
    private final double reach = 3;
    
    private final double angle = 90;

    public WeaponAttributeSword(double attackDamage) {
        this.attackDamage = attackDamage;
    }

    @Override
    public boolean attack(Player player){
        boolean isHit = false;
        List<Entity> nearbyEntities = player.getNearbyEntities(5,5,5);
        for (Entity nearbyEntity : nearbyEntities) {
            Vector diff = nearbyEntity.getBoundingBox().getCenter().clone().subtract(player.getEyeLocation().toVector()); //プレイヤーから見た敵の相対座標
            if(diff.length() > reach) //リーチ内にいるか判定
                continue;
            
            Vector playerDir = player.getEyeLocation().getDirection(); //プレイヤーの視線
            
            if(!(playerDir.angle(diff) * 180 / Math.PI < angle)) //敵がプレイヤーの視線を軸としたコーン状の範囲内にいるか判定
                continue;
            
            if(!(nearbyEntity instanceof Mob livingEntity))
                continue;
            
            livingEntity.damage(attackDamage);
            livingEntity.setVelocity(diff.normalize());
            MobUtils.setLootableTag(livingEntity, true);
            isHit = true;
        }
        
        return isHit;
    }
}
