/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.dungeon;

import net.riblab.tradecore.block.BlockUtils;
import net.riblab.tradecore.entity.mob.CustomMobService;
import net.riblab.tradecore.entity.mob.ITCMob;
import net.riblab.tradecore.general.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldInitEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * ダンジョンで起こるイベントのハンドラ
 */
public final class DungeonEventHandler {
    public static DungeonService getservice() {
        return DungeonService.getImpl();
    }

    @ParametersAreNonnullByDefault
    public void tryProcessDungeonSpawn(PlayerRespawnEvent event) {
        DungeonProgressionTracker<?> tracker = getservice().getTracker(event.getPlayer().getWorld());
        if (Objects.isNull(tracker))
            return;

        tracker.onPlayerRespawn(event);
    }

    public void onDungeonSecondPassed() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!getservice().isPlayerInDungeon(player))
                return;

            String unfixedName = getservice().getUnfixedDungeonName(player.getWorld().getName());
            IDungeonData<?> data = DungeonDatas.nameToDungeonData(unfixedName);
            Objects.requireNonNull(data, "ダンジョン名からダンジョンデータを推測できません！");

            trySpawnMob(player, data);

            DungeonProgressionTracker<?> tracker = getservice().getTracker(player.getWorld());
            Objects.requireNonNull(tracker, "ダンジョンにトラッカーが紐づいていません！");

            tracker.onDungeonSecond(player);
        });
    }

    /**
     * ダンジョンにいるプレイヤー周辺のスポナーからダンジョンに応じたモブをスポーンさせる
     */
    @ParametersAreNonnullByDefault
    private void trySpawnMob(Player player, IDungeonData<?> data) {
        List<Block> activatedSpawner = BlockUtils.getBlocksInRadius(player, 8, Material.REDSTONE_BLOCK);
        for (Block block : activatedSpawner) {
            for (int i = 0; i < data.getBasePackSize(); i++) {
                ITCMob mobToSpawn = data.getSpawnTable().get(new Random().nextInt(data.getSpawnTable().size()));
                Location randomizedSpawnLocation = Utils.randomizeLocationXZ(block.getLocation().add(0.5d, 0, 0.5d), 1);
                CustomMobService.getImpl().spawn(player, randomizedSpawnLocation, mobToSpawn);
            }
            block.setType(Material.AIR);
        }
    }

    @ParametersAreNonnullByDefault
    public void onDungeonInit(WorldInitEvent event) {
        event.getWorld().setKeepSpawnInMemory(false);
    }

    @ParametersAreNonnullByDefault
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player)
            return;

        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }

        DungeonProgressionTracker<?> tracker = DungeonService.getImpl().getTracker(mob.getWorld());
        if (Objects.isNull(tracker))
            return;

        if (tracker instanceof IPlayerKillHandler handler) {
            handler.onPlayerKill(mob);
        }
    }

    @ParametersAreNonnullByDefault
    public void preventDungeonBlockPlacement(BlockPlaceEvent event) {
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }
}
