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
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItemRegistry;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.shop.IShopData;
import net.riblab.tradecore.shop.Shops;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class CustomEnumArguments {

    public static Argument<ITCItem> customITCItemArgument(String nodeName) {

        return new CustomArgument<>(new StringArgument(nodeName),
                info -> TCItems.commandToTCItem(info.input())
                        .orElseThrow(() -> CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown item: ").appendArgInput()))).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(TCItems.values()).map(tcItems -> tcItems.get().getInternalName()).toArray(String[]::new))
        );
    }

    public static Argument<JobType> customJobTypeArgument(String nodeName) {

        return new CustomArgument<>(new StringArgument(nodeName),
                info -> JobType.commandToJOBType(info.input())
                        .orElseThrow(() -> CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown job: ").appendArgInput()))).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(JobType.values()).map(Enum::toString).toArray(String[]::new))
        );
    }

    public static Argument<IDungeonData<?>> customDungeonDataArgument(@Nonnull String nodeName) {
        return new CustomArgument<IDungeonData<?>, String>(new StringArgument(nodeName),
                info -> DungeonDatas.commandToDungeonData(info.input())
                        .orElseThrow(() -> CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown dungeon: ").appendArgInput()))).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(DungeonDatas.values()).map(Enum::toString).toArray(String[]::new))
        );
    }

    public static Argument<ITCMob> customITCMobArgument(String nodeName) {

        return new CustomArgument<>(new StringArgument(nodeName),
                info -> TCMobs.commandToTCMob(info.input())
                        .orElseThrow(() -> CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown mob: ").appendArgInput()))).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(TCMobs.values()).map(tcMobs -> tcMobs.get().getInternalName()).toArray(String[]::new))
        );
    }

    public static Argument<IShopData> customShopDataArgument(String nodeName) {

        return new CustomArgument<>(new StringArgument(nodeName), info ->
                Shops.commandToShop(info.input()).orElseThrow(() -> CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown shop: ").appendArgInput()))).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(Shops.values()).map(Enum::toString).toArray(String[]::new))
        );
    }

    public static Argument<ITCItem> customNewITCItemArgument(String nodeName) {
        return new CustomArgument<>(new StringArgument(nodeName),
                info -> TCItemRegistry.INSTANCE.commandToTCItem(info.input())
                        .orElseThrow(() -> CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown item: ").appendArgInput())))
                .replaceSuggestions(ArgumentSuggestions.strings(info ->
                        TCItemRegistry.INSTANCE.getItems().stream().map(ITCItem::getInternalName).toArray(String[]::new))
                );
    }
}
