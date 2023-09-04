package net.riblab.tradecore.block;

import com.google.common.collect.Multimap;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.entity.mob.ITCMob;
import net.riblab.tradecore.entity.mob.MobUtils;
import net.riblab.tradecore.integration.WorldGuardUtil;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.ItemUtils;
import net.riblab.tradecore.item.LootTables;
import net.riblab.tradecore.item.Materials;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.item.mod.IItemMod;
import net.riblab.tradecore.job.data.JobDataService;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.modifier.IMiningSpeedModifier;
import net.riblab.tradecore.modifier.IMonsterSpawnModifier;
import net.riblab.tradecore.modifier.IToolStatsModifier;
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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

/**
 * ブロックの状態変更関連のイベントハンドラ
 */
public final class BlockStateEventHandler implements Listener {

    private BrokenBlocksService getService() {
        return BrokenBlocksService.getImpl();
    }

    /**
     * ブロックにサーバー側でひびを入れることを試みる
     */
    @ParametersAreNonnullByDefault
    public void tryCreateBrokenBlock(BlockDamageEvent event) {
        if (Materials.UNBREAKABLE.get().contains(event.getBlock().getType())) {
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

        getService().createBrokenBlock(event.getBlock(), event.getPlayer());
    }

    /**
     * ブロックのサーバー側のヒビを大きくする
     */
    @ParametersAreNonnullByDefault
    public void tryIncrementBlockDamage(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        Set<Material> transparentBlocks = new HashSet<>();
        transparentBlocks.add(Material.WATER);
        transparentBlocks.add(Material.LAVA);
        transparentBlocks.add(Material.AIR);
        Block block = player.getTargetBlock(transparentBlocks, 5);
        Location blockPosition = block.getLocation();

        if (!getService().isPlayerAlreadyBreaking(player)) return;
        if (getService().isPlayerBreakingAnotherBlock(event.getPlayer(), blockPosition)) return;

        double distanceX = blockPosition.getX() - player.getLocation().x();
        double distanceY = blockPosition.getY() - player.getLocation().y();
        double distanceZ = blockPosition.getZ() - player.getLocation().z();

        if (distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ >= 1024.0D) return;

        ItemStack mainHandItem = event.getPlayer().getInventory().getItemInMainHand();
        ITCItem itcItem = TCItems.toTCItem(mainHandItem);
        if(Objects.isNull(itcItem)){
            getService().incrementDamage(player, 0.1d); //カスタムアイテム以外を持っているなら実質素手
            return;
        }
        List<IItemMod<?>> mods = itcItem.getDefaultMods();
        IToolStatsModifier mod = (IToolStatsModifier) mods.stream().filter(iItemMod -> iItemMod instanceof IToolStatsModifier).findFirst().orElse(null);
        if(Objects.isNull(mod)){
            getService().incrementDamage(player, 0.1d); //ツールステータスが付与されているアイテム以外を持っているなら実質素手
            return;
        }

        int minHardness = LootTables.getMinHardness(block.getType(), mod);
        if (minHardness > mod.apply(null, null).getHarvestLevel()) {
            getService().incrementDamage(player, 0.1d); //ツールで採掘できないなら実質素手
            return;
        }

        SoundGroup soundGroup = block.getBlockData().getSoundGroup();
        player.playSound(block.getLocation(), soundGroup.getHitSound(), SoundCategory.BLOCKS, 1f, 1f);
        getService().incrementDamage(player, getActualMiningSpeed(mainHandItem));
    }

    /**
     * ツールを一振りしたらどれくらい亀裂が入るかの実際の値を取得
     */
    public double getActualMiningSpeed(ItemStack itemStack){
        List<IItemMod<?>> mods = new ItemCreator(itemStack).getItemRandomMods();
        IMiningSpeedModifier miningSpeedMod = (IMiningSpeedModifier) mods.stream().filter(iItemMod -> iItemMod instanceof IMiningSpeedModifier).findFirst().orElse(null);
        double miningSpeed = 0.1d;
        if(Objects.nonNull(miningSpeedMod))
            miningSpeed = miningSpeedMod.apply(miningSpeed, miningSpeed);
        return miningSpeed;
    }

    /**
     * カスタムツールが使われていた場合、ドロップ品や敵を生成する
     */
    @ParametersAreNonnullByDefault
    public void tryHarvestBlockWithCustomTool(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && Materials.UNBREAKABLE.get().contains(event.getBlock().getType())) {
            event.setCancelled(true);
            return;
        }

        if (TradeCore.isWGLoaded() && !WorldGuardUtil.canBreakBlockWithWG(event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
            return;
        }

        ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) {//素手
            Multimap<Float, ITCItem> table = LootTables.get(event.getBlock().getType(), IToolStatsModifier.ToolType.HAND);
            if (table.size() != 0) {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                ItemUtils.dropItemByLootTable(event.getPlayer(), event.getBlock(), table);
                JobDataService.getImpl().addJobExp(event.getPlayer(), JobType.MOWER, 1);
                return;
            }
        }

        ITCItem itcItem = TCItems.toTCItem(mainHand);
        if(Objects.isNull(itcItem)){
            event.setDropItems(false);//適正ツール以外での採掘は何も落とさない
            return;
        }
        
        List<IItemMod<?>> mods = itcItem.getDefaultMods();
        IToolStatsModifier toolMod = (IToolStatsModifier) mods.stream().filter(iItemMod -> iItemMod instanceof IToolStatsModifier).findFirst().orElse(null);
        if (Objects.isNull(toolMod)) { //ツール
            //適正ツール以外での採掘は何も落とさない
            event.setDropItems(false);
            return;
        }

        Multimap<Float, ITCItem> table = LootTables.get(event.getBlock().getType(), toolMod);
        if (table.isEmpty()) {
            event.setDropItems(false);
            return;
        }

        event.setCancelled(true);
        ItemUtils.dropItemByLootTable(event.getPlayer(), event.getBlock(), table);
        ItemStack newItemStack = ItemUtils.reduceDurabilityIfPossible(mainHand, 1);
        event.getPlayer().getInventory().setItemInMainHand(newItemStack);

        IMonsterSpawnModifier spawnMod = (IMonsterSpawnModifier) itcItem.getDefaultMods().stream().filter(iItemMod -> iItemMod instanceof IMonsterSpawnModifier).findFirst().orElse(null);
        if (Objects.nonNull(spawnMod)) {
            List<ITCMob> mobsToSpawn = new ArrayList<>();
            mobsToSpawn = spawnMod.apply(mobsToSpawn, mobsToSpawn);
            MobUtils.trySpawnMobInRandomArea(event.getPlayer(), event.getBlock(), mobsToSpawn, 5);
        }

        JobType jobType = toolMod.apply(null,null).getToolType().getExpType();
        if (Objects.nonNull(jobType)) {
            //硬度によって経験値が決まるが、硬度0でも1は入るようにする
            JobDataService.getImpl().addJobExp(event.getPlayer(), jobType, LootTables.getMinHardness(event.getBlock().getType(), toolMod) + 1);
        }

        event.getBlock().setType(Material.AIR);
    }

