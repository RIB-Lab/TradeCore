package net.riblab.tradecore.dungeon;

import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

public class DPTExtermination extends DungeonProgressionTracker<Integer> implements IPlayerKillHandler {
    int killedCount = 0;
    
    public DPTExtermination(Integer objective, World instance){
        super(objective, instance);
    }
    
    public void onPlayerKill(Mob mob) {
        killedCount++;
        if(killedCount >= getObjective() && isActive){
            onComplete.accept(getInstance());
            isActive = false;
        }
    }
    
    public void onDungeonSecond(Player player) {
        player.sendActionBar(Component.text("敵を倒せ：" + killedCount + "/" + getObjective()));
    }
}
