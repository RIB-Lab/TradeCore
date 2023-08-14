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
    PEBBLE(new TCTool(Component.text("小石"), Material.PAPER, "pebble", 1, TCTool.ToolType.AXE, 0)),
    HATCHET(new TCTool(Component.text("ハチェット"), Material.IRON_AXE, "hatchet", 1, TCTool.ToolType.AXE, 1)),
    STICK(new TCItem(Component.text("木の棒"), Material.STICK, "stick", 0));
    
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
