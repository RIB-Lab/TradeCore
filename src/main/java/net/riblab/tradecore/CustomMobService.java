package net.riblab.tradecore;

import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.EntityAI;
import me.gamercoder215.mobchip.ai.attribute.AttributeInstance;
import me.gamercoder215.mobchip.ai.attribute.EntityAttribute;
import me.gamercoder215.mobchip.ai.goal.PathfinderLeapAtTarget;
import me.gamercoder215.mobchip.ai.goal.PathfinderMoveTowardsTarget;
import me.gamercoder215.mobchip.ai.goal.PathfinderRangedAttack;
import me.gamercoder215.mobchip.ai.goal.PathfinderRangedBowAttack;
import me.gamercoder215.mobchip.ai.goal.target.PathfinderNearestAttackableTarget;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import net.riblab.tradecore.item.TCItems;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class CustomMobService {
    
    public static List<Mob> spawnedMobs = new ArrayList<>();
    
    //TODO:複数のカスタムエンティティを定義できるデータベース
    public static void spawn(Player player, Location spawnlocation){
        Mob entity = (Mob) spawnlocation.getWorld().spawnEntity(spawnlocation, EntityType.SILVERFISH);
        entity.setTarget(player);
        
        EntityBrain brain = BukkitBrain.getBrain(entity);
        EntityAI target = brain.getTargetAI();
        target.clear();
        PathfinderLeapAtTarget pathfinder = new PathfinderLeapAtTarget(entity, 0.5f);
        target.put(pathfinder, 0);
        PathfinderNearestAttackableTarget<Player> pathfinder2 = new PathfinderNearestAttackableTarget<>(entity, Player.class, 10, true, false);
        target.put(pathfinder2, 1);
        
        AttributeInstance attributeInstance = brain.getAttributeInstance(EntityAttribute.GENERIC_MAX_HEALTH);
        attributeInstance.setBaseValue(12);
        entity.setHealth(12);
        
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!entity.isDead())
                    entity.remove();
            }
        }.runTaskLater(TradeCore.getInstance(), 6000); //5分後に自然デスポーン
        
        spawnedMobs.add(entity);
    }
    
    public static void onEntityDeath(EntityDeathEvent event){
        if(!(event.getEntity() instanceof Mob) || !spawnedMobs.contains((Mob) event.getEntity()))
            return;
        
        spawnedMobs.remove((Mob) event.getEntity());
        event.getDrops().clear();
        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), TCItems.STICK.get().getItemStack());
    }
}
