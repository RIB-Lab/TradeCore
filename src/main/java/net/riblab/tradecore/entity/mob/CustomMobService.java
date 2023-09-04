package net.riblab.tradecore.entity.mob;

import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public sealed interface CustomMobService permits CustomMobServiceImpl {

    static CustomMobService getImpl() {
        return CustomMobServiceImpl.INSTANCE;
    }

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
     *
     * @return
     */
    int deSpawnAll();
}
