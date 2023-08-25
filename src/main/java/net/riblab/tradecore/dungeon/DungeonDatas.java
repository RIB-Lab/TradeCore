package net.riblab.tradecore.dungeon;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.Getter;
import net.riblab.tradecore.ShopData;
import net.riblab.tradecore.mob.TCMobs;
import net.riblab.tradecore.mob.Treant;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public enum DungeonDatas {
    TEST(new DungeonData("test", new Vector(-17, 97, -24), List.of(TCMobs.BASIC_TREANT.get(), TCMobs.BASIC_SILVERFISH.get()), 3));
    
    @Getter
    private final DungeonData data;

    DungeonDatas(DungeonData data) {
        this.data = data;
    }

    @Nullable
    public static DungeonData commandToDungeonData(String command) {
        DungeonDatas datas = Arrays.stream(DungeonDatas.values()).filter(e -> e.toString().equals(command)).findFirst().orElse(null);
        return datas == null ? null : datas.getData();
    }
    
    public static Argument<DungeonData> customDungeonDataArgument(String nodeName) {
        return new CustomArgument<DungeonData, String>(new StringArgument(nodeName), info -> {
            DungeonData data = commandToDungeonData(info.input());

            if (data == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown dungeon: ").appendArgInput());
            } else {
                return data;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(values()).map(dungeons -> dungeons.toString()).toArray(String[]::new))
        );
    }

    @Nullable
    public static DungeonData nameToDungeonData(String name) {
        DungeonDatas datas = Arrays.stream(DungeonDatas.values()).filter(e -> e.getData().getName().equals(name)).findFirst().orElse(null);
        return datas == null ? null : datas.getData();
    }
}
