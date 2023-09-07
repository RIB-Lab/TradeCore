/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.entity.mob;

import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * このプラグイン独自のモブをスポーンさせたりデスポーンイベントを発行したりするシステム
 */
enum CustomMobServiceImpl implements CustomMobService {
    INSTANCE;

    /**
     * このプラグインが現在スポーンさせているモブのリスト
     */
    private final List<Mob> spawnedMobs = new ArrayList<>();

    @Override
    @ParametersAreNonnullByDefault
    public void spawn(Player player, Location spawnlocation, ITCMob type) {
        Mob entity = (Mob) spawnlocation.getWorld().spawnEntity(spawnlocation, type.getBaseType());
        entity.setTarget(player);

        type.spawn(entity);

        spawnedMobs.add(entity);
    }

    @Override
    public void onCustomMobDeath(Mob mob) {
        if (!spawnedMobs.contains(mob))
            return;

        spawnedMobs.remove(mob);

        boolean isLootable = MobUtils.isLootable(mob);
        if (!isLootable)
            return;

        TCMobs.toTCMob(mob).ifPresent(itcMob -> itcMob.onKilledByPlayer(mob));
    }

    @Override
    public int deSpawnAll() {
        int size = spawnedMobs.size();
        for (Mob mob : spawnedMobs) {
            MobUtils.setLootableTag(mob, false);
            mob.remove();
        }
        spawnedMobs.clear();
        return size;
    }
}
