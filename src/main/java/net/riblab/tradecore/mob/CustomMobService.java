package net.riblab.tradecore.mob;

import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public interface CustomMobService {
    
    /**
     * 指定した場所にカスタムモブをスポーンさせる
     */
    @ParametersAreNonnullByDefault
    void spawn(Player player, Location spawnlocation, ITCMob type);

    /**
     * もし死んだモブがカスタムモブだったらそのモブの死亡処理を実行させる
     */
    void onCustomMobDeath(@Nullable Mob mob);

    /**
     * 全てのカスタムモブをデスポーンさせる
     */
    void deSpawnAll();
}
