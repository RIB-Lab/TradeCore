package net.riblab.tradecore.general;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
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

/**
 * 進捗管理クラス
 */
public enum AdvancementInitializer {
    INSTANCE;

    private AdvancementTab primitiveAgeTab;

    /**
     * 実績が既に登録されたかどうか
     */
    boolean isInit = false;

    private UltimateAdvancementAPI getAPI() {
        return TradeCore.getInstance().getAdvancementAPI();
    }

    public void init() {
        if (isInit)
            return;

        getAPI().disableVanillaAdvancements();

        primitiveAgeTab = getAPI().createAdvancementTab("primitive_age");
        
        RootAdvancement gatherPebbleAdv = new RootAdvancement(primitiveAgeTab, "adv_gather_pebble", AdvancementDisplays.GATHERPEBBLES.get(), "textures/block/stone.png", 1);
        
        BaseAdvancement craftHatchetAdv = new BaseAdvancement("adv_craft_hatchet", AdvancementDisplays.CRAFTHATCHETS.get(), gatherPebbleAdv, 1);
        
        BaseAdvancement craftTableAdv = new BaseAdvancement("adv_craft_table", AdvancementDisplays.CRAFTTABLE.get(), craftHatchetAdv, 1);
        
        BaseAdvancement woodenAxeAdv = new BaseAdvancement("adv_wooden_axe", AdvancementDisplays.WOODENAXE.get(), craftTableAdv, 1);
        
        MultiTasksAdvancement woodenComponentAdv = new MultiTasksAdvancement("adv_wooden_component", AdvancementDisplays.WOODENCOMPONENT.get(), woodenAxeAdv, 3);

        TaskAdvancement woodpulpTask = new TaskAdvancement("task_woodpulp", woodenComponentAdv);
        TaskAdvancement dustTask = new TaskAdvancement("task_dust", woodenComponentAdv);
        TaskAdvancement mossTask = new TaskAdvancement("task_moss", woodenComponentAdv);

        woodenComponentAdv.registerTasks(woodpulpTask, dustTask, mossTask);
        
        BaseAdvancement stoneAxeAdv = new BaseAdvancement("adv_stone_axe", AdvancementDisplays.STONEAXE.get(), woodenComponentAdv, 1);
        
        MultiTasksAdvancement stoneComponentAdv = new MultiTasksAdvancement("adv_stone_component", AdvancementDisplays.STONECOMPONENT.get(), stoneAxeAdv, 4);

        TaskAdvancement andesiteStoneTask = new TaskAdvancement("task_andestitestone", stoneComponentAdv);
        TaskAdvancement graniteStoneTask = new TaskAdvancement("task_granitestone", stoneComponentAdv);
        TaskAdvancement dioriteStoneTask = new TaskAdvancement("task_dioritestone", stoneComponentAdv);
        TaskAdvancement roundStoneTask = new TaskAdvancement("task_roundstone", stoneComponentAdv);

        stoneComponentAdv.registerTasks(andesiteStoneTask, graniteStoneTask, dioriteStoneTask, roundStoneTask);

        
        BaseAdvancement ironAxeAdv = new BaseAdvancement("adv_iron_axe", AdvancementDisplays.IRONAXE.get(), stoneComponentAdv, 1);

        primitiveAgeTab.registerAdvancements(gatherPebbleAdv, craftHatchetAdv, craftTableAdv, woodenAxeAdv, woodenComponentAdv, stoneAxeAdv, stoneComponentAdv, ironAxeAdv);
        primitiveAgeTab.automaticallyShowToPlayers();

        //TODO:プレイヤー単位で実績を管理。ロード時にプレイヤーが未取得の実績を洗い出してそこにだけイベントが飛ぶようにする
        primitiveAgeTab.getEventManager().register(primitiveAgeTab, PlayerAttemptPickupItemEvent.class, playerPickItemEvent -> {
            ITCItem itcItem = TCItems.toTCItem(playerPickItemEvent.getItem().getItemStack());
            if (itcItem == null)
                return;

            Player player = playerPickItemEvent.getPlayer();
            check(itcItem, TCItems.PEBBLE, gatherPebbleAdv, player);
            check(itcItem, TCItems.WOODPULP, woodpulpTask, player);
            check(itcItem, TCItems.DUST, dustTask, player);
            check(itcItem, TCItems.MOSS, mossTask, player);
            check(itcItem, TCItems.ANDESITE_STONE, andesiteStoneTask, player);
            check(itcItem, TCItems.GRANITE_STONE, graniteStoneTask, player);
            check(itcItem, TCItems.DIORITE_STONE, dioriteStoneTask, player);
            check(itcItem, TCItems.ROUND_STONE, roundStoneTask, player);

        });

        primitiveAgeTab.getEventManager().register(primitiveAgeTab, CraftItemEvent.class, event -> {
            if (!(event.getWhoClicked() instanceof Player player))
                return;

            if (event.getRecipe().getResult().getType() == Material.CRAFTING_TABLE) {
                craftTableAdv.incrementProgression(player);
            }

            ITCItem itcItem = TCItems.toTCItem(event.getRecipe().getResult());
            if (itcItem == null)
                return;

            check(itcItem, TCItems.HATCHET, craftHatchetAdv, player);
        });

        primitiveAgeTab.getEventManager().register(primitiveAgeTab, BlockBreakEvent.class, event -> {
            ITCItem itcItem = TCItems.toTCItem(event.getPlayer().getInventory().getItemInMainHand());
            if (itcItem == null)
                return;

            Player player = event.getPlayer();
            check(itcItem, TCItems.WOODEN_AXE, woodenAxeAdv, player);
            check(itcItem, TCItems.STONE_AXE, stoneAxeAdv, player);
            check(itcItem, TCItems.IRON_AXE, ironAxeAdv, player);
        });

        Bukkit.getOnlinePlayers().forEach(player -> primitiveAgeTab.showTab(player));
        isInit = true;
    }

    private static void check(ITCItem item, TCItems type, Advancement adv, Player player) {
        if (item.equals(type.get())) {
            adv.incrementProgression(player);
        }
    }
}
