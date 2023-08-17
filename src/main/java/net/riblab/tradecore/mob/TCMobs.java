package net.riblab.tradecore.mob;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.item.TCItems;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

/**
 * カスタムモブの定義一覧
 */
public enum TCMobs {
    BASIC_SILVERFISH(new TCMob(EntityType.SILVERFISH, Component.text("ふぃっしゅ数ver1"), 12, "basic_silverfish", Map.of(TCItems.ROUND_STONE.get().getItemStack(), 1f))),
    BASIC_TREANT(new Treant());

    private final TCMob tcMob;

    TCMobs(TCMob tcMob) {
        this.tcMob = tcMob;
    }

    public TCMob get() {
        return tcMob;
    }

    /**
     * モブをカスタムモブに変換する
     */
    @Nullable
    public static TCMob toTCMob(Mob mob) {
        TCMobs itcMob = Arrays.stream(TCMobs.values()).filter(e -> e.get().isSimilar(mob)).findFirst().orElse(null);
        return itcMob == null ? null : itcMob.get();
    }

    /**
     * コマンド文字列をカスタムモブに変換する
     */
    @Nullable
    public static TCMob commandToTCMob(String command) {
        TCMobs itcMob = Arrays.stream(TCMobs.values()).filter(e -> e.get().getInternalName().equals(command)).findFirst().orElse(null);
        return itcMob == null ? null : itcMob.get();
    }

    // Function that returns our custom argument
    public static Argument<TCMob> customITCMobArgument(String nodeName) {

        // Construct our CustomArgument that takes in a String input and returns a World object
        return new CustomArgument<TCMob, String>(new StringArgument(nodeName), info -> {
            // Parse the itcMob from our input
            TCMob itcMob = commandToTCMob(info.input());

            if (itcMob == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown mob: ").appendArgInput());
            } else {
                return itcMob;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                // List of world names on the server
                Arrays.stream(values()).map(tcMobs -> tcMobs.get().getInternalName()).toArray(String[]::new))
        );
    }
}