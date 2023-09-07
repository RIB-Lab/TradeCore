/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.advancement;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.tasks.MultiTasksAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.tasks.TaskAdvancement;
import lombok.Getter;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.general.ErrorMessages;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

import java.util.Optional;

/**
 * 進捗管理クラス
 */
public enum AdvancementInitializer {
    INSTANCE();

    @Getter
    private static AdvancementTab primitiveAgeTab;

    /**
     * 実績が既に登録されたかどうか
     */
    boolean isInit = false;

    public void init() {
        if (isInit)
            throw new RuntimeException(ErrorMessages.ADVANCEMENT_INIT_TWO_TIMES.get());

        UltimateAdvancementAPI api = UltimateAdvancementAPI.getInstance(TradeCore.getInstance());
        primitiveAgeTab = api.createAdvancementTab("primitive_age");

        api.disableVanillaAdvancements();

        ((MultiTasksAdvancement) Advancements.WOODEN_COMPONENT.get()).registerTasks((TaskAdvancement) Advancements.WOODEN_COMPONENT_SUB1.get(), (TaskAdvancement) Advancements.WOODEN_COMPONENT_SUB2.get(),
                (TaskAdvancement) Advancements.WOODEN_COMPONENT_SUB3.get());

        ((MultiTasksAdvancement) Advancements.STONE_COMPONENT.get()).registerTasks((TaskAdvancement) Advancements.STONE_COMPONENT_SUB1.get(), (TaskAdvancement) Advancements.STONE_COMPONENT_SUB2.get(),
                (TaskAdvancement) Advancements.STONE_COMPONENT_SUB3.get(), (TaskAdvancement) Advancements.STONE_COMPONENT_SUB4.get());

        primitiveAgeTab.registerAdvancements((RootAdvancement) Advancements.GATHER_PEBBLE.get(), (BaseAdvancement) Advancements.CRAFT_HATCHET.get(), (BaseAdvancement) Advancements.CRAFT_TABLE.get(),
                (BaseAdvancement) Advancements.WOODEN_AXE.get(), (BaseAdvancement) Advancements.WOODEN_COMPONENT.get(), (BaseAdvancement) Advancements.STONE_AXE.get(), (BaseAdvancement) Advancements.STONE_COMPONENT.get(), (BaseAdvancement) Advancements.IRON_AXE.get());
        primitiveAgeTab.automaticallyShowToPlayers();

        //TODO:プレイヤー単位で実績を管理。ロード時にプレイヤーが未取得の実績を洗い出してそこにだけイベントが飛ぶようにする
        primitiveAgeTab.getEventManager().register(primitiveAgeTab, PlayerAttemptPickupItemEvent.class, playerPickItemEvent -> {
            Optional<ITCItem> itcItem = TCItems.toTCItem(playerPickItemEvent.getItem().getItemStack());
            if (itcItem.isEmpty()) {
                return;
            }

            Player player = playerPickItemEvent.getPlayer();
            check(itcItem.get(), TCItems.PEBBLE, Advancements.GATHER_PEBBLE.get(), player);
            check(itcItem.get(), TCItems.WOODPULP, Advancements.WOODEN_COMPONENT_SUB1.get(), player);
            check(itcItem.get(), TCItems.DUST, Advancements.WOODEN_COMPONENT_SUB2.get(), player);
            check(itcItem.get(), TCItems.MOSS, Advancements.WOODEN_COMPONENT_SUB3.get(), player);
            check(itcItem.get(), TCItems.ANDESITE_STONE, Advancements.STONE_COMPONENT_SUB1.get(), player);
            check(itcItem.get(), TCItems.GRANITE_STONE, Advancements.STONE_COMPONENT_SUB2.get(), player);
            check(itcItem.get(), TCItems.DIORITE_STONE, Advancements.STONE_COMPONENT_SUB3.get(), player);
            check(itcItem.get(), TCItems.ROUND_STONE, Advancements.STONE_COMPONENT_SUB4.get(), player);

        });

        primitiveAgeTab.getEventManager().register(primitiveAgeTab, CraftItemEvent.class, event -> {
            if (!(event.getWhoClicked() instanceof Player player))
                return;

            if (event.getRecipe().getResult().getType() == Material.CRAFTING_TABLE) {
                Advancements.CRAFT_TABLE.get().incrementProgression(player);
            }

            Optional<ITCItem> itcItem = TCItems.toTCItem(event.getRecipe().getResult());
            if (itcItem.isEmpty())
                return;

            check(itcItem.get(), TCItems.HATCHET, Advancements.CRAFT_HATCHET.get(), player);
        });

        primitiveAgeTab.getEventManager().register(primitiveAgeTab, BlockBreakEvent.class, event -> {
            Optional<ITCItem> itcItem = TCItems.toTCItem(event.getPlayer().getInventory().getItemInMainHand());
            if (itcItem.isEmpty())
                return;

            Player player = event.getPlayer();
            check(itcItem.get(), TCItems.WOODEN_AXE, Advancements.WOODEN_AXE.get(), player);
            check(itcItem.get(), TCItems.STONE_AXE, Advancements.STONE_AXE.get(), player);
            check(itcItem.get(), TCItems.IRON_AXE, Advancements.IRON_AXE.get(), player);
            check(itcItem.get(), TCItems.GOLDEN_AXE, Advancements.IRON_AXE.get(), player);
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
