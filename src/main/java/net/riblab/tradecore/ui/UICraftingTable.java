/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.ui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.craft.CraftingRecipesRegistry;
import net.riblab.tradecore.craft.ITCCraftingRecipe;
import net.riblab.tradecore.entity.mob.FakeVillagerService;
import net.riblab.tradecore.general.Utils;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.ItemUtils;
import net.riblab.tradecore.item.Materials;
import net.riblab.tradecore.item.base.TCItemRegistry;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.job.data.JobDataService;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.modifier.ICraftFeeModifier;
import net.riblab.tradecore.modifier.IIngredientAmountModifier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * 作業台UI
 */
final class UICraftingTable implements IUI {

    /**
     * レシピや材料を置くことが可能なスロット番号のリスト
     */
    private static final Set<Integer> allowedIngredientSlotSet = Set.of(0, 1, 2, 9, 10, 11, 18, 19, 20);

    @Override
    public BaseGui open(Player player) {
        return open(player, CraftingScreenType.CATEGORY);
    }

    /**
     * カテゴリやレシピ選択画面を開く
     */
    public static PaginatedGui open(Player player, CraftingScreenType type) {
        PaginatedGui gui = Gui.paginated()
                .title(type.getTitle())
                .rows(3)
                .disableAllInteractions()
                .create();

        if (type == CraftingScreenType.CATEGORY) {
            addCategoryScreen(gui, player);
        } else if (type == CraftingScreenType.CRAFTING) {
            throw new IllegalArgumentException();
        } else {
            addRecipeListScreen(type, gui, player);
        }

        //タイトルを強制的にcustomfontにする
        gui.setUpdating(true);
        gui.setInventory(Bukkit.createInventory(gui, gui.getInventory().getSize(), type.getTitle()));
        gui.setUpdating(false);

        gui.setCloseGuiAction(event -> {
            if (event.getReason() != InventoryCloseEvent.Reason.OPEN_NEW)
                FakeVillagerService.getImpl().tryDeSpawnFakeVillager(player);
        });

        gui.open(player);

        Location spawnLocation = player.getTargetBlock(Materials.TRANSPARENT.get(), 5).getRelative(0, 1, 0).getLocation().add(new Vector(0.5d, 0d, 0.5d));
        FakeVillagerService.getImpl().spawnFakeVillager(player, "職人", spawnLocation);
        return gui;
    }

    /**
     * クラフト確認画面を開く
     */
    public static PaginatedGui open(Player player, ITCCraftingRecipe recipe) {
        PaginatedGui gui = Gui.paginated()
                .title(CraftingScreenType.CRAFTING.getTitle())
                .rows(3)
                .disableAllInteractions()
                .create();

        gui.setUpdating(true);
        gui.setInventory(Bukkit.createInventory(gui, gui.getInventory().getSize(), CraftingScreenType.CRAFTING.getTitle()));
        gui.setUpdating(false);

        addCraftingScreen(gui, player, recipe);

        gui.setCloseGuiAction(event -> {
            if (event.getReason() != InventoryCloseEvent.Reason.OPEN_NEW)
                FakeVillagerService.getImpl().tryDeSpawnFakeVillager(player);
        });

        gui.open(player);

        Location spawnLocation = player.getTargetBlock(Materials.TRANSPARENT.get(), 5).getRelative(0, 1, 0).getLocation().add(new Vector(0.5d, 0d, 0.5d));
        FakeVillagerService.getImpl().spawnFakeVillager(player, "職人", spawnLocation);
        return gui;
    }

    /**
     * カテゴリ選択画面を実装
     */
    private static void addCategoryScreen(PaginatedGui gui, Player player) {
        ItemStack armorCategory = new ItemCreator(Material.IRON_CHESTPLATE).setName(Component.text("装備品").decoration(TextDecoration.ITALIC, false))
                .create();
        GuiItem armorButton = new GuiItem(armorCategory,
                event -> open(player, CraftingScreenType.ARMOR));
        gui.setItem(0, armorButton);

        ItemStack toolCategory = new ItemCreator(Material.IRON_PICKAXE).setName(Component.text("ツール").decoration(TextDecoration.ITALIC, false))
                .create();
        GuiItem toolButton = new GuiItem(toolCategory,
                event -> open(player, CraftingScreenType.TOOL));
        gui.setItem(1, toolButton);

        ItemStack weaponCategory = new ItemCreator(Material.IRON_SWORD).setName(Component.text("武器").decoration(TextDecoration.ITALIC, false))
                .create();
        GuiItem weaponButton = new GuiItem(weaponCategory,
                event -> open(player, CraftingScreenType.WEAPON));
        gui.setItem(2, weaponButton);

        ItemStack miscCategory = new ItemCreator(Material.FLOWER_POT).setName(Component.text("その他").decoration(TextDecoration.ITALIC, false))
                .create();
        GuiItem miscButton = new GuiItem(miscCategory,
                event -> open(player, CraftingScreenType.MISC));
        gui.setItem(9, miscButton);
    }

