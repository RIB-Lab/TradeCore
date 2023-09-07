/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.advancement;

import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import net.riblab.tradecore.item.base.TCItemRegistry;
import net.riblab.tradecore.item.base.TCItems;
import org.bukkit.Material;

/**
 * 実績の表示部分 TODO: 保存
 */
enum AdvancementDisplays {
    GATHER_PEBBLES(new AdvancementDisplay(TCItemRegistry.INSTANCE.commandToTCItem("pebble").orElseThrow().getTemplateItemStack(), "ようこそ", AdvancementFrameType.TASK, true, true, 0, 0, "素手で草を壊して小石を集めた")),
    CRAFT_HATCHETS(new AdvancementDisplay(TCItemRegistry.INSTANCE.commandToTCItem("hatchet").orElseThrow().getTemplateItemStack(), "知恵          ", AdvancementFrameType.TASK, true, true, 1, 0, "小石で葉ブロックを壊して入手した棒と小石をクラフトして、最初のツールを入手した")),
    CRAFT_TABLE(new AdvancementDisplay(Material.CRAFTING_TABLE, "作業(を依頼する)台", AdvancementFrameType.TASK, true, true, 2, 0, "ハチェットで木を伐って丸太を4つ集めて、作業台をクラフトした")),
    WOODEN_AXE(new AdvancementDisplay(TCItemRegistry.INSTANCE.commandToTCItem("wooden_axe").orElseThrow().getTemplateItemStack(), "高効率伐採", AdvancementFrameType.TASK, true, true, 3, 0, "木の斧で伐採した")),
    WOODEN_COMPONENT(new AdvancementDisplay(TCItemRegistry.INSTANCE.commandToTCItem("wooden_component").orElseThrow().getTemplateItemStack(), "色々集めた", AdvancementFrameType.TASK, true, true, 4, 0, "wikiを見ながら木の強化資材の材料を集めた")),
    STONE_AXE(new AdvancementDisplay(TCItemRegistry.INSTANCE.commandToTCItem("stone_axe").orElseThrow().getTemplateItemStack(), "湧き出る丸太", AdvancementFrameType.TASK, true, true, 5, 0, "石の斧で伐採した")),
    STONE_COMPONENT(new AdvancementDisplay(TCItemRegistry.INSTANCE.commandToTCItem("stone_component").orElseThrow().getTemplateItemStack(), "さらに色々集めた", AdvancementFrameType.TASK, true, true, 6, 0, "wikiを見ながら石の強化資材の材料を集めた")),
    IRON_AXE(new AdvancementDisplay(TCItemRegistry.INSTANCE.commandToTCItem("iron_axe").orElseThrow().getTemplateItemStack(), "鉄金器時代", AdvancementFrameType.GOAL, true, true, 7, 0, "鉄の斧/金の斧で木を伐った")),

    ;

    private final AdvancementDisplay display;

    AdvancementDisplays(AdvancementDisplay display) {
        this.display = display;
    }

    public AdvancementDisplay get() {
        return display;
    }
}
