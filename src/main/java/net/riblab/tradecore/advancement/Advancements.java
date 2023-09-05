package net.riblab.tradecore.advancement;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.tasks.AbstractMultiTasksAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.tasks.MultiTasksAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.tasks.TaskAdvancement;

/**
 * 実績の本体レジストリ
 */
public enum Advancements {
    GATHER_PEBBLE(new RootAdvancement(AdvancementInitializer.getPrimitiveAgeTab(), "adv_gather_pebble", AdvancementDisplays.GATHER_PEBBLES.get(), "textures/block/stone.png", 1)),
    CRAFT_HATCHET(new BaseAdvancement("adv_craft_hatchet", AdvancementDisplays.CRAFT_HATCHETS.get(), GATHER_PEBBLE.get(), 1)),
    CRAFT_TABLE(new BaseAdvancement("adv_craft_table", AdvancementDisplays.CRAFT_TABLE.get(), CRAFT_HATCHET.get(), 1)),
    WOODEN_AXE(new BaseAdvancement("adv_wooden_axe", AdvancementDisplays.WOODEN_AXE.get(), CRAFT_TABLE.get(), 1)),
    WOODEN_COMPONENT(new MultiTasksAdvancement("adv_wooden_component", AdvancementDisplays.WOODEN_COMPONENT.get(), WOODEN_AXE.get(), 3)),
    WOODEN_COMPONENT_SUB1(new TaskAdvancement("task_woodpulp", (AbstractMultiTasksAdvancement) WOODEN_COMPONENT.get())),
    WOODEN_COMPONENT_SUB2(new TaskAdvancement("task_dust", (AbstractMultiTasksAdvancement) WOODEN_COMPONENT.get())),
    WOODEN_COMPONENT_SUB3(new TaskAdvancement("task_moss", (AbstractMultiTasksAdvancement) WOODEN_COMPONENT.get())),
    STONE_AXE(new BaseAdvancement("adv_stone_axe", AdvancementDisplays.STONE_AXE.get(), WOODEN_COMPONENT.get(), 1)),
    STONE_COMPONENT(new MultiTasksAdvancement("adv_stone_component", AdvancementDisplays.STONE_COMPONENT.get(), STONE_AXE.get(), 4)),
    STONE_COMPONENT_SUB1(new TaskAdvancement("task_andestitestone", (AbstractMultiTasksAdvancement) STONE_COMPONENT.get())),
    STONE_COMPONENT_SUB2(new TaskAdvancement("task_granitestone", (AbstractMultiTasksAdvancement) STONE_COMPONENT.get())),
    STONE_COMPONENT_SUB3(new TaskAdvancement("task_dioritestone", (AbstractMultiTasksAdvancement) STONE_COMPONENT.get())),
    STONE_COMPONENT_SUB4(new TaskAdvancement("task_roundstone", (AbstractMultiTasksAdvancement) STONE_COMPONENT.get())),
    IRON_AXE(new BaseAdvancement("adv_iron_axe", AdvancementDisplays.IRON_AXE.get(), STONE_COMPONENT.get(), 1))
    ;

    private final Advancement adv;

    Advancements(Advancement adv) {
        this.adv = adv;
    }
    
    public Advancement get(){
        return adv;
    }
}
