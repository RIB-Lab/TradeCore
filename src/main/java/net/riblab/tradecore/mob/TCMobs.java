package net.riblab.tradecore.mob;

import de.tr7zw.nbtapi.NBTEntity;
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

    private final ITCMob ITCMob;

    TCMobs(ITCMob ITCMob) {
        this.ITCMob = ITCMob;
    }

    public ITCMob get() {
        return ITCMob;
    }

    /**
     * モブをカスタムモブに変換する
     */
    @Nullable
    public static ITCMob toTCMob(Mob mob) {
        if(mob == null)
            return null;

        NBTEntity nbtEntity = new NBTEntity(mob);
        String ID = nbtEntity.getPersistentDataContainer().getString("TCID");
        
        TCMobs itcMob = Arrays.stream(TCMobs.values()).filter(e -> e.get().getInternalName().equals(ID)).findFirst().orElse(null);
        return itcMob == null ? null : itcMob.get();
    }

    /**
     * コマンド文字列をカスタムモブに変換する
     */
    @Nullable
    public static ITCMob commandToTCMob(String command) {
        TCMobs itcMob = Arrays.stream(TCMobs.values()).filter(e -> e.get().getInternalName().equals(command)).findFirst().orElse(null);
        return itcMob == null ? null : itcMob.get();
    }

    // Function that returns our custom argument
    public static Argument<ITCMob> customITCMobArgument(String nodeName) {

        // Construct our CustomArgument that takes in a String input and returns a World object
        return new CustomArgument<ITCMob, String>(new StringArgument(nodeName), info -> {
            // Parse the itcMob from our input
            ITCMob itcMob = commandToTCMob(info.input());

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