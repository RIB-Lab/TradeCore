package net.riblab.tradecore.dungeon;

import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.Utils;
import net.riblab.tradecore.mob.CustomMobService;
import net.riblab.tradecore.mob.TCMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldInitEvent;

import java.util.List;
import java.util.Random;

/**
 * ダンジョンで起こるイベントのハンドラ
 */
public class DungeonEventHandler {
    public static IDungeonService getservice(){
        return TradeCore.getInstance().getIDungeonService();
    }

    public void tryProcessDungeonSpawn(PlayerRespawnEvent event){
        if(!getservice().isPlayerInDungeon(event.getPlayer()))
            return;

        event.setRespawnLocation(event.getPlayer().getWorld().getSpawnLocation());
    }
    
    public void onDungeonSecondPassed(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(!getservice().isPlayerInDungeon(player))
                return;

            String unfixedName = getservice().getUnfixedDungeonName(player.getWorld().getName());
            IDungeonData data = DungeonDatas.nameToDungeonData(unfixedName);
            trySpawnMob(player, data);
        });
    }

    /**
     * ダンジョンにいるプレイヤー周辺のスポナーからダンジョンに応じたモブをスポーンさせる
     */
    private void trySpawnMob(Player player, IDungeonData data){
        List<Block> activatedSpawner = Utils.getBlocksInRadius(player, 8, Material.REDSTONE_BLOCK);
        for (Block block : activatedSpawner) {
            for (int i = 0; i < data.getBasePackSize(); i++) {
                TCMob mobToSpawn = data.getSpawnTable().get(new Random().nextInt(data.getSpawnTable().size()));
                Location randomizedSpawnLocation = Utils.randomizeLocation(block.getLocation().add(0.5d, 0, 0.5d), 1);
                CustomMobService.spawn(player, randomizedSpawnLocation, mobToSpawn);
            }
            block.setType(Material.AIR);
        }
    }

    public void onDungeonInit(WorldInitEvent event){
        event.getWorld().setKeepSpawnInMemory(false);
    }
}
