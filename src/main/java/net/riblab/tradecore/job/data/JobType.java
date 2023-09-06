/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.job.data;

import lombok.Getter;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.Arrays;

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

    /**
     * コマンド文字列をジョブに変換する
     */
    @Nullable
    public static JobType commandToJOBType(String command) {
        return Arrays.stream(JobType.values()).filter(e -> e.toString().equals(command)).findFirst().orElse(null);
    }
}
