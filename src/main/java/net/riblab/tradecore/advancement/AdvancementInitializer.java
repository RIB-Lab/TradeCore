package net.riblab.tradecore.advancement;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.tasks.MultiTasksAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.tasks.TaskAdvancement;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

import java.util.Objects;

/**
 * 進捗管理クラス
 */
public enum AdvancementInitializer {
    INSTANCE;

    public static final AdvancementTab primitiveAgeTab = TradeCore.getInstance().getAdvancementAPI().createAdvancementTab("primitive_age");
    
    /**
     * 実績が既に登録されたかどうか
     */
    boolean isInit = false;

    public void init() {
        if (isInit)
            return;

        TradeCore.getInstance().getAdvancementAPI().disableVanillaAdvancements();

        ((MultiTasksAdvancement)Advancements.WOODENCOMPONENT.get()).registerTasks((TaskAdvancement) Advancements.WOODENCOMPONENT_SUB1.get(),(TaskAdvancement) Advancements.WOODENCOMPONENT_SUB2.get(),
                (TaskAdvancement) Advancements.WOODENCOMPONENT_SUB3.get());

        ((MultiTasksAdvancement)Advancements.STONECOMPONENT.get()).registerTasks((TaskAdvancement) Advancements.STONECOMPONENT_SUB1.get(), (TaskAdvancement) Advancements.STONECOMPONENT_SUB2.get(), 
                (TaskAdvancement) Advancements.STONECOMPONENT_SUB3.get(), (TaskAdvancement) Advancements.STONECOMPONENT_SUB4.get());

        primitiveAgeTab.registerAdvancements((RootAdvancement) Advancements.GATHERPEBBLE.get(), (BaseAdvancement) Advancements.CRAFTHATCHET.get(),(BaseAdvancement) Advancements.CRAFTTABLE.get(),
                (BaseAdvancement) Advancements.WOODENAXE.get(), (BaseAdvancement) Advancements.WOODENCOMPONENT.get(), (BaseAdvancement) Advancements.STONEAXE.get(), (BaseAdvancement) Advancements.STONECOMPONENT.get(), (BaseAdvancement) Advancements.IRONAXE.get());
        primitiveAgeTab.automaticallyShowToPlayers();

        //TODO:プレイヤー単位で実績を管理。ロード時にプレイヤーが未取得の実績を洗い出してそこにだけイベントが飛ぶようにする
        primitiveAgeTab.getEventManager().register(primitiveAgeTab, PlayerAttemptPickupItemEvent.class, playerPickItemEvent -> {
            ITCItem itcItem = TCItems.toTCItem(playerPickItemEvent.getItem().getItemStack());
            if (Objects.isNull(itcItem))
                return;

            Player player = playerPickItemEvent.getPlayer();
            check(itcItem, TCItems.PEBBLE, Advancements.GATHERPEBBLE.get(), player);
            check(itcItem, TCItems.WOODPULP, Advancements.WOODENCOMPONENT_SUB1.get(), player);
            check(itcItem, TCItems.DUST, Advancements.WOODENCOMPONENT_SUB2.get(), player);
            check(itcItem, TCItems.MOSS, Advancements.WOODENCOMPONENT_SUB3.get(), player);
            check(itcItem, TCItems.ANDESITE_STONE, Advancements.STONECOMPONENT_SUB1.get(), player);
            check(itcItem, TCItems.GRANITE_STONE, Advancements.STONECOMPONENT_SUB2.get(), player);
            check(itcItem, TCItems.DIORITE_STONE, Advancements.STONECOMPONENT_SUB3.get(), player);
            check(itcItem, TCItems.ROUND_STONE, Advancements.STONECOMPONENT_SUB4.get(), player);

        });

        primitiveAgeTab.getEventManager().register(primitiveAgeTab, CraftItemEvent.class, event -> {
            if (!(event.getWhoClicked() instanceof Player player))
                return;

            if (event.getRecipe().getResult().getType() == Material.CRAFTING_TABLE) {
                Advancements.CRAFTTABLE.get().incrementProgression(player);
            }

            ITCItem itcItem = TCItems.toTCItem(event.getRecipe().getResult());
            if (Objects.isNull(itcItem))
                return;

            check(itcItem, TCItems.HATCHET, Advancements.CRAFTHATCHET.get(), player);
        });

        primitiveAgeTab.getEventManager().register(primitiveAgeTab, BlockBreakEvent.class, event -> {
            ITCItem itcItem = TCItems.toTCItem(event.getPlayer().getInventory().getItemInMainHand());
            if (Objects.isNull(itcItem))
                return;

            Player player = event.getPlayer();
            check(itcItem, TCItems.WOODEN_AXE, Advancements.WOODENAXE.get(), player);
            check(itcItem, TCItems.STONE_AXE, Advancements.STONEAXE.get(), player);
            check(itcItem, TCItems.IRON_AXE, Advancements.IRONAXE.get(), player);
            check(itcItem, TCItems.GOLDEN_AXE, Advancements.IRONAXE.get(), player);
        });

        Bukkit.getOnlinePlayers().forEach(primitiveAgeTab::showTab);
        isInit = true;
    }

    private static void check(ITCItem item, TCItems type, Advancement adv, Player player) {
        if (item.equals(type.get())) {
            adv.incrementProgression(player);
        }
    }
}