    /**
     * レシピリスト画面を実装
     */
    private static void addRecipeListScreen(CraftingScreenType type, PaginatedGui gui, Player player) {
        List<ITCCraftingRecipe> recipeList = CraftingRecipesRegistry.INSTANCE.getRecipes(type.getRecipeType());
        if (recipeList.isEmpty())
            return;

        gui.setPageSize(9);
        gui.getFiller().fillBetweenPoints(1, 4, 3, 9, ItemBuilder.from(Material.AIR).asGuiItem());

        GuiItem previousPageButton = new GuiItem(TCItems.PREVIOUS_PAGE.get().getTemplateItemStack(),
                event -> gui.previous());
        gui.setItem(22, previousPageButton);
        GuiItem nextPageButton = new GuiItem(TCItems.NEXT_PAGE.get().getTemplateItemStack(),
                event -> gui.next());
        gui.setItem(24, nextPageButton);

        for (ITCCraftingRecipe tcCraftingRecipe : recipeList) {
            ItemStack recipeStack = TCItemRegistry.INSTANCE.commandToTCItem(tcCraftingRecipe.getResult()).orElseThrow().getTemplateItemStack();
            GuiItem recipeButton = new GuiItem(recipeStack,
                    event -> open(player, tcCraftingRecipe));
            gui.addItem(recipeButton);
        }
    }

    /**
     * クラフト確認画面を実装
     */
    private static void addCraftingScreen(PaginatedGui gui, Player player, ITCCraftingRecipe recipe) {
        addIngredientStack(recipe, gui, player);

        addResultStack(recipe, gui, player);

        addFeeStack(recipe, gui, player);
    }

