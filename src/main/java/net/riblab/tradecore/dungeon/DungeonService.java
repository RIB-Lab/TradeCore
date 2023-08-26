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
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DungeonService implements IDungeonService {
    private static final List<World> dungeons = new ArrayList<>();
    private static final Map<Player, Location> locationsOnEnter = new HashMap<>();
    
    @Override
    public void create(IDungeonData data){
        create(data, -1);
    }
    
    @Override
    public World create(IDungeonData data, int instanceID){
        String name = data.getName();
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
                    .to(BlockVector3.at(dungeonGenerateLoc.getX(), dungeonGenerateLoc.getY(), dungeonGenerateLoc.getZ()))
                    .copyEntities(true)
                    .build();
            Operations.complete(operation);
        }
        
        world.setAutoSave(false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setTime(6000);
        Vector loc = data.getSpawnPoint();
        world.setSpawnLocation(new Location(world, loc.getX(),  loc.getY(), loc.getZ()));
        dungeons.add(world);
        
        return world;
    }

    @Override
    public boolean isDungeonExist(IDungeonData data, int id){
        return isDungeonExist(data.getName(), id);
    }
    
    private boolean isDungeonExist(String name, int id){
        String dungeonName = getAffixedDungeonName(name, id);
        return dungeons.stream().filter(world -> world.getName().equals(dungeonName)).findFirst().orElse(null) != null;
    }
    
    private World getDungeonWorld(String name, int id){
        return getDungeonWorld(getAffixedDungeonName(name, id));
    }
    
    private World getDungeonWorld(String affixedDungeonName){
        return dungeons.stream().filter(world -> world.getName().equals(affixedDungeonName)).findFirst().orElse(null);
    }
    
    @Override
    public void enter(Player player, IDungeonData data, int id){
        enter(player, getDungeonWorld(data.getName(), id));
    }
    
    @Override
    public void enter(Player player, World world){
        if(world == null){
            player.sendMessage("その名前またはインスタンスIDのダンジョンは存在しません");
            return;
        }
        
        if(isPlayerInDungeon(player)){
            PaperLib.teleportAsync(player, world.getSpawnLocation());
            return;
        }
        
        Location locationOnEnter = player.getLocation().clone();
        locationsOnEnter.put(player, locationOnEnter);

        PaperLib.teleportAsync(player, world.getSpawnLocation());
    }
    
    @Override
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
    
    @Override
    public void evacuate(Player player){
        if(!locationsOnEnter.containsKey(player)){
            player.sendMessage("復帰できる座標が見つかりませんでした");
            player.teleport(new Location(Bukkit.getWorlds().get(0), 0,  0, 0));
            return;
        }

        player.teleport(locationsOnEnter.get(player));
        locationsOnEnter.remove(player);
    }
    
    @Override
    public boolean isPlayerInDungeon(Player player){
        return dungeons.stream().filter(world -> player.getWorld().equals(world)).findAny().orElse(null) != null;
    }
    
    @Override
    public void destroyAll(){
        dungeons.forEach(this::killInstance);
        dungeons.clear();
    }
    
    @Override
    public void destroySpecific(World world){
        if(!dungeons.contains(world)){
            return;
        }
        
        killInstance(world);
        
        dungeons.remove(world);
    }
    
    private void killInstance(World world){
        world.getPlayers().forEach(this::evacuate);
        File folder = world.getWorldFolder();
        Bukkit.unloadWorld(world, false);
        Utils.deleteFolder(folder);
    }
    
    private String getAffixedDungeonName(String name, int id){
        return dungeonPrefix + "_" + name + "_" + id;
    }
    
    private String getPrefixedDungeonName(String name){
        return dungeonPrefix + "_" + name;
    }
    
    @Override
    public String getUnfixedDungeonName(String affixedDungeonName){
        return affixedDungeonName.split("_")[1];
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
    
    @Override
    public List<String> getDungeonListInfo(){
        List<String> info = new ArrayList<>();
        dungeons.forEach(world -> {
            boolean hasPlayer = world.getPlayers().size() != 0;
            info.add(world.getName() + ": " + (hasPlayer ? ChatColor.GREEN + "有人" : ChatColor.RED + "無人"));
        });
        return info;
    }
    
    @Override
    public void killEmptyDungeons(){
        List<World> nobodyDungeons = dungeons.stream().filter(world -> world.getPlayers().size() == 0).collect(Collectors.toList());
        nobodyDungeons.forEach(this::destroySpecific);
    }
}
