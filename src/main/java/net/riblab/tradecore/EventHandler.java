package net.riblab.tradecore;

import net.kyori.adventure.text.Component;
import net.riblab.tradecore.item.*;
import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.skill.IArmorModifier;
import net.riblab.tradecore.job.skill.ICanHitWithToolModifier;
import net.riblab.tradecore.job.skill.IHandAttackDamageModifier;
import net.riblab.tradecore.mob.CustomMobService;
import net.riblab.tradecore.ui.UICraftingTable;
import net.riblab.tradecore.ui.UIFurnace;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.riblab.tradecore.Materials.unbreakableMaterial;

/**
 * イベント受信システム
 */
public class EventHandler implements Listener {
    
    private static final Set<EntityDamageEvent.DamageCause> unBlockableDamageCause = Set.of(EntityDamageEvent.DamageCause.SUICIDE, EntityDamageEvent.DamageCause.KILL, 
            EntityDamageEvent.DamageCause.DROWNING, EntityDamageEvent.DamageCause.CUSTOM, EntityDamageEvent.DamageCause.STARVATION, EntityDamageEvent.DamageCause.VOID,
            EntityDamageEvent.DamageCause.WORLD_BORDER);

    public EventHandler() {
        Bukkit.getServer().getPluginManager().registerEvents(this, TradeCore.getInstance());
    }


