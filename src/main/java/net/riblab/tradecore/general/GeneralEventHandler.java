package net.riblab.tradecore.general;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.block.BlockUtils;
import net.riblab.tradecore.dungeon.DungeonService;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.integration.TCResourcePackData;
import net.riblab.tradecore.item.PlayerItemModService;
import net.riblab.tradecore.item.base.*;
import net.riblab.tradecore.mob.CustomMobService;
import net.riblab.tradecore.modifier.IArmorModifier;
import net.riblab.tradecore.modifier.ICanHitWithToolModifier;
import net.riblab.tradecore.modifier.IHandAttackDamageModifier;
import net.riblab.tradecore.playerstats.PlayerStatsService;
import net.riblab.tradecore.ui.UICraftingTable;
import net.riblab.tradecore.ui.UIFurnace;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

/**
 * イベント受信システム
 */
@ParametersAreNonnullByDefault
public final class GeneralEventHandler {

    private static final Set<EntityDamageEvent.DamageCause> unBlockableDamageCause = Set.of(EntityDamageEvent.DamageCause.SUICIDE, EntityDamageEvent.DamageCause.KILL,
            EntityDamageEvent.DamageCause.DROWNING, EntityDamageEvent.DamageCause.CUSTOM, EntityDamageEvent.DamageCause.STARVATION, EntityDamageEvent.DamageCause.VOID,
            EntityDamageEvent.DamageCause.WORLD_BORDER);

    public void processPlayerJoin(PlayerJoinEvent event) {
        if (!TCEconomy.getImpl().hasAccount(event.getPlayer()))
            TCEconomy.getImpl().createPlayerAccount(event.getPlayer());

        BlockUtils.addSlowDig(event.getPlayer());
        PlayerItemModService.getImpl().updateEquipment(event.getPlayer());
        PlayerItemModService.getImpl().updateMainHand(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot());
    }

    public void processPlayerQuit(PlayerQuitEvent event) {
        BlockUtils.removeSlowDig(event.getPlayer());

        PlayerItemModService.getImpl().remove(event.getPlayer());
        PlayerStatsService.getImpl().remove(event.getPlayer());

        DungeonService.getImpl().tryLeave(event.getPlayer());
    }

