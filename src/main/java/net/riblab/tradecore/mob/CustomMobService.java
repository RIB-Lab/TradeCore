package net.riblab.tradecore.mob;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * このプラグイン独自のモブをスポーンさせたりデスポーンイベントを発行したりするシステム
 */
public class CustomMobService {

    /**
     * このプラグインが現在スポーンさせているモブのリスト
     */
    public static List<Mob> spawnedMobs = new ArrayList<>();

    /**
     * 指定した場所にカスタムモブをスポーンさせる
     */
    public static void spawn(Player player, Location spawnlocation, TCMob type) {
        Mob entity = (Mob) spawnlocation.getWorld().spawnEntity(spawnlocation, type.getBaseType());
        entity.setTarget(player);

        type.spawn(entity);

        spawnedMobs.add(entity);
    }

    /**
     * もし死んだモブがカスタムモブだったらそのモブの死亡処理を実行させる
     * @param event
     */
    public static void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof Player)//プレイヤーの死亡時ドロップは消さない
            return;
        
        event.getDrops().clear();//バニラのモブドロップをブロック

        if (!(event.getEntity() instanceof Mob) || !spawnedMobs.contains((Mob) event.getEntity()) || event.getEntity().getKiller() == null)
            return;

        spawnedMobs.remove((Mob) event.getEntity());

        TCMob tcMob = TCMobs.toTCMob((Mob) event.getEntity());
        if (tcMob == null)
            return;

        tcMob.onKilledByPlayer(event);
    }

    /**
     * 全てのカスタムモブをデスポーンさせる
     */
    public static void deSpawnAll() {
        spawnedMobs.forEach(Entity::remove);
        spawnedMobs.clear();
    }
}
