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
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.util.Vector;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DungeonService {
    
    private static final String tmpDirName = "dungeontemplate";
    private static final String dungeonPrefix = "dungeons";
    private static final String copySchemDir = "schematics";
    private static final File pasteSchemDir = TradeCore.getInstance().getDataFolder();
    private static final List<World> dungeons = new ArrayList<>();
    private static final Map<Player, Location> locationsOnEnter = new HashMap<>();
    
    private static final Vector fallBackSpawnLoc = new Vector(0,100,0);
    
    public void create(String name){
        create(name, -1);
    }

    /**
     * ダンジョンを作る
     * @param name 名前
     * @param instanceID インスタンスのID。0未満なら0以上の最初に空いているインスタンス
     */
    public World create(String name, int instanceID){
        //ダンジョンのインスタンスの競合を確認
        String affixedDungeonName;
        if(instanceID >= 0){
            if(isDungeonExist(name, instanceID))
                return null;
            
            affixedDungeonName = getAffixedDungeonName(name, instanceID);
        }
        else{
            affixedDungeonName = getFirstAvailableAffixedDungeonName(name);
        }
        
        //ワールドをresourceからコピー
        File destDir = new File(affixedDungeonName);
        try {
            Utils.copyFolder(tmpDirName, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File uidFile = new File( affixedDungeonName + "/uid.dat");
        uidFile.delete();
        WorldCreator wc = new WorldCreator(affixedDungeonName, new NamespacedKey(TradeCore.getInstance(), affixedDungeonName));
        wc.generator(new EmptyChunkGenerator());
        World world = Bukkit.getServer().createWorld(wc);

        //ダンジョン名に対応したschemをresourceからコピーする
        String prefixedDungeonName = getPrefixedDungeonName(name);
        File instantiatedSchemFile = new File(pasteSchemDir + "/" + prefixedDungeonName + ".schem");
        boolean fileHasCopied = false;
        try {
            fileHasCopied = Utils.copyFile(copySchemDir + "/" + prefixedDungeonName + ".schem", instantiatedSchemFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if(!fileHasCopied){
            Bukkit.getLogger().severe("schemファイルが見つかりません：" + copySchemDir + "/" + prefixedDungeonName + ".schem");
            dungeons.add(world);
            return world;
        }
        
        //schemから地形生成
        Clipboard clipboard;
        ClipboardFormat format = ClipboardFormats.findByFile(instantiatedSchemFile);
        try (ClipboardReader reader = format.getReader(new FileInputStream(instantiatedSchemFile))) {
            clipboard = reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(fallBackSpawnLoc.getX(), fallBackSpawnLoc.getY(), fallBackSpawnLoc.getZ()))
                    .copyEntities(true)
                    .build();
            Operations.complete(operation);
        }
        
        world.setAutoSave(false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setTime(6000);
        world.setSpawnLocation(new Location(world, fallBackSpawnLoc.getX(),  fallBackSpawnLoc.getY(), fallBackSpawnLoc.getZ()));
        dungeons.add(world);
        
        return world;
    }
    
    public boolean isDungeonExist(String name, int id){
        String dungeonName = getAffixedDungeonName(name, id);
        return dungeons.stream().filter(world -> world.getName().equals(dungeonName)).findFirst().orElse(null) != null;
    }
    
    public void enter(Player player, String name, int id){
        String dungeonName = getAffixedDungeonName(name, id);
        enter(player, Bukkit.getWorld(dungeonName));
    }
    
    public void enter(Player player, World world){
        if(isPlayerInDungeon(player)){
            PaperLib.teleportAsync(player, new Location(world, fallBackSpawnLoc.getX(),  fallBackSpawnLoc.getY(), fallBackSpawnLoc.getZ()));
            return;
        }
        
        if(!dungeons.contains(world)){
            player.sendMessage("その名前またはインスタンスIDのダンジョンは存在しません");
            return;
        }
        
        Location locationOnEnter = player.getLocation().clone();
        locationsOnEnter.put(player, locationOnEnter);

        PaperLib.teleportAsync(player, new Location(world, fallBackSpawnLoc.getX(),  fallBackSpawnLoc.getY(), fallBackSpawnLoc.getZ()));
    }
    
    public void tryLeave(Player player){
        if(!isPlayerInDungeon(player)){
            return;
        }
        
        if(!locationsOnEnter.containsKey(player)){
            player.sendMessage("ダンジョン進入時の座標が見つかりませんでした。");
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
            return;
        }
        
        player.teleport(locationsOnEnter.get(player));
        locationsOnEnter.remove(player);
    }
    
    public void evacuate(Player player){
        if(!locationsOnEnter.containsKey(player)){
            player.sendMessage("復帰できる座標が見つかりませんでした");
            player.teleport(new Location(Bukkit.getWorld("world"), fallBackSpawnLoc.getX(),  fallBackSpawnLoc.getY(), fallBackSpawnLoc.getZ()));
            return;
        }

        player.teleport(locationsOnEnter.get(player));
        locationsOnEnter.remove(player);
    }
    
    public boolean isPlayerInDungeon(Player player){
        return dungeons.stream().filter(world -> player.getWorld().equals(world)).findAny().orElse(null) != null;
    }
    
    public void destroyAll(){
        dungeons.forEach(this::killInsance);
        dungeons.clear();
    }
    
    public void destroySpecific(World world){
        if(!dungeons.contains(world)){
            return;
        }
        
        killInsance(world);
        
        dungeons.remove(world);
    }
    
    public void killInsance(World world){
        world.getPlayers().forEach(this::tryLeave);
        File folder = world.getWorldFolder();
        Bukkit.unloadWorld(world, false);
        try {
            FileUtils.deleteDirectory(folder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void tryProcessDungeonSpawn(PlayerRespawnEvent event){
        if(!dungeons.contains(event.getPlayer().getWorld()))
            return;
        
        event.setRespawnLocation(new Location(event.getPlayer().getLocation().getWorld(), fallBackSpawnLoc.getX(),  fallBackSpawnLoc.getY(), fallBackSpawnLoc.getZ()));
    }
    
    private String getAffixedDungeonName(String name, int id){
        return dungeonPrefix + "_" + name + "_" + id;
    }
    
    private String getPrefixedDungeonName(String name){
        return dungeonPrefix + "_" + name;
    }
    
    private String getFirstAvailableAffixedDungeonName(String name){
        List<String> instances = dungeons.stream().map(World::getName).filter(worldName -> worldName.startsWith(dungeonPrefix + "_" + name + "_")).toList();
        for (int i = 0; i < 1000; i++) {
            String predicate = dungeonPrefix + "_" + name + "_" + i;
            if(!instances.contains(predicate)){
                return predicate;
            }
        }
        throw new StackOverflowError("ダンジョンのインスタンス数が1000を超えました。嘘だろ");
    }
    
    public List<String> getDungeonListInfo(){
        List<String> info = new ArrayList<>();
        dungeons.forEach(world -> {
            boolean hasPlayer = world.getPlayers().size() != 0;
            info.add(world.getName() + ": " + (hasPlayer ? ChatColor.GREEN + "有人" : ChatColor.RED + "無人"));
        });
        return info;
    }
    
    public void killEmptyDungeons(){
        List<World> nobodyDungeons = dungeons.stream().filter(world -> world.getPlayers().size() == 0).collect(Collectors.toList());
        nobodyDungeons.forEach(this::destroySpecific);
    }
    
    public void onDungeonInit(WorldInitEvent event){
        event.getWorld().setKeepSpawnInMemory(false);
    }
}
