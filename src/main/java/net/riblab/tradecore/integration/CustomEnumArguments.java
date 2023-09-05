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
import java.util.Objects;

public class CustomEnumArguments {

    // Function that returns our custom argument
    public static Argument<ITCItem> customITCItemArgument(String nodeName) {

        // Construct our CustomArgument that takes in a String input and returns a World object
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            // Parse the itcItem from our input
            ITCItem itcItem = TCItems.commandToTCItem(info.input());

            if (Objects.isNull(itcItem)) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown item: ").appendArgInput());
            } else {
                return itcItem;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(TCItems.values()).map(tcItems -> tcItems.get().getInternalName()).toArray(String[]::new))
        );
    }

    // Function that returns our custom argument
    public static Argument<JobType> customJobTypeArgument(String nodeName) {

        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            JobType type = JobType.commandToJOBType(info.input());

            if (Objects.isNull(type)) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown job: ").appendArgInput());
            } else {
                return type;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(JobType.values()).map(Enum::toString).toArray(String[]::new))
        );
    }

    public static Argument<IDungeonData<?>> customDungeonDataArgument(@Nonnull String nodeName) {
        return new CustomArgument<IDungeonData<?>, String>(new StringArgument(nodeName), info -> {
            IDungeonData<?> data = DungeonDatas.commandToDungeonData(info.input());

            if (Objects.isNull(data)) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown dungeon: ").appendArgInput());
            } else {
                return data;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                Arrays.stream(DungeonDatas.values()).map(Enum::toString).toArray(String[]::new))
        );
    }

    // Function that returns our custom argument
    public static Argument<ITCMob> customITCMobArgument(String nodeName) {

        // Construct our CustomArgument that takes in a String input and returns a World object
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            // Parse the itcMob from our input
            ITCMob itcMob = TCMobs.commandToTCMob(info.input());

            if (Objects.isNull(itcMob)) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown mob: ").appendArgInput());
            } else {
                return itcMob;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                // List of world names on the server
                Arrays.stream(TCMobs.values()).map(tcMobs -> tcMobs.get().getInternalName()).toArray(String[]::new))
        );
    }

    // Function that returns our custom argument
    public static Argument<IShopData> customShopDataArgument(String nodeName) {

        // Construct our CustomArgument that takes in a String input and returns a World object
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            // Parse the data from our input
            IShopData data = Shops.commandToShop(info.input());

            if (Objects.isNull(data)) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown shop: ").appendArgInput());
            } else {
                return data;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                // List of shops on the server
                Arrays.stream(Shops.values()).map(Enum::toString).toArray(String[]::new))
        );
    }

    public static Argument<ITCItem> customNewITCItemArgument(String nodeName) {
        // Construct our CustomArgument that takes in a String input and returns a World object
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
            // Parse the itcItem from our input
            ITCItem itcItem = TCItemRegistry.commandToTCItem(info.input());

            if (Objects.isNull(itcItem)) {
                throw CustomArgument.CustomArgumentException.fromMessageBuilder(new CustomArgument.MessageBuilder("Unknown item: ").appendArgInput());
            } else {
                return itcItem;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                TCItemRegistry.getDeserializedItems().stream().map(ITCItem::getInternalName).toArray(String[]::new))
        );
    }
}
