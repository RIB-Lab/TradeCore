package net.riblab.tradecore.mob;

import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * このプラグイン独自のモブをスポーンさせたりデスポーンイベントを発行したりするシステム
 */
public class CustomMobServiceImpl implements CustomMobService {

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
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof Player)//プレイヤーの死亡時ドロップは消さない
            return;
        
        event.getDrops().clear();//バニラのモブドロップをブロック

        if (!(event.getEntity() instanceof Mob mob) || !spawnedMobs.contains(mob))
            return;

       
        boolean isLootable = MobUtils.isLootable(mob);
        if(!isLootable)
            return;

        spawnedMobs.remove(mob);

        ITCMob ITCMob = TCMobs.toTCMob(mob);
        if (ITCMob == null)
            return;

        ITCMob.onKilledByPlayer(event);
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