    @org.bukkit.event.EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event) {
        if (!TradeCore.getInstance().getEconomy().hasAccount(event.getPlayer()))
            TradeCore.getInstance().getEconomy().createPlayerAccount(event.getPlayer());

        Utils.addSlowDig(event.getPlayer());
    }

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Utils.removeSlowDig(event.getPlayer());
    }

    @org.bukkit.event.EventHandler
    public void OnPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking())
            return;
        
        blockAnvilInteraction(event);
        if(event.isCancelled())
            return;
        
        interactCraftingTable(event);
        if(event.isCancelled())
            return;
        
        interactFurnace(event);
        if(event.isCancelled())
            return;
        
        interactVoteTicket(event);
    }

    /**
     * 金床をブロック
     */
    public void blockAnvilInteraction(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ANVIL) {
            event.setCancelled(true);
        }
    }

    /**
     * カスタム作業台 TODO:カスタムブロックで管理
     */
    public void interactCraftingTable(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CRAFTING_TABLE) {
            event.setCancelled(true);
            UICraftingTable.open(event.getPlayer(), UICraftingTable.CraftingScreenType.CATEGORY);
        }
    }

    /**
     * カスタムかまど
     */
    public void interactFurnace(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.FURNACE) {
            event.setCancelled(true);
            UIFurnace.open(event.getPlayer());
        }
    }

    /**
     * 投票プラグインでもらえる引換券を右クリックしたときの処理
     */
    public void interactVoteTicket(PlayerInteractEvent event){
        if (event.getItem() != null && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().equals("§a投票引換券")) {
            event.setCancelled(true);
            TradeCore.getInstance().getEconomy().depositTickets(event.getPlayer(), 1);
            event.getPlayer().sendMessage(Component.text("投票引換券をプレイチケットと引き換えました！"));
            event.getItem().setAmount(event.getItem().getAmount() - 1);
        }
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
        if (!(itcItem instanceof TCTool)) {
            BrokenBlocksService.getBrokenBlock(player).incrementDamage(player, 0.1d); //ツールでないアイテムを持っているなら実質素手
            return;
        }

        int minHardness = LootTables.getMinHardness(block.getType(), (TCTool) itcItem);
        if (minHardness > ((TCTool) itcItem).getHarvestLevel()) {
            BrokenBlocksService.getBrokenBlock(player).incrementDamage(player, 0.1d); //ツール採掘できないなら実質素手
            return;
        }

        SoundGroup soundGroup = block.getBlockData().getSoundGroup();
        player.playSound(block.getLocation(), soundGroup.getHitSound(), SoundCategory.BLOCKS, 1f, 1f);
        BrokenBlocksService.getBrokenBlock(player).incrementDamage(player, ((TCTool) itcItem).getActualMiningSpeed());
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
            Map<Float, ITCItem> table = LootTables.get(event.getBlock().getType(), TCTool.ToolType.HAND);
            if (table.size() != 0) {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                Utils.dropItemByLootTable(event.getPlayer(), event.getBlock(), table);
                TradeCore.getInstance().getJobHandler().addJobExp(event.getPlayer(), JobData.JobType.Mower, 1);
                return;
            }
        }

        ITCItem itcItem = TCItems.toTCItem(mainHand);
        if (itcItem instanceof TCTool tool) { //ツール
            Map<Float, ITCItem> table = LootTables.get(event.getBlock().getType(), (TCTool) itcItem);
            if (table.size() != 0) {
                event.setCancelled(true);
                Utils.dropItemByLootTable(event.getPlayer(), event.getBlock(), table);
                ItemStack newItemStack = tool.reduceDurability(mainHand);
                event.getPlayer().getInventory().setItemInMainHand(newItemStack);

                if (itcItem instanceof TCEncountableTool encountableTool) {
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
        
        if (event.getBlock().getType() == Material.FARMLAND && itcItem instanceof TCTool) { //耕地を耕したときのドロップ
            Map<Float, ITCItem> table = LootTables.get(Material.FARMLAND, (TCTool) itcItem);
            Utils.dropItemByLootTable(event.getPlayer(), event.getBlock(), table);
            ItemStack newItemStack = ((TCTool) itcItem).reduceDurability(event.getItemInHand());
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

    @org.bukkit.event.EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        CustomMobService.onEntityDeath(event);
    }

    @org.bukkit.event.EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        tryReduceWeaponDurability(event);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerReSpawn(PlayerRespawnEvent event){
        new BukkitRunnable() { //他のプラグインのエフェクト除去効果を上書き
            @Override
            public void run() {
                Utils.addSlowDig(event.getPlayer());
            }
        }.runTaskLater(TradeCore.getInstance(), 1);
    }

    /**
     * 攻撃した時に武器の耐久値を減らす
     */
    public void tryReduceWeaponDurability(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player))
            return;
        
        if(player.getInventory().getItemInMainHand().getType() == Material.AIR){ //素手で攻撃
            double damage = event.getDamage();
            event.setDamage(TradeCore.getInstance().getJobSkillHandler().apply(player, damage, IHandAttackDamageModifier.class));
            return;
        }

        ITCItem item = TCItems.toTCItem(player.getInventory().getItemInMainHand());
        if (!(item instanceof TCTool)) {
            event.setCancelled(true);
            return;
        }

        if (!(((TCTool) item).getToolType() == TCTool.ToolType.SWORD)) {
            boolean canhitWithTool = TradeCore.getInstance().getJobSkillHandler().apply(player, false, ICanHitWithToolModifier.class);
            if(!canhitWithTool){
                event.setCancelled(true);
                return;
            }
        }

        ItemStack newItemStack = ((TCTool) item).reduceDurability(player.getInventory().getItemInMainHand());
        player.getInventory().setItemInMainHand(newItemStack);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerDamage(EntityDamageEvent event){
        tryBlockDamageWithCustomArmor(event);
    }

    /**
     * ダメージをカスタム装備でブロックすると同時に装備の耐久を減らす
     */
    public void tryBlockDamageWithCustomArmor(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player player))
            return;
        
        if(unBlockableDamageCause.contains(event.getCause()))
            return;

        if(!event.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) //アーマーで防御出来ないダメージタイプは無視
            return;

        double rawDamage = event.getDamage();
        double armor = 0;
        ItemStack[] newArmorContent = new ItemStack[4];
        for (int i = 0; i < player.getInventory().getArmorContents().length; i++) {
            ITCItem itcItem = TCItems.toTCItem(player.getInventory().getArmorContents()[i]);
            if(!(itcItem instanceof TCEquipment equipment))
                continue;

            armor += equipment.getArmor();
            newArmorContent[i] = equipment.reduceDurability(player.getInventory().getArmorContents()[i]);
        }
        player.getInventory().setArmorContents(newArmorContent);

        double armorSkillApplied = TradeCore.getInstance().getJobSkillHandler().apply(player, armor, IArmorModifier.class);
        
        double finalDamage = (5* rawDamage * rawDamage)/(armorSkillApplied + 5* rawDamage);
        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, finalDamage);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event){
        if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.MILK_BUCKET){ //採掘デバフが剥がれるのを防ぐ
            event.setCancelled(true);
        }
    }
}
