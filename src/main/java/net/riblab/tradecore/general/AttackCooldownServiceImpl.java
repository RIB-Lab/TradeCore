package net.riblab.tradecore.general;

import net.riblab.tradecore.TradeCore;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

enum AttackCooldownServiceImpl implements AttackCooldownService {
    INSTANCE;
    
    private final Map<Player, Double> cooldownMap = new HashMap<>();
    
    @Override
    public void add(Player player, double duration){
        if(cooldownMap.containsKey(player))
            return;
        
        cooldownMap.put(player, duration);

        //クールダウン更新
        new BukkitRunnable() {
            @Override
            public void run() {
                Double oldDuration = cooldownMap.get(player);
                if(oldDuration == null){
                    cancel();
                    return;
                }
                
                double newDuration  = oldDuration - 0.05d;
                if(newDuration <= 0 ){
                    cooldownMap.remove(player);
                }
                else{
                    cooldownMap.put(player, newDuration);
                }
            }
        }.runTaskTimer(TradeCore.getInstance(), 0, 1);
    }
    
    @Override
    public double getCooldown(Player player){
        return cooldownMap.get(player) != null ? cooldownMap.get(player) : 0;
    }

    @Override
    public void forceRemove(Player player){
        cooldownMap.remove(player);
    }
}
