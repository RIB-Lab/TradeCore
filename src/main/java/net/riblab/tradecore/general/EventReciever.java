package net.riblab.tradecore.general;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.block.BlockStateEventHandler;
import net.riblab.tradecore.dungeon.DungeonEventHandler;
import net.riblab.tradecore.dungeon.DungeonService;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldInitEvent;

/**
 * 各イベントハンドラに順番にイベントを処理させるクラス
 */
public enum EventReciever implements Listener {
    INSTANCE;

    private final GeneralEventHandler generalEventHandler;
    private final BlockStateEventHandler blockStateEventHandler;
    private final DungeonEventHandler dungeonEventHandler;

    EventReciever() {
        Bukkit.getServer().getPluginManager().registerEvents(this, TradeCore.getInstance());
        blockStateEventHandler = new BlockStateEventHandler();
        generalEventHandler = new GeneralEventHandler();
        dungeonEventHandler = new DungeonEventHandler();
    }

    @org.bukkit.event.EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event) {
        generalEventHandler.processPlayerJoin(event);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        generalEventHandler.processPlayerQuit(event);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        generalEventHandler.processPlayerInteract(event);
    }

    @org.bukkit.event.EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        dungeonEventHandler.onEntityDeath(event);
        generalEventHandler.processEntityDeath(event);
    }

    @org.bukkit.event.EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        generalEventHandler.processEntityDamageByEntity(event);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerReSpawn(PlayerRespawnEvent event) {
        generalEventHandler.processPlayerRespawn(event);
        dungeonEventHandler.tryProcessDungeonSpawn(event);
    }

    @org.bukkit.event.EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        generalEventHandler.processEntityDamage(event);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        generalEventHandler.processPlayerConsumeItem(event);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerChangeArmor(PlayerArmorChangeEvent event) {
        generalEventHandler.processPlayerArmorChange(event);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        generalEventHandler.processPlayerItemHeld(event);
    }

    @org.bukkit.event.EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        generalEventHandler.processInventoryClick(event);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerSwapItem(PlayerSwapHandItemsEvent event) {
        generalEventHandler.processPlayerSwapHandItems(event);
    }

    @org.bukkit.event.EventHandler
    public void onWorldInit(WorldInitEvent event) {
        dungeonEventHandler.onDungeonInit(event);
    }

    public void onSecondPassed() {
        generalEventHandler.processSecondPassed();
        dungeonEventHandler.onDungeonSecondPassed();
    }

    @org.bukkit.event.EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        if(DungeonService.getImpl().isPlayerInDungeon(event.getPlayer())){
            return;
        }
        
        blockStateEventHandler.tryCreateBrokenBlock(event);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        blockStateEventHandler.tryIncrementBlockDamage(event);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        blockStateEventHandler.tryHarvestBlockWithCustomTool(event);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        if(DungeonService.getImpl().isPlayerInDungeon(event.getPlayer())){
            dungeonEventHandler.preventDungeonBlockPlacement(event);
            return;
        }
        
        blockStateEventHandler.tryProcessHoeDrop(event);
    }

    @org.bukkit.event.EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        blockStateEventHandler.preventVanillaStickFromDropping(event);
    }
}