    public void processPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND)
            return;

        swingWeapon(event);
        if (event.isCancelled())
            return;

        if (event.getPlayer().isSneaking())
            return;

        blockAnvilInteraction(event);
        if (event.isCancelled())
            return;

        interactCraftingTable(event);
        if (event.isCancelled())
            return;

        interactFurnace(event);
        if (event.isCancelled())
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
    public void interactCraftingTable(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CRAFTING_TABLE) {
            event.setCancelled(true);
            UICraftingTable.open(event.getPlayer(), UICraftingTable.CraftingScreenType.CATEGORY);
        }
    }

    /**
     * カスタムかまど
     */
    public void interactFurnace(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.FURNACE) {
            event.setCancelled(true);
            UIFurnace.open(event.getPlayer());
        }
    }

    /**
     * 投票プラグインでもらえる引換券を右クリックしたときの処理
     */
    public void interactVoteTicket(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().equals("§a投票引換券")) {
            event.setCancelled(true);
            TCEconomy.getImpl().depositTickets(event.getPlayer(), 1);
            event.getPlayer().sendMessage(Component.text("投票引換券をプレイチケットと引き換えました！"));
            event.getItem().setAmount(event.getItem().getAmount() - 1);
        }
    }

    /**
     * 宙に向かって武器を振ったとき
     *
     * @param event
     */
    public void swingWeapon(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
            return;

        if (event.getItem() == null)
            return;

        if (event.getPlayer().getAttackCooldown() != 1)
            return;

        ITCItem itcItem = TCItems.toTCItem(event.getItem());
        if (itcItem instanceof ITCWeapon weapon) {
            event.setCancelled(true);
            if (weapon.getAttribute().attack(event.getPlayer())) {
                ItemStack newItemStack = weapon.reduceDurability(event.getPlayer().getInventory().getItemInMainHand(), 1);
                event.getPlayer().getInventory().setItemInMainHand(newItemStack);
            }
        }
    }

    public void processEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return; //TODO:ここでprojectile攻撃の処理分岐

        tryProcessMeleeAttack(event);
    }

    public void processPlayerRespawn(PlayerRespawnEvent event) {
        new BukkitRunnable() { //他のプラグインのエフェクト除去効果を上書き
            @Override
            public void run() {
                BlockUtils.addSlowDig(event.getPlayer());
            }
        }.runTaskLater(TradeCore.getInstance(), 1);
    }

    /**
     * 素手・ツール・武器で近接攻撃した時の処理
     */
    public void tryProcessMeleeAttack(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getDamager();

        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) { //素手で攻撃
            double damage = event.getDamage();
            double newDamage = Utils.apply(player, damage, IHandAttackDamageModifier.class);
            event.setDamage(newDamage);
            if (event.getDamage() == 0)
                event.setCancelled(true);

            return;
        }

        ITCItem item = TCItems.toTCItem(player.getInventory().getItemInMainHand());
        if (item instanceof TCTool) { //ツールで攻撃
            boolean canhitWithTool = Utils.apply(player, false, ICanHitWithToolModifier.class);
            if (!canhitWithTool) {
                event.setCancelled(true);
                return;
            }

            ItemStack newItemStack = ((IHasDurability) item).reduceDurability(player.getInventory().getItemInMainHand(), 1);
            player.getInventory().setItemInMainHand(newItemStack);
        }

        if (item instanceof ITCWeapon weapon) { //武器で攻撃
            event.setCancelled(true);
            if (player.getAttackCooldown() != 1)
                return;

            if (weapon.getAttribute().attack(player)) {
                ItemStack newItemStack = ((IHasDurability) item).reduceDurability(player.getInventory().getItemInMainHand(), 1);
                player.getInventory().setItemInMainHand(newItemStack);
            }
        }
    }

    public void processEntityDamage(EntityDamageEvent event) {
        tryBlockDamageWithCustomArmor(event);
    }

    /**
     * ダメージをカスタム装備でブロックすると同時に装備の耐久を減らす
     */
    public void tryBlockDamageWithCustomArmor(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        if (unBlockableDamageCause.contains(event.getCause()))
            return;

        if (!event.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) //アーマーで防御出来ないダメージタイプは無視
            return;

        double rawDamage = event.getDamage();
        ItemStack[] newArmorContent = new ItemStack[4];
        for (int i = 0; i < player.getInventory().getArmorContents().length; i++) {
            ITCItem itcItem = TCItems.toTCItem(player.getInventory().getArmorContents()[i]);
            if (!(itcItem instanceof ITCEquipment equipment))
                continue;
            newArmorContent[i] = equipment.reduceDurability(player.getInventory().getArmorContents()[i]);
        }
        player.getInventory().setArmorContents(newArmorContent);

        double armor = Utils.apply(player, 0d, IArmorModifier.class); //アーマーの基礎値は0

        double finalDamage = (5 * rawDamage * rawDamage) / (armor + 5 * rawDamage);
        double damageReduction = rawDamage - finalDamage;
        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, -damageReduction);
    }

    public void processPlayerConsumeItem(PlayerItemConsumeEvent event) {
        preventMiningDebuffRemoved(event);
    }

    /**
     * 採掘デバフが剥がれるのを防ぐ
     */
    private void preventMiningDebuffRemoved(PlayerItemConsumeEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.MILK_BUCKET) {
            event.setCancelled(true);
        }
    }

    public void processPlayerArmorChange(PlayerArmorChangeEvent event) {
        PlayerItemModService.getImpl().updateEquipment(event.getPlayer());
    }

    public void processPlayerItemHeld(PlayerItemHeldEvent event) {
        PlayerItemModService.getImpl().updateMainHand(event.getPlayer(), event.getNewSlot());
    }

    public void processInventoryClick(InventoryClickEvent event) {
        checkMainHandItemUpdate(event);
    }

    /**
     * メインハンドのアイテムが更新されたらmodサービスに通知する
     */
    private void checkMainHandItemUpdate(InventoryClickEvent event) {
        int mainhandSlot = event.getWhoClicked().getInventory().getHeldItemSlot();
        if ((event.getSlot() == mainhandSlot || (event.isShiftClick() && !event.getClickedInventory().equals(event.getWhoClicked().getInventory()))) && event.getWhoClicked() instanceof Player player) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    PlayerItemModService.getImpl().updateMainHand(player, mainhandSlot);
                }
            }.runTaskLater(TradeCore.getInstance(), 0);
        }
    }

    public void processPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        checkMainHandItemUpdate(event);
    }

    /**
     * メインハンドのアイテムが更新されたらmodサービスに通知する
     */
    private void checkMainHandItemUpdate(PlayerSwapHandItemsEvent event) {
        new BukkitRunnable() {

            @Override
            public void run() {
                PlayerItemModService.getImpl().updateMainHand(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot());
            }
        }.runTaskLater(TradeCore.getInstance(), 0);
    }

    public void processSecondPassed() {
        updateActionBar();
    }

    /**
     * 所持金と投票券表示
     */
    private void updateActionBar() {
        String negativeSpace = TCResourcePackData.IconsFont.NEGATIVE_SPACE.get_char();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (DungeonService.getImpl().isPlayerInDungeon(player))
                continue;

            int balance = (int) TCEconomy.getImpl().getBalance(player);
            int tickets = TCEconomy.getImpl().getPlayTickets(player);
            Component text = Component.text("");
            text = text.append(Component.text(negativeSpace + negativeSpace + negativeSpace + negativeSpace + TCResourcePackData.IconsFont.COIN.get_char()).font(TCResourcePackData.iconsFontName));
            text = text.append(Component.text(" " + balance).font(TCResourcePackData.yPlus12FontName));
            text = text.append(Component.text("                         " + TCResourcePackData.IconsFont.VOTE_TICKET.get_char()).font(TCResourcePackData.iconsFontName));
            text = text.append(Component.text(" " + tickets).font(TCResourcePackData.yPlus12FontName));
            player.sendActionBar(text);
        }
    }

    /**
     * エンティティの死亡処理
     */
    public void processEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player)//プレイヤーの死亡時ドロップは消さない
            return;

        event.getDrops().clear();//バニラのモブドロップをブロック

        if (!(event.getEntity() instanceof Mob mob))
            return;

        CustomMobService.getImpl().onCustomMobDeath(mob);
    }
}
