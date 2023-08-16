package net.riblab.tradecore.mob;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class CustomMobService {
    
    public static List<Mob> spawnedMobs = new ArrayList<>();
    
    public static void spawn(Player player, Location spawnlocation, TCMob type){
        Mob entity = (Mob) spawnlocation.getWorld().spawnEntity(spawnlocation, type.getBaseType());
        entity.setTarget(player);
        
        type.spawn(entity);
        
        spawnedMobs.add(entity);
    }
    
    public static void onEntityDeath(EntityDeathEvent event){
        event.getDrops().clear();//バニラのモブドロップをブロック
        
        if(!(event.getEntity() instanceof Mob) || !spawnedMobs.contains((Mob) event.getEntity()) || event.getEntity().getKiller() == null)
            return;
        
        spawnedMobs.remove((Mob) event.getEntity());
        
        TCMob tcMob = TCMobs.toTCMob((Mob) event.getEntity());
        if(tcMob == null)
            return;
        
        tcMob.onKilledByPlayer(event);
    }
    
    public static void deSpawnAll(){
        spawnedMobs.forEach(Entity::remove);
        spawnedMobs.clear();
    }
}
