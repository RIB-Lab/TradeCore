package net.riblab.tradecore.item;


import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.mob.TCMobs;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

/**
 * アイテムレジストリ
 */
public enum TCItems {
    //原始時代
    PEBBLE(new TCTool(Component.text("小石"), Material.PAPER, "pebble", 1, TCTool.ToolType.AXE, 0, 1.1, -1)),
    HATCHET(new TCTool(Component.text("ハチェット"), Material.IRON_AXE, "hatchet", 1, TCTool.ToolType.AXE, 1, 1.2, 10)),
    STICK(new TCSellableItem(Component.text("木の棒"), Material.STICK, "stick", 0, 0.01d)),
    BARK(new TCSellableItem(Component.text("樹皮"), Material.OAK_LOG, "bark", 1, 0.01d)),
    TWIG(new TCSellableItem(Component.text("小枝"), Material.OAK_LOG, "twig", 3, 0.01d)),
    ROUND_TRUNK(new TCSellableItem(Component.text("丸太"), Material.OAK_LOG, "round_trunk", 2, 0.02d)),
    DRYGRASS(new TCSellableItem(Component.text("干し草"), Material.PAPER, "drygrass", 2, 0.01d)),

    //原木時代
    WOODEN_AXE(new TCTool(Component.text("木の斧"), Material.WOODEN_AXE, "wooden_axe", 0, TCTool.ToolType.AXE, 2, 1.2, 32)),
    WOODEN_SHOVEL(new TCTool(Component.text("木のシャベル"), Material.WOODEN_SHOVEL, "wooden_shovel", 0, TCTool.ToolType.SHOVEL, 0, 1.2, 32)),
    WOODEN_PICKAXE(new TCTool(Component.text("木のツルハシ"), Material.WOODEN_PICKAXE, "wooden_pickaxe", 0, TCTool.ToolType.PICKAXE, 0, 1.2, 32)),
    WOODEN_HOE(new TCTool(Component.text("木のクワ"), Material.WOODEN_HOE, "wooden_hoe", 0, TCTool.ToolType.HOE, 0, 1.2, 32)),
    WOODEN_SWORD(new TCTool(Component.text("木の剣"), Material.WOODEN_SWORD, "wooden_sword", 0, TCTool.ToolType.SWORD, 0, 1, 32)),
    COIN(new TCItem(Component.text("工費："), Material.GOLD_INGOT, "coin", 1)),
    DUST(new TCSellableItem(Component.text("塵"), Material.DIRT, "dust", 1, 0.02d)),
    MUD(new TCSellableItem(Component.text("泥"), Material.DIRT, "mud", 2, 0.02d)),
    WIDESTONE(new TCSellableItem(Component.text("横長の石"), Material.COBBLESTONE, "widestone", 1, 0.02d)),
    TALLSTONE(new TCSellableItem(Component.text("縦長の石"), Material.COBBLESTONE, "tallstone", 2, 0.02d)),
    WOODPULP(new TCSellableItem(Component.text("木くず"), Material.OAK_PLANKS, "woodpulp", 1, 0.02d)),
    MOSS(new TCSellableItem(Component.text("コケ"), Material.DIRT, "moss", 3, 0.02d)),
    WOODEN_COMPONENT(new TCSellableItem(Component.text("木の強化資材"), Material.OAK_PLANKS, "wooden_component", 2, 0.05d)),
    NEXT_PAGE(new TCItem(Component.text("次のページ"), Material.ARROW, "nextpage", 1)),
    PREVIOUS_PAGE(new TCItem(Component.text("前のページ"), Material.ARROW, "previouspage", 2)),
    
    //石器時代
    BIG_STONE(new TCSellableItem(Component.text("大きな石"), Material.STONE, "big_stone", 0, 0.05d)),
    STONE_SWORD(new TCTool(Component.text("石の剣"), Material.STONE_SWORD, "stone_sword", 0, TCTool.ToolType.SWORD, 0, 1, 128)),
    FUEL_BALL(new TCSellableItem(Component.text("燃料玉"), Material.HAY_BLOCK, "fuel_ball", 0, 0.05d)),
    
    //その他
    DESTRUCTORS_WAND(new TCItem(Component.text("メインワールド高速破壊杖"), Material.STICK, "destructors_wand", 1)),

    //モブ召喚系は他のアイテムを参照するので必ず最後に配置
    STONE_AXE(new TCEncountableTool(Component.text("石の斧"), Material.STONE_AXE, "stone_axe", 0, TCTool.ToolType.AXE, 3, 1.25, 128, Map.of(TCMobs.BASIC_TREANT.get(), 0.01f))),
    STONE_SHOVEL(new TCTool(Component.text("石のシャベル"), Material.STONE_SHOVEL, "stone_shovel", 0, TCTool.ToolType.SHOVEL, 1, 1.25, 128)),
    STONE_PICKAXE(new TCEncountableTool(Component.text("石のツルハシ"), Material.STONE_PICKAXE, "stone_pickaxe", 0, TCTool.ToolType.PICKAXE, 1, 1.25, 128, Map.of(TCMobs.BASIC_SILVERFISH.get(), 0.01f))),
    STONE_HOE(new TCTool(Component.text("石のクワ"), Material.STONE_HOE, "stone_hoe", 0, TCTool.ToolType.HOE, 1, 1.25, 128));

    private final ITCItem tcItem;

    TCItems(ITCItem tcItem) {
        this.tcItem = tcItem;
    }

    public ITCItem get() {
        return tcItem;
    }

    /**
     * アイテムが固有アイテムであった場合その実体を固有アイテムクラスに変換する<br>
     * この際実体特有のNBTなどは失われる
     * @param itemStack 変換したいアイテム
     * @return 変換された固有アイテム
     */
    @Nullable
    public static ITCItem toTCItem(ItemStack itemStack){
        TCItems itcItem = Arrays.stream(TCItems.values()).filter(e-> e.get().isSimilar(itemStack)).findFirst().orElse(null);
        return itcItem == null ? null : itcItem.get();
    }

    /**
     * 固有アイテムの召喚コマンドを固有アイテムに変換する
     * @param command 召喚コマンド
     * @return 変換された固有アイテム
     */
    @Nullable
    public static ITCItem commandToTCItem(String command){
        TCItems itcItem = Arrays.stream(TCItems.values()).filter(e -> e.get().getInternalName().equals(command)).findFirst().orElse(null);
        return itcItem == null ? null : itcItem.get();
    }

    // Function that returns our custom argument
    public static Argument<ITCItem> customITCItemArgument(String nodeName) {

        // Construct our CustomArgument that takes in a String input and returns a World object
        return new CustomArgument<ITCItem, String>(new StringArgument(nodeName), info -> {
            // Parse the itcItem from our input
            ITCItem itcItem = commandToTCItem(info.input());

            if (itcItem == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown item: ").appendArgInput());
            } else {
                return itcItem;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                // List of world names on the server
                Arrays.stream(values()).map(tcItems -> tcItems.get().getInternalName()).toArray(String[]::new))
        );
    }
}
