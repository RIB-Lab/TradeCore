package net.riblab.tradecore.item;


import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;

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
    COIN(new TCItem(Component.text("工費："), Material.GOLD_INGOT, "coin", 0)),
    DUST(new TCItem(Component.text("塵"), Material.DIRT, "dust", 1)),
    MUD(new TCItem(Component.text("泥"), Material.DIRT, "mud", 2)),
    WIDESTONE(new TCItem(Component.text("横長の石"), Material.COBBLESTONE, "widestone", 1)),
    TALLSTONE(new TCItem(Component.text("縦長の石"), Material.COBBLESTONE, "tallstone", 2)),
    WOODPULP(new TCItem(Component.text("木くず"), Material.OAK_PLANKS, "woodpulp", 1)),
    MOSS(new TCItem(Component.text("コケ"), Material.DIRT, "moss", 3)),
    WOODEN_COMPONENT(new TCItem(Component.text("木の強化資材"), Material.OAK_PLANKS, "wooden_component", 2)),
    
    //石器時代
    STONE_AXE(new TCTool(Component.text("石の斧"), Material.STONE_AXE, "stone_axe", 0, TCTool.ToolType.AXE, 3, 1.25, 128)),
    STONE_SHOVEL(new TCTool(Component.text("石のシャベル"), Material.STONE_SHOVEL, "stone_shovel", 0, TCTool.ToolType.SHOVEL, 1, 1.25, 128)),
    STONE_PICKAXE(new TCTool(Component.text("石のツルハシ"), Material.STONE_PICKAXE, "stone_pickaxe", 0, TCTool.ToolType.PICKAXE, 1, 1.25, 128)),
    STONE_HOE(new TCTool(Component.text("石のクワ"), Material.STONE_HOE, "stone_hoe", 0, TCTool.ToolType.HOE, 1, 1.25, 128));
    
    private final ITCItem tcItem;

    TCItems(ITCItem ocItem) {
        this.tcItem = ocItem;
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
