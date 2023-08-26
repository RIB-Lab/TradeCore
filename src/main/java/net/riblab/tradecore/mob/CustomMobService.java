package net.riblab.tradecore.mob;

import de.tr7zw.nbtapi.NBTEntity;
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
    private static final String lootableTag = "lootable";

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

        if (!(event.getEntity() instanceof Mob mob) || !spawnedMobs.contains(mob))
            return;

        NBTEntity nbtEntity = new NBTEntity(mob);
        boolean isLootable =  nbtEntity.getPersistentDataContainer().getBoolean(lootableTag);
        if(!isLootable)
            return;

        spawnedMobs.remove(mob);

        TCMob tcMob = TCMobs.toTCMob(mob);
        if (tcMob == null)
            return;

        tcMob.onKilledByPlayer(event);
    }

    /**
     * 全てのカスタムモブをデスポーンさせる
     */
    public static void deSpawnAll() {
        spawnedMobs.forEach(mob -> {
            setLootableTag(mob, false);
            mob.remove();
        });
        spawnedMobs.clear();
    }
    
    public static void setLootableTag(Mob mob, boolean flag){
        NBTEntity nbtEntity = new NBTEntity(mob);
        nbtEntity.getPersistentDataContainer().setBoolean(lootableTag, flag);
    }
}
