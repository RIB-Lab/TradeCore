package net.riblab.tradecore.general;

import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import net.riblab.tradecore.item.base.TCItems;
import org.bukkit.Material;

enum AdvancementDisplays {
    GATHERPEBBLES(new AdvancementDisplay(TCItems.PEBBLE.get().getItemStack(), "ようこそ", AdvancementFrameType.TASK, true, true, 0, 0, "素手で草を壊して小石を集めた")),
    CRAFTHATCHETS(new AdvancementDisplay(TCItems.HATCHET.get().getItemStack(), "知恵          ", AdvancementFrameType.TASK, true, true, 1, 0, "小石で葉ブロックを壊して入手した棒と小石をクラフトして、最初のツールを入手した")),
    CRAFTTABLE(new AdvancementDisplay(Material.CRAFTING_TABLE, "作業(を依頼する)台",AdvancementFrameType.TASK, true, true, 2, 0, "ハチェットで木を伐って丸太を4つ集めて、作業台をクラフトした")),
    WOODENAXE(new AdvancementDisplay(TCItems.WOODEN_AXE.get().getItemStack(), "高効率伐採", AdvancementFrameType.TASK, true, true, 3, 0, "木の斧で伐採した")),
    WOODENCOMPONENT(new AdvancementDisplay(TCItems.WOODEN_COMPONENT.get().getItemStack(), "色々集めた", AdvancementFrameType.TASK, true, true, 4, 0, "wikiを見ながら木の強化資材の材料を集めた")),
    STONEAXE(new AdvancementDisplay(TCItems.STONE_AXE.get().getItemStack(), "湧き出る丸太", AdvancementFrameType.TASK, true, true, 5, 0, "石の斧で伐採した")),
    STONECOMPONENT(new AdvancementDisplay(TCItems.STONE_COMPONENT.get().getItemStack(), "さらに色々集めた", AdvancementFrameType.TASK, true, true, 6, 0, "wikiを見ながら石の強化資材の材料を集めた")),
    IRONAXE(new AdvancementDisplay(TCItems.IRON_AXE.get().getItemStack(), "鉄金器時代", AdvancementFrameType.GOAL, true, true, 7, 0, "鉄の斧/金の斧で木を伐った")),
    
    ;
    
    private final AdvancementDisplay display;

    AdvancementDisplays(AdvancementDisplay display) {
        this.display = display;
    }
    
    public AdvancementDisplay get(){
        return display;
    }
}
