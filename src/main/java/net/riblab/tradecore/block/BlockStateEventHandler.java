package net.riblab.tradecore.block;

import com.google.common.collect.Multimap;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.Utils;
import net.riblab.tradecore.integration.WorldGuardUtil;
import net.riblab.tradecore.item.*;
import net.riblab.tradecore.job.JobData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

import static net.riblab.tradecore.Materials.unbreakableMaterial;

/**
 * ブロックの状態変更関連のイベントハンドラ
 */
public class BlockStateEventHandler implements Listener {

    public BlockStateEventHandler() {
        Bukkit.getServer().getPluginManager().registerEvents(this, TradeCore.getInstance());
    }

    @org.bukkit.event.EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        tryCreateBrokenBlock(event);
    }

    /**
     * ブロックにサーバー側でひびを入れることを試みる
     */
    public void tryCreateBrokenBlock(BlockDamageEvent event){
        if (unbreakableMaterial.contains(event.getBlock().getType())) {
            event.setCancelled(true);
            return;
        }

        ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (TCItems.DESTRUCTORS_WAND.get().isSimilar(mainHand) && event.getBlock().getWorld().getName().equals("world")) { //高速破壊杖
            if (TradeCore.isWGLoaded() && !WorldGuardUtil.canBreakBlockWithWG(event.getPlayer(), event.getBlock())) {
                event.setCancelled(true);
                return;
            }

            event.getBlock().setType(Material.AIR);
            return;
        }

        BrokenBlocksService.createBrokenBlock(event.getBlock(), event.getPlayer());
    }
    
    @org.bukkit.event.EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        tryIncrementBlockDamage(event);
    }

    /**
     * ブロックのサーバー側のヒビを大きくする
     */
    public void tryIncrementBlockDamage(PlayerAnimationEvent event){
        Player player = event.getPlayer();
        Set<Material> transparentBlocks = new HashSet<>();
        transparentBlocks.add(Material.WATER);
        transparentBlocks.add(Material.LAVA);
        transparentBlocks.add(Material.AIR);
        Block block = player.getTargetBlock(transparentBlocks, 5);
        Location blockPosition = block.getLocation();

        if (!BrokenBlocksService.getBrokenBlocks().containsKey(player)) return;
        if (BrokenBlocksService.isPlayerBreakingAnotherBlock(event.getPlayer(), blockPosition)) return;

        double distanceX = blockPosition.getX() - player.getLocation().x();
        double distanceY = blockPosition.getY() - player.getLocation().y();
        double distanceZ = blockPosition.getZ() - player.getLocation().z();

        if (distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ >= 1024.0D) return;

        ITCItem itcItem = TCItems.toTCItem(event.getPlayer().getInventory().getItemInMainHand());
        if (!(itcItem instanceof ITCTool tool)) {
            BrokenBlocksService.getBrokenBlock(player).incrementDamage(player, 0.1d); //ツールでないアイテムを持っているなら実質素手
            return;
        }

        int minHardness = LootTables.getMinHardness(block.getType(), tool);
        if (minHardness > tool.getHarvestLevel()) {
            BrokenBlocksService.getBrokenBlock(player).incrementDamage(player, 0.1d); //ツールで採掘できないなら実質素手
            return;
        }

        SoundGroup soundGroup = block.getBlockData().getSoundGroup();
        player.playSound(block.getLocation(), soundGroup.getHitSound(), SoundCategory.BLOCKS, 1f, 1f);
        BrokenBlocksService.getBrokenBlock(player).incrementDamage(player, tool.getActualMiningSpeed());
    }


    @org.bukkit.event.EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        tryHarvestBlockWithCustomTool(event);
    }

    /**
     * カスタムツールが使われていた場合、ドロップ品や敵を生成する
     * @param event
     */
    public void tryHarvestBlockWithCustomTool(BlockBreakEvent event) {
        if (unbreakableMaterial.contains(event.getBlock().getType())) {
            event.setCancelled(true);
            return;
        }

        if (TradeCore.isWGLoaded() && !WorldGuardUtil.canBreakBlockWithWG(event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
            return;
        }

        ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) {//素手
            Multimap<Float, ITCItem> table = LootTables.get(event.getBlock().getType(), ITCTool.ToolType.HAND);
            if (table.size() != 0) {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                Utils.dropItemByLootTable(event.getPlayer(), event.getBlock(), table);
                TradeCore.getInstance().getJobHandler().addJobExp(event.getPlayer(), JobData.JobType.Mower, 1);
                return;
            }
        }

        ITCItem itcItem = TCItems.toTCItem(mainHand);
        if (itcItem instanceof ITCTool tool) { //ツール
            Multimap<Float, ITCItem> table = LootTables.get(event.getBlock().getType(), tool);
            if (table.size() != 0) {
                event.setCancelled(true);
                Utils.dropItemByLootTable(event.getPlayer(), event.getBlock(), table);
                ItemStack newItemStack = tool.reduceDurability(mainHand, 1);
                event.getPlayer().getInventory().setItemInMainHand(newItemStack);

                if (itcItem instanceof ICanSpawnMobOnUse encountableTool) {
                    Utils.trySpawnMob(event.getPlayer(), event.getBlock(), encountableTool.getSpawnTable());
                }

                JobData.JobType jobType = tool.getToolType().getExpType();
                if(jobType != null){
                    //硬度によって経験値が決まるが、硬度0でも1は入るようにする
                    TradeCore.getInstance().getJobHandler().addJobExp(event.getPlayer(), jobType, LootTables.getMinHardness(event.getBlock().getType(), (TCTool) itcItem) + 1);
                }

                event.getBlock().setType(Material.AIR);
                return;
            }
        }

        //適正ツール以外での採掘は何も落とさない
        event.setDropItems(false);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        tryProcessHoeDrop(event);
    }

    /**
     * クワの耕地ドロップ処理を実行すると同時にITCItemが設置されるのを防止する
     * @param event
     */
    public void tryProcessHoeDrop(BlockPlaceEvent event) {
        ITCItem itcItem = TCItems.toTCItem(event.getItemInHand());

        if (itcItem == null)
            return;

        if (event.getBlock().getType() == Material.FARMLAND && itcItem instanceof ITCTool tool) { //耕地を耕したときのドロップ
            Multimap<Float, ITCItem> table = LootTables.get(Material.FARMLAND, tool);
            Utils.dropItemByLootTable(event.getPlayer(), event.getBlock(), table);
            ItemStack newItemStack = tool.reduceDurability(event.getItemInHand(), 1);
            if (event.getHand() == EquipmentSlot.HAND)
                event.getPlayer().getInventory().setItemInMainHand(newItemStack);
            else {
                event.getPlayer().getInventory().setItemInOffHand(newItemStack);
            }
            return;
        }

        event.setCancelled(true);
    }

    @org.bukkit.event.EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        preventVanillaStickFromDropping(event);
    }

    /**
     * バニラの棒が葉っぱからドロップすることを防ぐ
     */
    public void preventVanillaStickFromDropping(LeavesDecayEvent event) {
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);
    }
}
