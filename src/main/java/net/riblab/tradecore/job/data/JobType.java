package net.riblab.tradecore.job.data;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.Getter;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.Arrays;

public enum JobType {
    Miner("鉱夫", Material.IRON_PICKAXE),
    Digger("整地師", Material.IRON_SHOVEL),
    Woodcutter("木こり", Material.IRON_AXE),
    Mower("草刈り機", Material.GRASS),
    Crafter("クラフター", Material.CRAFTING_TABLE);

    private final String name;
    @Getter
    private final Material uiMaterial;

    JobType(String name, Material uiMaterial) {
        this.name = name;
        this.uiMaterial = uiMaterial;
    }

    public String getName() {
        return name;
    }

    // Function that returns our custom argument
    public static Argument<JobType> customJobTypeArgument(String nodeName) {

        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            JobType type = commandToJOBType(info.input());

            if (type == null) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown job: ").appendArgInput());
            } else {
                return type;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(values()).map(Enum::toString).toArray(String[]::new))
        );
    }

    /**
     * コマンド文字列をジョブに変換する
     */
    @Nullable
    public static JobType commandToJOBType(String command) {
        return Arrays.stream(JobType.values()).filter(e -> e.toString().equals(command)).findFirst().orElse(null);
    }
}
