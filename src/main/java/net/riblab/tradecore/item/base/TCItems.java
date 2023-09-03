package net.riblab.tradecore.item.base;


import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.dungeon.DungeonNames;
import net.riblab.tradecore.general.NBTTagNames;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.impl.*;
import net.riblab.tradecore.item.mod.*;
import net.riblab.tradecore.entity.mob.TCMobs;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * アイテムレジストリ
 */
public enum TCItems {
    //原始時代
    PEBBLE(new TCTool(Component.text("小石"), Material.PAPER, "pebble", 1, TCTool.ToolType.AXE, 0, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.PEBBLE),new ModSellPriceI(0.01d), new ModDefaultMaxDurabilityI(DurabilityTable.INFINITE)))),
    HATCHET(new TCTool(Component.text("ハチェット"), Material.IRON_AXE, "hatchet", 1, TCTool.ToolType.AXE, 1, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.WOODMADE), new ModDefaultMaxDurabilityI(DurabilityTable.HATCHET)))),
    STICK(new TCItem(Component.text("木の棒"), Material.STICK, "stick", 0, List.of(new ModSellPriceI(0.02d)))),
    BARK(new TCItem(Component.text("樹皮"), Material.OAK_LOG, "bark", 1, List.of(new ModSellPriceI(0.02d)))),
    TWIG(new TCItem(Component.text("小枝"), Material.OAK_LOG, "twig", 3, List.of(new ModSellPriceI(0.02d)))),
    ROUND_TRUNK(new TCItem(Component.text("丸太"), Material.OAK_LOG, "round_trunk", 2, List.of(new ModSellPriceI(0.04d)))),
    DRYGRASS(new TCItem(Component.text("干し草"), Material.PAPER, "drygrass", 2, List.of(new ModSellPriceI(0.01d)))),

    //原木時代
    WOODEN_AXE(new TCTool(Component.text("木の斧"), Material.WOODEN_AXE, "wooden_axe", 0, TCTool.ToolType.AXE, 2, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.WOODMADE), new ModEcologyI(1), new ModDefaultMaxDurabilityI(DurabilityTable.WOODENAGE)))),
    WOODEN_SHOVEL(new TCTool(Component.text("木のシャベル"), Material.WOODEN_SHOVEL, "wooden_shovel", 0, TCTool.ToolType.SHOVEL, 0, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.WOODMADE), new ModEcologyI(1), new ModDefaultMaxDurabilityI(DurabilityTable.WOODENAGE)))),
    WOODEN_PICKAXE(new TCTool(Component.text("木のツルハシ"), Material.WOODEN_PICKAXE, "wooden_pickaxe", 0, TCTool.ToolType.PICKAXE, 0, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.WOODMADE), new ModEcologyI(1), new ModDefaultMaxDurabilityI(DurabilityTable.WOODENAGE)))),
    WOODEN_HOE(new TCTool(Component.text("木のクワ"), Material.WOODEN_HOE, "wooden_hoe", 0, TCTool.ToolType.HOE, 0, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.WOODMADE), new ModEcologyI(1), new ModDefaultMaxDurabilityI(DurabilityTable.WOODENAGE)))),
    WOODEN_SWORD(new TCWeapon(Component.text("木の剣"), Material.WOODEN_SWORD, "wooden_sword", 0, List.of(new ModEcologyI(1), new ModDefaultMaxDurabilityI(DurabilityTable.WOODENAGE)), new WeaponAttributeSword(3), AttackDamageSpread.WOOD)),
    COIN(new TCItem(Component.text("工費："), Material.GOLD_INGOT, "coin", 1, new ArrayList<>())),
    DUST(new TCItem(Component.text("塵"), Material.DIRT, "dust", 1, List.of(new ModSellPriceI(0.04d)))),
    MUD(new TCItem(Component.text("泥"), Material.DIRT, "mud", 2, List.of(new ModSellPriceI(0.04d)))),
    WIDESTONE(new TCItem(Component.text("横長の石"), Material.COBBLESTONE, "widestone", 1, List.of(new ModSellPriceI(0.04d)))),
    TALLSTONE(new TCItem(Component.text("縦長の石"), Material.COBBLESTONE, "tallstone", 2, List.of(new ModSellPriceI(0.04d)))),
    WOODPULP(new TCItem(Component.text("木くず"), Material.OAK_PLANKS, "woodpulp", 1, List.of(new ModSellPriceI(0.04d)))),
    MOSS(new TCItem(Component.text("コケ"), Material.DIRT, "moss", 3, List.of(new ModSellPriceI(0.04d)))),
    WOODEN_COMPONENT(new TCItem(Component.text("木の強化資材"), Material.OAK_PLANKS, "wooden_component", 2, List.of(new ModSellPriceI(0.10d)))),
    NEXT_PAGE(new TCItem(Component.text("次のページ"), Material.ARROW, "nextpage", 1, List.of())),
    PREVIOUS_PAGE(new TCItem(Component.text("前のページ"), Material.ARROW, "previouspage", 2, List.of())),
    BARK_HELMET(new TCItem(Component.text("樹皮のヘルメット"), Material.CHAINMAIL_HELMET, "bark_helmet", 0, List.of(new ModCustomArmor("bark"), new ModAddArmorI(8), new ModDefaultMaxDurabilityI(DurabilityTable.WOODENAGE)))),
    BARK_CHESTPLATE(new TCItem(Component.text("樹皮のチェストプレート"), Material.CHAINMAIL_CHESTPLATE, "bark_chestplate", 0, List.of(new ModCustomArmor("bark"),new ModAddArmorI(8), new ModDefaultMaxDurabilityI(DurabilityTable.WOODENAGE)))),
    BARK_LEGGINGS(new TCItem(Component.text("樹皮のレギンス"), Material.CHAINMAIL_LEGGINGS, "bark_leggings", 0, List.of(new ModCustomArmor("bark"), new ModAddArmorI(8), new ModDefaultMaxDurabilityI(DurabilityTable.WOODENAGE)))),
    BARK_BOOTS(new TCItem(Component.text("樹皮のブーツ"), Material.CHAINMAIL_BOOTS, "bark_boots", 0, List.of(new ModCustomArmor("bark"), new ModAddArmorI(8),new ModDefaultMaxDurabilityI(DurabilityTable.WOODENAGE)))),
    WALKING_STICK(new TCItem(Component.text("転ばぬ先の杖"), Material.STICK, "walking_stick", 0, List.of(new ModWalkSpeedI(4)))),
    VANILLA_PLANK(new TCItem(Component.text("木材"), Material.OAK_PLANKS, "vanilla_plank", 0, List.of(new ModPlaceableI(true)))),
    VANILLA_CHEST(new TCItem(Component.text("チェスト"), Material.CHEST, "vanilla_plank", 0, List.of(new ModPlaceableI(true)))),

    //石器時代
    ROUND_STONE(new TCItem(Component.text("丸い石"), Material.COBBLESTONE, "round_stone", 6, List.of(new ModSellPriceI(0.10d)))),
    STONE_SWORD(new TCWeapon(Component.text("石の剣"), Material.STONE_SWORD, "stone_sword", 0, List.of(new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)), new WeaponAttributeSword(5), AttackDamageSpread.STONE)),
    STONE_SPEAR(new TCWeapon(Component.text("石の槍"), Material.STONE_SWORD, "stone_spear", 1, List.of(new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)), new WeaponAttributeSpear(3.5), AttackDamageSpread.STONE)),
    STONE_DAGGER(new TCWeapon(Component.text("石の短剣"), Material.STONE_SWORD, "stone_dagger", 2, List.of(new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)), new WeaponAttributeDagger(4), AttackDamageSpread.STONE)),
    STONE_BATTLEAXE(new TCWeapon(Component.text("石の大斧"), Material.STONE_SWORD, "stone_battleaxe", 3, List.of(new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)), new WeaponAttributeBattleAxe(8), AttackDamageSpread.STONE)),
    FUEL_BALL(new TCItem(Component.text("燃料玉"), Material.HAY_BLOCK, "fuel_ball", 1, List.of(new ModSellPriceI(0.10d)))),
    STONE_HELMET(new TCItem(Component.text("石のヘルメット"), Material.CHAINMAIL_HELMET, "stone_helmet", 0, List.of(new ModCustomArmor("stone"), new ModAddArmorI(16), new ModWalkSpeedI(-1), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    STONE_CHESTPLATE(new TCItem(Component.text("石のチェストプレート"), Material.CHAINMAIL_CHESTPLATE, "stone_chestplate", 0, List.of(new ModCustomArmor("stone"), new ModAddArmorI(16), new ModWalkSpeedI(-1), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    STONE_LEGGINGS(new TCItem(Component.text("石のレギンス"), Material.CHAINMAIL_LEGGINGS, "stone_leggings", 0, List.of(new ModCustomArmor("stone"), new ModAddArmorI(16), new ModWalkSpeedI(-1), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    STONE_BOOTS(new TCItem(Component.text("石のブーツ"), Material.CHAINMAIL_BOOTS, "stone_boots", 0, List.of(new ModCustomArmor("stone"), new ModAddArmorI(16), new ModWalkSpeedI(-1), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    ANDESITE_STONE(new TCItem(Component.text("安山石"), Material.COBBLESTONE, "andesite_stone", 3, List.of(new ModSellPriceI(0.10d)))),
    GRANITE_STONE(new TCItem(Component.text("花崗石"), Material.COBBLESTONE, "granite_stone", 4, List.of(new ModSellPriceI(0.10d)))),
    DIORITE_STONE(new TCItem(Component.text("閃緑石"), Material.COBBLESTONE, "diorite_stone", 5, List.of(new ModSellPriceI(0.10d)))),
    STONE_COMPONENT(new TCItem(Component.text("石の強化資材"), Material.COBBLESTONE, "stone_component", 7, List.of(new ModSellPriceI(0.20d)))),
    GRAVEL_DUST(new TCItem(Component.text("砂利の粉"), Material.GRAVEL, "gravel_dust", 1, List.of(new ModSellPriceI(0.10d)))),
    FLINT(new TCItem(Component.text("火打石"), Material.FLINT, "flint", 0, List.of(new ModSellPriceI(0.10d)))),
    METEORIC_IRON_ORE(new TCItem(Component.text("隕鉄鉱石"), Material.IRON_ORE, "meteoric_iron_ore", 1, List.of(new ModSellPriceI(0.10d)))),
    SAND_DUST(new TCItem(Component.text("粉状の砂"), Material.SAND, "sand_dust", 1, List.of(new ModSellPriceI(0.10d)))),
    SANDGOLD(new TCItem(Component.text("砂金"), Material.SAND, "sandgold", 2, List.of(new ModSellPriceI(0.10d)))),
    ASH(new TCItem(Component.text("灰"), Material.SAND, "ash", 3, List.of(new ModSellPriceI(0.10d)))),
    IRON_SHARD(new TCItem(Component.text("鉄の欠片"), Material.IRON_NUGGET, "iron_shard", 0, List.of(new ModSellPriceI(0.10d)))),
    GOLD_SHARD(new TCItem(Component.text("金の欠片"), Material.GOLD_NUGGET, "gold_shard", 0, List.of(new ModSellPriceI(0.10d)))),
    IRON_INGOT(new TCItem(Component.text("鉄インゴット"), Material.IRON_INGOT, "iron_ingot", 0, List.of(new ModSellPriceI(0.10d)))),
    GOLD_INGOT(new TCItem(Component.text("金インゴット"), Material.GOLD_INGOT, "gold_ingot", 0, List.of(new ModSellPriceI(0.10d)))),
    MAP_STONEROOM(new TCItem(Component.text("石の部屋のマップ"), Material.STONE_BRICKS, "map_stoneroom", 0, List.of(new ModEnterDungeonI(DungeonNames.STONEROOM)))),
    FURNACE(new TCItem(Component.text("かまど"), Material.FURNACE, "vanilla_furnace", 0, List.of(new ModPlaceableI(true)))),

    //鉄器時代
    REINFORCED_STICK(new TCItem(Component.text("強化棒"), Material.STICK, "reinforced_stick", 0, List.of(new ModSellPriceI(0.10d)))),
    REINFORCED_WALKING_STICK(new TCItem(Component.text("強化歩行杖"), Material.STICK, "reinforced _walking_stick", 0, List.of(new ModWalkSpeedI(6)))),
    BASIC_BOW(new TCWeapon(Component.text("普通の弓"), Material.BOW, "basic_bow", 0, List.of(new ModDefaultMaxDurabilityI(DurabilityTable.IRONAGE)), new WeaponAttributeBow(1, 4), AttackDamageSpread.IRON)),

    //店売り限定
    EMERALD_HELMET(new TCItem(Component.text("エメラルドのヘルメット"), Material.CHAINMAIL_HELMET, "emerald_helmet", 0, List.of(new ModCustomArmor("emerald"), new ModReduceCraftCostI(3), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    EMERALD_CHESTPLATE(new TCItem(Component.text("エメラルドのチェストプレート"), Material.CHAINMAIL_CHESTPLATE, "emerald_chestplate", 0, List.of(new ModCustomArmor("emerald"), new ModReduceCraftCostI(3), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    EMERALD_LEGGINGS(new TCItem(Component.text("エメラルドのレギンス"), Material.CHAINMAIL_LEGGINGS, "emerald_leggings", 0, List.of(new ModCustomArmor("emerald"), new ModReduceCraftCostI(3), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    EMERALD_BOOTS(new TCItem(Component.text("エメラルドのブーツ"), Material.CHAINMAIL_BOOTS, "emerald_boots", 0, List.of(new ModCustomArmor("emerald"), new ModReduceCraftCostI(3), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    WORKER_HELMET(new TCItem(Component.text("作業ヘルメット"), Material.CHAINMAIL_HELMET, "worker_helmet", 0, List.of(new ModCustomArmor("template"), new ModZeroHandAttackDamageI(-1), new ModWalkSpeedI(-10), new ModResouceChanceI(3), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    WORKER_CHESTPLATE(new TCItem(Component.text("作業チェストプレート"), Material.CHAINMAIL_CHESTPLATE, "worker_chestplate", 0, List.of(new ModCustomArmor("template"), new ModZeroHandAttackDamageI(-1), new ModWalkSpeedI(-10), new ModResouceChanceI(3), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    WORKER_LEGGINGS(new TCItem(Component.text("作業レギンス"), Material.CHAINMAIL_LEGGINGS, "worker_leggings", 0, List.of(new ModCustomArmor("template"), new ModZeroHandAttackDamageI(-1), new ModWalkSpeedI(-10), new ModResouceChanceI(3), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    WORKER_BOOTS(new TCItem(Component.text("作業ブーツ"), Material.CHAINMAIL_BOOTS, "worker_boots", 0, List.of(new ModCustomArmor("template"), new ModZeroHandAttackDamageI(-1), new ModWalkSpeedI(-10), new ModResouceChanceI(3), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    WATER_HELMET(new TCItem(Component.text("水気を帯びたヘルメット"), Material.CHAINMAIL_HELMET, "water_helmet", 0, List.of(new ModCustomArmor("template"), new ModWaterBreathI(1), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),

    //その他
    DESTRUCTORS_WAND(new TCItem(Component.text("メインワールド高速破壊杖"), Material.STICK, "destructors_wand", 1, List.of())),
    MESI((new TCItem(Component.text("COMP"), Material.COOKED_BEEF, "meshi", 0, List.of()))),

    //モブ召喚系は他のアイテムを参照するので必ず最後に配置
    STONE_AXE(new TCTool(Component.text("石の斧"), Material.STONE_AXE, "stone_axe", 0, TCTool.ToolType.AXE, 3, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.STONEMADE),new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE), new ModMonsterSpawnI(Map.of(TCMobs.BASIC_TREANT.get(), 0.05f))))),
    STONE_SHOVEL(new TCTool(Component.text("石のシャベル"), Material.STONE_SHOVEL, "stone_shovel", 0, TCTool.ToolType.SHOVEL, 1, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.STONEMADE), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    STONE_PICKAXE(new TCTool(Component.text("石のツルハシ"), Material.STONE_PICKAXE, "stone_pickaxe", 0, TCTool.ToolType.PICKAXE, 1, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.STONEMADE), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE),new ModMonsterSpawnI(Map.of(TCMobs.BASIC_SILVERFISH.get(), 0.01f))))),
    STONE_HOE(new TCTool(Component.text("石のクワ"), Material.STONE_HOE, "stone_hoe", 0, TCTool.ToolType.HOE, 1, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.STONEMADE), new ModDefaultMaxDurabilityI(DurabilityTable.STONEAGE)))),
    IRON_AXE(new TCTool(Component.text("鉄の斧"), Material.IRON_AXE, "iron_axe", 0, TCTool.ToolType.AXE, 4, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.IRONMADE), new ModDefaultMaxDurabilityI(DurabilityTable.IRONAGE), new ModMonsterSpawnI(Map.of(TCMobs.BASIC_TREANT.get(), 0.05f))))),
    IRON_SHOVEL(new TCTool(Component.text("鉄のシャベル"), Material.IRON_SHOVEL, "iron_shovel", 0, TCTool.ToolType.SHOVEL, 2, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.IRONMADE), new ModDefaultMaxDurabilityI(DurabilityTable.IRONAGE)))),
    IRON_PICKAXE(new TCTool(Component.text("鉄のツルハシ"), Material.IRON_PICKAXE, "iron_pickaxe", 0, TCTool.ToolType.PICKAXE, 2, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.IRONMADE), new ModDefaultMaxDurabilityI(DurabilityTable.IRONAGE), new ModMonsterSpawnI(Map.of(TCMobs.BASIC_SILVERFISH.get(), 0.01f))))),
    IRON_HOE(new TCTool(Component.text("鉄のクワ"), Material.IRON_HOE, "iron_hoe", 0, TCTool.ToolType.HOE, 2, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.IRONMADE), new ModDefaultMaxDurabilityI(DurabilityTable.IRONAGE)))),
    GOLDEN_AXE(new TCTool(Component.text("金の斧"), Material.GOLDEN_AXE, "golden_axe", 0, TCTool.ToolType.AXE, 4, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.GOLDMADE), new ModDefaultMaxDurabilityI(DurabilityTable.IRONAGE), new ModMonsterSpawnI(Map.of(TCMobs.BASIC_TREANT.get(), 0.05f))))),
    GOLDEN_SHOVEL(new TCTool(Component.text("金のシャベル"), Material.GOLDEN_SHOVEL, "golden_shovel", 0, TCTool.ToolType.SHOVEL, 2, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.IRONMADE), new ModDefaultMaxDurabilityI(DurabilityTable.IRONAGE)))),
    GOLDEN_PICKAXE(new TCTool(Component.text("金のツルハシ"), Material.GOLDEN_PICKAXE, "golden_pickaxe", 0, TCTool.ToolType.PICKAXE, 2, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.IRONMADE), new ModDefaultMaxDurabilityI(DurabilityTable.IRONAGE), new ModMonsterSpawnI(Map.of(TCMobs.BASIC_SILVERFISH.get(), 0.01f))))),
    GOLDEN_HOE(new TCTool(Component.text("金のクワ"), Material.GOLDEN_HOE, "golden_hoe", 0, TCTool.ToolType.HOE, 2, List.of(new ModDefaultMiningSpeedI(MiningSpeedTable.IRONMADE), new ModDefaultMaxDurabilityI(DurabilityTable.IRONAGE))));

    private final ITCItem tcItem;

    TCItems(ITCItem tcItem) {
        this.tcItem = tcItem;
    }

    public ITCItem get() {
        return tcItem;
    }

    /**
     * アイテムが固有アイテムであった場合その実体を固有アイテムクラスに変換する<br>
     * この際実体特有のNBTなどは失われる
     *
     * @param itemStack 変換したいアイテム
     * @return 変換された固有アイテム
     */
    @Nullable
    public static ITCItem toTCItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return null;

        String id = new ItemCreator(itemStack).getStrNBT(NBTTagNames.ITEMID.get());
        TCItems itcItem = Arrays.stream(TCItems.values()).filter(e -> e.get().isSimilar(id)).findFirst().orElse(null);
        return itcItem == null ? null : itcItem.get();
    }

    /**
     * 固有アイテムの召喚コマンドを固有アイテムに変換する
     *
     * @param command 召喚コマンド
     * @return 変換された固有アイテム
     */
    @Nullable
    public static ITCItem commandToTCItem(String command) {
        TCItems itcItem = Arrays.stream(TCItems.values()).filter(e -> e.get().isSimilar(command)).findFirst().orElse(null);
        return itcItem == null ? null : itcItem.get();
    }

    // Function that returns our custom argument
    public static Argument<ITCItem> customITCItemArgument(String nodeName) {

        // Construct our CustomArgument that takes in a String input and returns a World object
        return new CustomArgument<>(new StringArgument(nodeName), info -> {
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
