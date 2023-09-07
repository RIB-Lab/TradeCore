/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.dungeon;

import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.general.ErrorMessages;
import net.riblab.tradecore.general.Utils;
import net.riblab.tradecore.general.WorldNames;
import net.riblab.tradecore.item.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

enum DungeonServiceImpl implements DungeonService {
    INSTANCE;

    DungeonServiceImpl() {
        Utils.forceInit(DungeonProgressionTracker.class);
    }

    /**
     * ダンジョンのインスタンス達
     */
    private final Map<World, DungeonProgressionTracker<?>> dungeons = new HashMap<>();

    /**
     * プレイヤーがダンジョンに入る前いた場所
     */
    private final Map<Player, Location> locationsOnEnter = new HashMap<>();

    @Override
    @ParametersAreNonnullByDefault
    public void create(IDungeonData<?> data) {
        create(data, -1);
    }

    @Override
    @ParametersAreNonnullByDefault
    public Optional<World> create(IDungeonData<?> data, int instanceID) {
        String name = data.getNames().getInternalName();
        //ダンジョンのインスタンスの競合を確認
        String affixedDungeonName;
        if (instanceID >= 0) {
            if (isDungeonExist(name, instanceID))
                return Optional.empty();

            affixedDungeonName = getAffixedDungeonName(name, instanceID);
        } else {
            affixedDungeonName = getFirstAvailableAffixedDungeonName(name);
        }
        String schemName = getPrefixedDungeonName(name);

        World instance = DungeonBuilder.build(data, affixedDungeonName, schemName);
        DungeonProgressionTracker<?> progressionTracker;
        try {
            progressionTracker = data.getProgressionTracker().getDeclaredConstructor(data.getProgressionVariable().getClass(), World.class).newInstance(data.getProgressionVariable(), instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        progressionTracker.onComplete = this::onDungeonComplete;
        progressionTracker.onGameOver = this::destroySpecific;
        dungeons.put(instance, progressionTracker);
        return Optional.of(instance);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isDungeonExist(IDungeonData<?> data, int id) {
        return isDungeonExist(data.getNames().getInternalName(), id);
    }

    private boolean isDungeonExist(String name, int id) {
        String dungeonName = getAffixedDungeonName(name, id);
        return dungeons.keySet().stream().anyMatch(world -> world.getName().equals(dungeonName));
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
        enter(player, getDungeonWorld(data.getNames().getInternalName(), id));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void enter(Player player, @Nullable World world) {
        if (Objects.isNull(world)) {
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
            player.teleport(Bukkit.getWorld(WorldNames.OVERWORLD.get()).getSpawnLocation());
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
        return dungeons.keySet().stream().anyMatch(world -> player.getWorld().equals(world));
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
    @ParametersAreNonnullByDefault
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
        throw new RuntimeException(ErrorMessages.TOO_MANY_DUNGEON_INSTANCES.get());
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
        List<World> nobodyDungeons = dungeons.keySet().stream().filter(world -> world.getPlayers().isEmpty()).toList();
        nobodyDungeons.forEach(this::destroySpecific);
    }

    public Optional<DungeonProgressionTracker<?>> getTracker(World world) {
        return Optional.ofNullable(dungeons.get(world));
    }

    /**
     * ダンジョンが終了した時の処理
     */
    private void onDungeonComplete(World instance) {
        instance.sendMessage(Component.text("ダンジョンクリア!"));

        String unfixedName = getUnfixedDungeonName(instance.getName());
        IDungeonData<?> data = DungeonDatas.nameToDungeonData(unfixedName).orElseThrow(() -> new NullPointerException("ワールド名からダンジョンデータが推測できません！"));
        ItemStack reward = ItemUtils.getRandomItemFromPool(data.getRewardPool());
        if (Objects.nonNull(reward)) {
            for (Player player : instance.getPlayers()) {
                final HashMap<Integer, ItemStack> item = player.getInventory().addItem(reward);
                item.forEach((integer, itemStack) -> instance.dropItemNaturally(player.getLocation(), itemStack));
                player.sendMessage(reward.displayName().append(Component.text(" x" + reward.getAmount() + " を獲得!")));
            }
        }

        instance.sendMessage(Component.text("/tcdungeon leaveでダンジョンを抜けられます..."));
    }
}
