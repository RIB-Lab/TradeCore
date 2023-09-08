/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.integration;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.riblab.tradecore.dungeon.DungeonDatas;
import net.riblab.tradecore.dungeon.IDungeonData;
import net.riblab.tradecore.entity.mob.ITCMob;
import net.riblab.tradecore.entity.mob.TCMobs;
import net.riblab.tradecore.general.ErrorMessages;
import net.riblab.tradecore.item.MaterialSetRegistry;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItemRegistry;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.shop.IShopData;
import net.riblab.tradecore.shop.Shops;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Set;

/**
 * Enumの要素をコマンド引数にするためのユーティリティ
 */
public class CustomEnumArgumentsUtil {

    private CustomEnumArgumentsUtil(){
        throw new AssertionError();
    }

    /**
     * ジョブの種類をコマンド引数にする
     */
    public static Argument<JobType> customJobTypeArgument(String nodeName) {

        return new CustomArgument<>(new StringArgument(nodeName),
                info -> JobType.commandToJOBType(info.input())
                        .orElseThrow(() -> CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder(ErrorMessages.INVALID_ARGUMENT.get()).appendArgInput()))).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(JobType.values()).map(Enum::toString).toArray(String[]::new))
        );
    }

    /**
     * ダンジョンデータをコマンド引数にする
     */
    public static Argument<IDungeonData<?>> customDungeonDataArgument(@Nonnull String nodeName) {
        return new CustomArgument<IDungeonData<?>, String>(new StringArgument(nodeName),
                info -> DungeonDatas.commandToDungeonData(info.input())
                        .orElseThrow(() -> CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder(ErrorMessages.INVALID_ARGUMENT.get()).appendArgInput()))).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(DungeonDatas.values()).map(Enum::toString).toArray(String[]::new))
        );
    }

    /**
     * モブデータを引数にする
     */
    public static Argument<ITCMob> customITCMobArgument(String nodeName) {

        return new CustomArgument<>(new StringArgument(nodeName),
                info -> TCMobs.commandToTCMob(info.input())
                        .orElseThrow(() -> CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder(ErrorMessages.INVALID_ARGUMENT.get()).appendArgInput()))).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(TCMobs.values()).map(tcMobs -> tcMobs.get().getInternalName()).toArray(String[]::new))
        );
    }

    /**
     * ショップデータをコマンド引数にする
     */
    public static Argument<IShopData> customShopDataArgument(String nodeName) {

        return new CustomArgument<>(new StringArgument(nodeName), info ->
                Shops.commandToShop(info.input()).orElseThrow(() -> CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder(ErrorMessages.INVALID_ARGUMENT.get()).appendArgInput()))).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(Shops.values()).map(Enum::toString).toArray(String[]::new))
        );
    }

    /**
     * ItemRegistry内のアイテムをコマンド引数にする
     */
    public static Argument<ITCItem> customTCItemArgument(String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName),
                info -> TCItemRegistry.INSTANCE.commandToTCItem(info.input())
                        .orElseThrow(() -> CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder(ErrorMessages.INVALID_ARGUMENT.get()).appendArgInput())))
                .replaceSuggestions(ArgumentSuggestions.strings(info ->
                        TCItemRegistry.INSTANCE.getItems().keySet().toArray(new String[0]))
                );
    }

    /**
     * MaterialSetRegistry内のmaterials名をコマンド引数にする
     */
    public static Argument<Set<Material>> customMaterialSetArgument(String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName),
                info -> MaterialSetRegistry.INSTANCE.commandToMaterialSet(info.input())
                        .orElseThrow(() -> CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder(ErrorMessages.INVALID_ARGUMENT.get()).appendArgInput())))
                .replaceSuggestions(ArgumentSuggestions.strings(info ->
                        MaterialSetRegistry.INSTANCE.getItems().keySet().toArray(String[]::new))
                );
    }
}
