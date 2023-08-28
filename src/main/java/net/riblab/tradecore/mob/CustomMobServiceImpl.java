package net.riblab.tradecore.mob;

import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * このプラグイン独自のモブをスポーンさせたりデスポーンイベントを発行したりするシステム
 */
class CustomMobServiceImpl implements CustomMobService {

    /**
     * このプラグインが現在スポーンさせているモブのリスト
     */
    public static List<Mob> spawnedMobs = new ArrayList<>();

    @Override
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

        ITCMob ITCMob = TCMobs.toTCMob(mob);
        if (ITCMob == null)
            return;

        ITCMob.onKilledByPlayer(mob);
    }

    @Override
    public void deSpawnAll() {
        spawnedMobs.forEach(mob -> {
            MobUtils.setLootableTag(mob, false);
            mob.remove();
        });
        spawnedMobs.clear();
    }
}