    /**
     * 作業台の画面にレシピの材料のItemStackを追加する
     */
    private static void addIngredientStack(ITCCraftingRecipe recipe, PaginatedGui gui, Player player) {
        int slot = 0;
        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
            ItemStack ingredientStack = TCItemRegistry.INSTANCE.commandToTCItem(entry.getKey()).orElseThrow().getTemplateItemStack();

            IIngredientAmountModifier.PackedRecipeData packedRecipeData = new IIngredientAmountModifier.PackedRecipeData();
            packedRecipeData.setRecipe(recipe);
            packedRecipeData.setAmount(entry.getValue());
            int amountSkillApplied = Utils.apply(player, packedRecipeData, IIngredientAmountModifier.class).getAmount();

            ingredientStack.setAmount(amountSkillApplied);
            GuiItem ingredientDisplay = new GuiItem(ingredientStack);
            gui.setItem(slot, ingredientDisplay);
            do {
                slot++;
            } while (!allowedIngredientSlotSet.contains(slot));
        }
    }

    /**
     * 作業台の画面にレシピの完成品のItemStackを追加する
     */
    private static void addResultStack(ITCCraftingRecipe recipe, PaginatedGui gui, Player player) {
        ItemStack resultStack = TCItemRegistry.INSTANCE.commandToTCItem(recipe.getResult()).orElseThrow().getTemplateItemStack();
        Component craftTip = Component.text("<<クリックで製作>>").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        resultStack = new ItemCreator(resultStack).addLore(craftTip).create();
        resultStack.setAmount(recipe.getResultAmount());
        ItemStack finalResultStack = resultStack;
        GuiItem craftButton = new GuiItem(resultStack, event -> tryCraft(gui, player, recipe, finalResultStack));
        gui.setItem(14, craftButton);
    }

    /**
     * 作業台の画面にレシピの必要料金を表示するItemStackを追加する
     */
    private static void addFeeStack(ITCCraftingRecipe recipe, PaginatedGui gui, Player player) {
        ICraftFeeModifier.PackedCraftFee packedCraftFee = new ICraftFeeModifier.PackedCraftFee();
        packedCraftFee.setRecipe(recipe);
        packedCraftFee.setFee(recipe.getFee());
        double skillAppliedFee = Utils.apply(player, packedCraftFee, ICraftFeeModifier.class).getFee();

        ItemStack feeStack = TCItemRegistry.INSTANCE.commandToTCItem("coin").orElseThrow().getTemplateItemStack();
        Component name = Component.text("工費: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(Math.floor(skillAppliedFee * 100) / 100).color(NamedTextColor.YELLOW));
        Component lore = Component.text("職人に報酬を支払います").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY);
        feeStack = new ItemCreator(feeStack).setName(name).setLore(lore).create();
        GuiItem feeDisplay = new GuiItem(feeStack);
        gui.setItem(23, feeDisplay);
    }

    /**
     * クラフトの決済処理を行う
     */
    private static void tryCraft(PaginatedGui gui, Player player, ITCCraftingRecipe recipe, ItemStack resultStack) {
        List<Component> missingLore = getMissingItemsMessages(player, recipe);

        Component missingBalanceText = getMissingBalanceMessage(player, recipe);
        if (Objects.nonNull(missingBalanceText))
            missingLore.add(missingBalanceText);

        if (missingLore.size() > 0) {
            ItemStack newResultStack = new ItemCreator(resultStack).setLores(missingLore).create();
            gui.updatePageItem(14, newResultStack);
            gui.update();
            return;
        }

        withdrawItems(player, recipe);

        withdrawBalance(player, recipe);

        JobDataService.getImpl().addJobExp(player, JobType.CRAFTER, (int) recipe.getFee());

        final ItemStack stackToGive = TCItemRegistry.INSTANCE.commandToTCItem(recipe.getResult()).orElseThrow().getItemStack();
        HashMap<Integer, ItemStack> remains = player.getInventory().addItem(stackToGive);
        if (remains.isEmpty())
            return;

        remains.forEach((integer, itemStack) -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
    }

    /**
     * あるレシピについて、プレイヤーがその材料を持っていなかった場合、その種類を表すメッセージを取得する
     */
    private static List<Component> getMissingItemsMessages(Player player, ITCCraftingRecipe recipe) {
        List<Component> missingMessages = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
            IIngredientAmountModifier.PackedRecipeData packedRecipeData = new IIngredientAmountModifier.PackedRecipeData();
            packedRecipeData.setRecipe(recipe);
            packedRecipeData.setAmount(entry.getValue());
            int amountSkillApplied = Utils.apply(player, packedRecipeData, IIngredientAmountModifier.class).getAmount();

            boolean playerHasItem = ItemUtils.tcContainsAtLeast(player.getInventory(), TCItemRegistry.INSTANCE.commandToTCItem(entry.getKey()).orElseThrow(), amountSkillApplied);
            if (playerHasItem)
                continue;

            missingMessages.add(Component.text(TCItemRegistry.INSTANCE.commandToTCItem(entry.getKey()).orElseThrow().getName().content() + "が足りません！").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }
        return missingMessages;
    }

    private static Component getMissingBalanceMessage(Player player, ITCCraftingRecipe recipe) {
        double balance = TCEconomy.getImpl().getBalance(player);
        ICraftFeeModifier.PackedCraftFee packedCraftFee = new ICraftFeeModifier.PackedCraftFee();
        packedCraftFee.setRecipe(recipe);
        packedCraftFee.setFee(recipe.getFee());
        double skillAppliedFee = Utils.apply(player, packedCraftFee, ICraftFeeModifier.class).getFee();
        if (skillAppliedFee > balance) {
            return Component.text("所持金が足りません！ " + Math.floor(balance * 100) / 100 + "/" + recipe.getFee()).color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false);
        } else
            return null;
    }

    /**
     * プレイヤーインベントリからレシピで指定された材料を差し引く
     */
    private static void withdrawItems(Player player, ITCCraftingRecipe recipe) {
        for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {

            IIngredientAmountModifier.PackedRecipeData packedRecipeData = new IIngredientAmountModifier.PackedRecipeData();
            packedRecipeData.setRecipe(recipe);
            packedRecipeData.setAmount(entry.getValue());
            int amountSkillApplied = Utils.apply(player, packedRecipeData, IIngredientAmountModifier.class).getAmount();

            ItemUtils.tcRemoveItemAnySlot(player.getInventory(), TCItemRegistry.INSTANCE.commandToTCItem(entry.getKey()).orElseThrow(), amountSkillApplied);
        }
    }

    /**
     * プレイヤーインベントリからレシピで指定された工費を差し引く
     */
    private static void withdrawBalance(Player player, ITCCraftingRecipe recipe) {
        ICraftFeeModifier.PackedCraftFee packedCraftFee = new ICraftFeeModifier.PackedCraftFee();
        packedCraftFee.setRecipe(recipe);
        packedCraftFee.setFee(recipe.getFee());
        double skillAppliedFee = Utils.apply(player, packedCraftFee, ICraftFeeModifier.class).getFee();
        TCEconomy.getImpl().withdrawPlayer(player, skillAppliedFee);
    }

    private static void close(Player player) {
        player.closeInventory();
    }

}
