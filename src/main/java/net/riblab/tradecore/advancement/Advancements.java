package net.riblab.tradecore.advancement;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.tasks.AbstractMultiTasksAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.tasks.MultiTasksAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.tasks.TaskAdvancement;

import static net.riblab.tradecore.advancement.AdvancementInitializer.primitiveAgeTab;

/**
 * 実績の本体レジストリ
 */
public enum Advancements {
    GATHERPEBBLE(new RootAdvancement(primitiveAgeTab, "adv_gather_pebble", AdvancementDisplays.GATHERPEBBLES.get(), "textures/block/stone.png", 1)),
    CRAFTHATCHET(new BaseAdvancement("adv_craft_hatchet", AdvancementDisplays.CRAFTHATCHETS.get(), GATHERPEBBLE.get(), 1)),
    CRAFTTABLE(new BaseAdvancement("adv_craft_table", AdvancementDisplays.CRAFTTABLE.get(), CRAFTHATCHET.get(), 1)),
    WOODENAXE(new BaseAdvancement("adv_wooden_axe", AdvancementDisplays.WOODENAXE.get(), CRAFTTABLE.get(), 1)),
    WOODENCOMPONENT(new MultiTasksAdvancement("adv_wooden_component", AdvancementDisplays.WOODENCOMPONENT.get(), WOODENAXE.get(), 3)),
    WOODENCOMPONENT_SUB1(new TaskAdvancement("task_woodpulp", (AbstractMultiTasksAdvancement) WOODENCOMPONENT.get())),
    WOODENCOMPONENT_SUB2(new TaskAdvancement("task_dust", (AbstractMultiTasksAdvancement) WOODENCOMPONENT.get())),
    WOODENCOMPONENT_SUB3(new TaskAdvancement("task_moss", (AbstractMultiTasksAdvancement) WOODENCOMPONENT.get())),
    STONEAXE(new BaseAdvancement("adv_stone_axe", AdvancementDisplays.STONEAXE.get(), WOODENCOMPONENT.get(), 1)),
    STONECOMPONENT(new MultiTasksAdvancement("adv_stone_component", AdvancementDisplays.STONECOMPONENT.get(), STONEAXE.get(), 4)),
    STONECOMPONENT_SUB1(new TaskAdvancement("task_andestitestone", (AbstractMultiTasksAdvancement) STONECOMPONENT.get())),
    STONECOMPONENT_SUB2(new TaskAdvancement("task_granitestone", (AbstractMultiTasksAdvancement) STONECOMPONENT.get())),
    STONECOMPONENT_SUB3(new TaskAdvancement("task_dioritestone", (AbstractMultiTasksAdvancement) STONECOMPONENT.get())),
    STONECOMPONENT_SUB4(new TaskAdvancement("task_roundstone", (AbstractMultiTasksAdvancement) STONECOMPONENT.get())),
    IRONAXE(new BaseAdvancement("adv_iron_axe", AdvancementDisplays.IRONAXE.get(), STONECOMPONENT.get(), 1))
    ;

    private final Advancement adv;

    Advancements(Advancement adv) {
        this.adv = adv;
    }
    
    public Advancement get(){
        return adv;
    }
}
