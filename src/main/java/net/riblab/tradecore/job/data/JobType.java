package net.riblab.tradecore.job.data;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.Getter;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public enum JobType {
    MINER("鉱夫", Material.IRON_PICKAXE),
    DIGGER("整地師", Material.IRON_SHOVEL),
    WOODCUTTER("木こり", Material.IRON_AXE),
    MOWER("草刈り機", Material.GRASS),
    CRAFTER("クラフター", Material.CRAFTING_TABLE);

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

            if (Objects.isNull(type)) {
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
