package net.riblab.tradecore;

import net.riblab.tradecore.item.ITCItem;
import net.riblab.tradecore.item.LootTables;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.item.TCTool;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.riblab.tradecore.Materials.unbreakableMaterial;

public class EventHandler implements Listener {
    
    public EventHandler(){
        Bukkit.getServer().getPluginManager().registerEvents(this, TradeCore.getInstance());
    }


    @org.bukkit.event.EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event){
        if(!TradeCore.getInstance().getEconomy().hasAccount(event.getPlayer()))
            TradeCore.getInstance().getEconomy().createPlayerAccount(event.getPlayer());

        TradeCore.addSlowDig(event.getPlayer());
    }

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        TradeCore.removeSlowDig(event.getPlayer());
    }

    //念のため金床をブロック
    @org.bukkit.event.EventHandler
    public void OnPlayerInteract(PlayerInteractEvent event){
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ANVIL)
            event.setCancelled(true);
    }


    @org.bukkit.event.EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        if(unbreakableMaterial.contains(event.getBlock().getType()))
            return;

        BrokenBlocksService.createBrokenBlock(event.getBlock(), event.getPlayer());
    }

    @org.bukkit.event.EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        Set<Material> transparentBlocks = new HashSet<>();
        transparentBlocks.add(Material.WATER);
        transparentBlocks.add(Material.LAVA);
        transparentBlocks.add(Material.AIR);
        Block block = player.getTargetBlock(transparentBlocks, 5);
        Location blockPosition = block.getLocation();

        if (BrokenBlocksService.isPlayerBreakingAnotherBlock(event.getPlayer(), blockPosition)) return;

        double distanceX = blockPosition.getX() - player.getLocation().x();
        double distanceY = blockPosition.getY() - player.getLocation().y();
        double distanceZ = blockPosition.getZ() - player.getLocation().z();

        if (distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ >= 1024.0D) return;
        
        ITCItem itcItem = TCItems.toTCItem(event.getPlayer().getItemInHand());
        if(!(itcItem instanceof TCTool)){
            BrokenBlocksService.getBrokenBlock(player).incrementDamage(player, 0.1d); //ツールでないアイテムを持っているなら実質素手
            return;
        }
        
        Map<Float, ITCItem> loots = LootTables.get(block.getType(), (TCTool)itcItem);
        if(loots.size() == 0){
            BrokenBlocksService.getBrokenBlock(player).incrementDamage(player, 0.1d); //ツールでアイテムがドロップしないなら実質素手
            return;
        }
        
        BrokenBlocksService.getBrokenBlock(player).incrementDamage(player, ((TCTool) itcItem).getActualMiningSpeed());
    }

    @org.bukkit.event.EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if(unbreakableMaterial.contains(event.getBlock().getType())){
            event.setCancelled(true);
            return;
        }

        if(event.getPlayer().getItemInHand().getType() == Material.AIR){
            Map<Float, ITCItem> table = LootTables.get(event.getBlock().getType(), TCTool.ToolType.HAND);
            if(table.size() != 0){
                TradeCore.dropItemByLootTable(event, table);
                return;
            }
        }

        ITCItem itcItem = TCItems.toTCItem(event.getPlayer().getItemInHand());
        if(itcItem instanceof TCTool){
            Map<Float, ITCItem> table = LootTables.get(event.getBlock().getType(), (TCTool) itcItem);
            if(table.size() != 0){
                TradeCore.dropItemByLootTable(event, table);
                return;
            }
        }

        //適正ツール以外での採掘は何も落とさない
        event.setDropItems(false);
    }
}
