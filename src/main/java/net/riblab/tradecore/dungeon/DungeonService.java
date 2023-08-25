package net.riblab.tradecore.dungeon;

import net.riblab.tradecore.TradeCore;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DungeonService {
    
    private static final List<World> dungeons = new ArrayList<>();
    private static final Map<Player, Location> locationsOnEnter = new HashMap<>();
    
    private static final Vector spawnLoc = new Vector(0,100,0);
    
    public World create(String name){
        String dungeonName = getPrefixedDungeonName(name);
        WorldCreator wc = new WorldCreator(dungeonName, new NamespacedKey(TradeCore.getInstance(), name));
        wc.generator(new EmptyChunkGenerator());
        World world = wc.createWorld();
        world.setAutoSave(false);
        world.setKeepSpawnInMemory(false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setTime(6000);
        world.setSpawnLocation(new Location(world, spawnLoc.getX(),  spawnLoc.getY(), spawnLoc.getZ()));
        dungeons.add(world);
        return world;
    }
    
    public boolean isDungeonExist(String name){
        String dungeonName = getPrefixedDungeonName(name);
        return dungeons.stream().filter(world -> world.getName().equals(dungeonName)).findFirst().orElse(null) != null;
    }
    
    public void enter(Player player, String name){
        String dungeonName = getPrefixedDungeonName(name);
        enter(player, Bukkit.getWorld(dungeonName));
    }
    
    public void enter(Player player, World world){
        if(isPlayerInDungeon(player)){
            return;
        }
        
        if(!dungeons.contains(world)){
            player.sendMessage("それはダンジョンではありません!");
            return;
        }
        
        Location locationOnEnter = player.getLocation().clone();
        locationsOnEnter.put(player, locationOnEnter);
        player.teleport(new Location(world, spawnLoc.getX(),  spawnLoc.getY(), spawnLoc.getZ()));
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
            player.sendMessage("ダンジョン進入時の座標が見つかりませんでした");
            player.teleport(new Location(Bukkit.getWorld("world"), spawnLoc.getX(),  spawnLoc.getY(), spawnLoc.getZ()));
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
        
        event.setRespawnLocation(new Location(event.getPlayer().getLocation().getWorld(), spawnLoc.getX(),  spawnLoc.getY(), spawnLoc.getZ()));
    }
    
    private String getPrefixedDungeonName(String name){
        return "dungeons_" + name;
    }
    
    public void killEmptyDungeons(){
        List<World> nobodyDungeons = dungeons.stream().filter(world -> world.getPlayers().size() == 0).collect(Collectors.toList());
        nobodyDungeons.forEach(this::destroySpecific);
    }
}
