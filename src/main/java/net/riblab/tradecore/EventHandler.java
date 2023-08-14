package net.riblab.tradecore;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.riblab.tradecore.item.ITCItem;
import net.riblab.tradecore.item.LootTables;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.item.TCTool;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
    
    @org.bukkit.event.EventHandler
    public void OnPlayerInteract(PlayerInteractEvent event){
        //念のため金床をブロック
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ANVIL){
            event.setCancelled(true);
            return;
        }
        
        //カスタム作業台 TODO:システム化
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CRAFTING_TABLE){
            event.setCancelled(true);
            UICraftingTable.open(event.getPlayer(), UICraftingTable.CraftingScreenType.CATEGORY);
        }
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

        if(!BrokenBlocksService.getBrokenBlocks().containsKey(player)) return;
        if(BrokenBlocksService.isPlayerBreakingAnotherBlock(event.getPlayer(), blockPosition)) return;

        double distanceX = blockPosition.getX() - player.getLocation().x();
        double distanceY = blockPosition.getY() - player.getLocation().y();
        double distanceZ = blockPosition.getZ() - player.getLocation().z();

        if (distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ >= 1024.0D) return;
        
        ITCItem itcItem = TCItems.toTCItem(event.getPlayer().getInventory().getItemInMainHand());
        if(!(itcItem instanceof TCTool)){
            BrokenBlocksService.getBrokenBlock(player).incrementDamage(player, 0.1d); //ツールでないアイテムを持っているなら実質素手
            return;
        }
        
        Map<Float, ITCItem> loots = LootTables.get(block.getType(), (TCTool)itcItem);
        if(loots.size() == 0){
            BrokenBlocksService.getBrokenBlock(player).incrementDamage(player, 0.1d); //ツールでアイテムがドロップしないなら実質素手
            return;
        }

        SoundGroup soundGroup = block.getBlockData().getSoundGroup();
        player.playSound(block.getLocation(), soundGroup.getHitSound(), SoundCategory.BLOCKS, 1f, 1f);
        BrokenBlocksService.getBrokenBlock(player).incrementDamage(player, ((TCTool) itcItem).getActualMiningSpeed());
    }

    @org.bukkit.event.EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if(unbreakableMaterial.contains(event.getBlock().getType())){
            event.setCancelled(true);
            return;
        }
        
        ItemStack mainHand = event.getPlayer().getItemInHand();
        if(mainHand.getType() == Material.AIR){
            Map<Float, ITCItem> table = LootTables.get(event.getBlock().getType(), TCTool.ToolType.HAND);
            if(table.size() != 0){
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                TradeCore.dropItemByLootTable(event.getBlock(), table);
                return;
            }
        }

        ITCItem itcItem = TCItems.toTCItem(mainHand);
        if(itcItem instanceof TCTool){
            Map<Float, ITCItem> table = LootTables.get(event.getBlock().getType(), (TCTool) itcItem);
            if(table.size() != 0){
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                TradeCore.dropItemByLootTable(event.getBlock(), table);
                ItemStack newItemStack = ((TCTool) itcItem).reduceDurability(mainHand);
                event.getPlayer().getInventory().setItemInMainHand(newItemStack);
                return;
            }
        }

        //適正ツール以外での採掘は何も落とさない
        event.setDropItems(false);
    }
}
