package net.riblab.tradecore.mob;

import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

public interface CustomMobService {
    
    /**
     * 指定した場所にカスタムモブをスポーンさせる
     */
    void spawn(Player player, Location spawnlocation, ITCMob type);

    /**
     * もし死んだモブがカスタムモブだったらそのモブの死亡処理を実行させる
     *
     * @param event
     */
    void onEntityDeath(EntityDeathEvent event);

    /**
     * 全てのカスタムモブをデスポーンさせる
     */
    void deSpawnAll();
}
