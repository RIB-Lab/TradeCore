package net.riblab.tradecore.dungeon;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.general.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

class DungeonServiceImpl implements DungeonService {

    /**
     * ダンジョンのインスタンス達
     */
    private static final Map<World, DungeonProgressionTracker<?>> dungeons = new HashMap<>();

    /**
     * プレイヤーがダンジョンに入る前いた場所
     */
    private static final Map<Player, Location> locationsOnEnter = new HashMap<>();

    @Override
    @ParametersAreNonnullByDefault
    public void create(IDungeonData<?> data) {
        create(data, -1);
    }

    @Override
    public void create(IDungeonData<?> data, int instanceID) {
        String name = data.getName();
        //ダンジョンのインスタンスの競合を確認
        String affixedDungeonName;
        if (instanceID >= 0) {
            if (isDungeonExist(name, instanceID))
                return;

            affixedDungeonName = getAffixedDungeonName(name, instanceID);
        } else {
            affixedDungeonName = getFirstAvailableAffixedDungeonName(name);
        }
        String schemName = getPrefixedDungeonName(name);
        
        World instance = DungeonBuilder.build(data, affixedDungeonName, schemName);
        DungeonProgressionTracker<?> progressionTracker;
        try {
            progressionTracker = data.getProgressionTracker().getDeclaredConstructor(data.getProgressionVariable().getClass(), World.class).newInstance(data.getProgressionVariable(), instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        progressionTracker.onComplete = this::onDungeonComplete;
        dungeons.put(instance, progressionTracker);
    }

    @Override
    public boolean isDungeonExist(IDungeonData<?> data, int id) {
        return isDungeonExist(data.getName(), id);
    }

    private boolean isDungeonExist(String name, int id) {
        String dungeonName = getAffixedDungeonName(name, id);
        return dungeons.keySet().stream().filter(world -> world.getName().equals(dungeonName)).findFirst().orElse(null) != null;
    }

    /**
     * ダンジョンの名前からインスタンスのワールドを取得
     */
    private World getDungeonWorld(String name, int id) {
        return getDungeonWorld(getAffixedDungeonName(name, id));
    }

    private World getDungeonWorld(String affixedDungeonName) {
        return dungeons.keySet().stream().filter(world -> world.getName().equals(affixedDungeonName)).findFirst().orElse(null);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void enter(Player player, IDungeonData<?> data, int id) {
        enter(player, getDungeonWorld(data.getName(), id));
    }

    private void enter(Player player, World world) {
        if (world == null) {
            player.sendMessage("その名前またはインスタンスIDのダンジョンは存在しません");
            return;
        }

        if (isPlayerInDungeon(player)) {
            PaperLib.teleportAsync(player, world.getSpawnLocation());
            return;
        }

        Location locationOnEnter = player.getLocation().clone();
        locationsOnEnter.put(player, locationOnEnter);

        PaperLib.teleportAsync(player, world.getSpawnLocation());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void tryLeave(Player player) {
        if (!isPlayerInDungeon(player)) {
            return;
        }

        if (!locationsOnEnter.containsKey(player)) {
            player.sendMessage("ダンジョン進入時の座標が見つかりませんでした。");
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
            return;
        }

        player.teleport(locationsOnEnter.get(player));
        locationsOnEnter.remove(player);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void evacuate(Player player) {
        if (!locationsOnEnter.containsKey(player)) {
            player.sendMessage("復帰できる座標が見つかりませんでした");
            player.teleport(new Location(Bukkit.getWorlds().get(0), 0, 0, 0));
            return;
        }

        player.teleport(locationsOnEnter.get(player));
        locationsOnEnter.remove(player);
    }

    @Override
    public boolean isPlayerInDungeon(Player player) {
        return dungeons.keySet().stream().filter(world -> player.getWorld().equals(world)).findAny().orElse(null) != null;
    }

    @Override
    public void destroyAll() {
        dungeons.forEach((world, dungeonProgressionTracker) -> killInstance(world));
        dungeons.clear();
    }

    @Override
    public void destroySpecific(World world) {
        if (!dungeons.containsKey(world)) {
            return;
        }

        killInstance(world);

        dungeons.remove(world);
    }

    /**
     * ダンジョンのインスタンスを削除する
     *
     * @param world
     */
    private void killInstance(World world) {
        world.getPlayers().forEach(this::evacuate);
        File folder = world.getWorldFolder();
        Bukkit.unloadWorld(world, false);
        Utils.deleteFolder(folder);
    }

    /**
     * 接辞がついたダンジョン名を取得
     */
    private String getAffixedDungeonName(String name, int id) {
        return dungeonPrefix + "_" + name + "_" + id;
    }

    /**
     * 接頭辞だけついたダンジョン名を取得
     */
    private String getPrefixedDungeonName(String name) {
        return dungeonPrefix + "_" + name;
    }

    @Override
    public @NotNull String getUnfixedDungeonName(String affixedDungeonName) {
        return affixedDungeonName.split("_")[1];
    }

    /**
     * 利用可能な最初の空いているダンジョン名を取得する
     */
    private String getFirstAvailableAffixedDungeonName(String name) {
        List<String> instances = dungeons.keySet().stream().map(World::getName).filter(worldName -> worldName.startsWith(dungeonPrefix + "_" + name + "_")).toList();
        for (int i = 0; i < 1000; i++) {
            String predicate = getAffixedDungeonName(name, i);
            if (!instances.contains(predicate)) {
                return predicate;
            }
        }
        throw new RuntimeException("ダンジョンのインスタンス数が1000を超えました。嘘だろ");
    }

    @Override
    public @NotNull List<String> getDungeonListInfo() {
        List<String> info = new ArrayList<>();
        dungeons.forEach((world, dungeonProgressionTracker) -> {
            boolean hasPlayer = world.getPlayers().size() != 0;
            info.add(world.getName() + ": " + (hasPlayer ? ChatColor.GREEN + "有人" : ChatColor.RED + "無人"));
        });
        return info;
    }

    @Override
    public void killEmptyDungeons() {
        List<World> nobodyDungeons = dungeons.keySet().stream().filter(world -> world.getPlayers().size() == 0).toList();
        nobodyDungeons.forEach(this::destroySpecific);
    }
    
    public DungeonProgressionTracker<?> getTracker(World world){
        return dungeons.get(world);
    }

    /**
     * ダンジョンが終了した時の処理
     */
    private void onDungeonComplete(World instance){
        instance.sendMessage(Component.text("ダンジョンクリア(仮)"));
    }
}