    /**
     * クワの耕地ドロップ処理を実行すると同時にITCItemが設置されるのを防止する
     */
    @ParametersAreNonnullByDefault
    public void tryProcessHoeDrop(BlockPlaceEvent event) {
        ITCItem itcItem = TCItems.toTCItem(event.getItemInHand());

        if (Objects.isNull(itcItem))
            return;

        List<IItemMod<?>> mods = itcItem.getDefaultMods();
        IToolStatsModifier toolMod = (IToolStatsModifier) mods.stream().filter(iItemMod -> iItemMod instanceof IToolStatsModifier).findFirst().orElse(null);
        if (event.getBlock().getType() == Material.FARMLAND && Objects.nonNull(toolMod)) { //耕地を耕したときのドロップ
            Multimap<Float, ITCItem> table = LootTables.get(Material.FARMLAND, toolMod);
            ItemUtils.dropItemByLootTable(event.getPlayer(), event.getBlock(), table);
            ItemStack newItemStack = ItemUtils.reduceDurabilityIfPossible(event.getItemInHand(), 1);
            if (event.getHand() == EquipmentSlot.HAND)
                event.getPlayer().getInventory().setItemInMainHand(newItemStack);
            else {
                event.getPlayer().getInventory().setItemInOffHand(newItemStack);
            }
        }
    }

    /**
     * バニラの棒が葉っぱからドロップすることを防ぐ
     */
    @ParametersAreNonnullByDefault
    public void preventVanillaStickFromDropping(LeavesDecayEvent event) {
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);
    }
}
