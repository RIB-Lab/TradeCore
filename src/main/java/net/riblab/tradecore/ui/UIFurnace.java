/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.ui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.craft.ITCFurnaceRecipe;
import net.riblab.tradecore.craft.TCFurnaceRecipes;
import net.riblab.tradecore.integration.TCResourcePackData;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.item.ItemUtils;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItemRegistry;
import net.riblab.tradecore.item.base.TCItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

final class UIFurnace implements IUI {

    /**
     * レシピや材料を置くことが可能なスロット番号のリスト
     */
    private static final Set<Integer> allowedIngredientSlotSet = Set.of(0, 1, 2, 9, 10, 11, 18, 19, 20);

    /**
     * レシピ選択画面を開く
     */
    @Override
    public BaseGui open(Player player) {
        PaginatedGui gui = Gui.paginated()
                .title(SmeltingScreenType.SELECTION.getTitle())
                .rows(3)
                .disableAllInteractions()
                .create();

        addRecipeListScreen(gui, player);

        //タイトルを強制的にcustomfontにする
        gui.setUpdating(true);
        gui.setInventory(Bukkit.createInventory(gui, gui.getInventory().getSize(), SmeltingScreenType.SELECTION.getTitle()));
        gui.setUpdating(false);

        gui.open(player);
        return gui;
    }

    /**
     * クラフト確認画面を開く
     */
    public static PaginatedGui open(Player player, ITCFurnaceRecipe recipe) {
        PaginatedGui gui = Gui.paginated()
                .title(SmeltingScreenType.SMELTING.getTitle())
                .rows(3)
                .disableAllInteractions()
                .create();

        gui.setUpdating(true);
        gui.setInventory(Bukkit.createInventory(gui, gui.getInventory().getSize(), SmeltingScreenType.SMELTING.getTitle()));
        gui.setUpdating(false);

        addSmeltingScreen(gui, player, recipe);

        gui.open(player);
        return gui;
    }

    /**
     * レシピリスト画面を実装
     */
    private static void addRecipeListScreen(PaginatedGui gui, Player player) {
        List<ITCFurnaceRecipe> recipeList = Arrays.stream(TCFurnaceRecipes.values()).map(TCFurnaceRecipes::getRecipe).toList();
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

        recipeList.forEach(tcCraftingRecipe -> {
            ItemStack recipeStack = TCItemRegistry.INSTANCE.commandToTCItem(tcCraftingRecipe.result()).orElseThrow().getTemplateItemStack();
            GuiItem recipeButton = new GuiItem(recipeStack,
                    event -> open(player, tcCraftingRecipe));
            gui.addItem(recipeButton);
        });
    }

    /**
     * クラフト確認画面を実装
     */
    private static void addSmeltingScreen(PaginatedGui gui, Player player, ITCFurnaceRecipe recipe) {
        int slot = 0;
        for (Map.Entry<String, Integer> entry : recipe.ingredients().entrySet()) {
            ItemStack ingredientStack = TCItemRegistry.INSTANCE.commandToTCItem(entry.getKey()).orElseThrow().getTemplateItemStack();
            ingredientStack.setAmount(entry.getValue());
            GuiItem ingredientDisplay = new GuiItem(ingredientStack);
            gui.setItem(slot, ingredientDisplay);

            do {
                slot++;
            } while (!allowedIngredientSlotSet.contains(slot));
        }

        ItemStack resultStack = TCItemRegistry.INSTANCE.commandToTCItem(recipe.result()).orElseThrow().getTemplateItemStack();;
        Component craftTip = Component.text("<<クリックで精錬>>").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        resultStack = new ItemCreator(resultStack).addLore(craftTip).create();
        resultStack.setAmount(recipe.resultAmount());
        ItemStack finalResultStack = resultStack;
        GuiItem craftButton = new GuiItem(resultStack, event -> trySmelt(gui, player, recipe, finalResultStack));
        gui.setItem(14, craftButton);

        ItemStack feeStack = TCItems.FUEL_BALL.get().getTemplateItemStack();
        Component name = Component.text("燃料: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(recipe.fuelAmount()).color(NamedTextColor.YELLOW));
        Component lore = Component.text("タイプ：燃料玉").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY);
        feeStack = new ItemCreator(feeStack).setName(name).setLore(lore).create();
        GuiItem feeDisplay = new GuiItem(feeStack);
        gui.setItem(23, feeDisplay);
    }

    /**
     * 精錬の決済処理を行う
     */
    private static void trySmelt(PaginatedGui gui, Player player, ITCFurnaceRecipe recipe, ItemStack resultStack) {
        List<Component> missingLore = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : recipe.ingredients().entrySet()) {
            final ITCItem ingredientItem = TCItemRegistry.INSTANCE.commandToTCItem(entry.getKey()).orElseThrow();
            boolean playerHasItem = ItemUtils.tcContainsAtLeast(player.getInventory(), ingredientItem, entry.getValue());
            if (playerHasItem)
                continue;
            
            missingLore.add(Component.text(ingredientItem.getName().content() + "が足りません！").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }

        boolean playerHasFuel = ItemUtils.tcContainsAtLeast(player.getInventory(), TCItems.FUEL_BALL.get(), recipe.fuelAmount());
        if (!playerHasFuel) {
            missingLore.add(Component.text("燃料が足りません！").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }

        if (missingLore.size() > 0) {
            ItemStack newResultStack = new ItemCreator(resultStack).setLores(missingLore).create();
            gui.updatePageItem(14, newResultStack);
            gui.update();
            return;
        }

        for (Map.Entry<String, Integer> entry : recipe.ingredients().entrySet()) {
            final ITCItem ingredientItem = TCItemRegistry.INSTANCE.commandToTCItem(entry.getKey()).orElseThrow();
            ItemUtils.tcRemoveItemAnySlot(player.getInventory(), ingredientItem, entry.getValue());
        }
        ItemUtils.tcRemoveItemAnySlot(player.getInventory(), TCItems.FUEL_BALL.get(), recipe.fuelAmount());

        final ITCItem resultItem = TCItemRegistry.INSTANCE.commandToTCItem(recipe.result()).orElseThrow();
        HashMap<Integer, ItemStack> remains = player.getInventory().addItem(resultItem.getItemStack());
        if (remains.isEmpty())
            return;

        remains.forEach((integer, itemStack) -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
    }

    private static void close(Player player) {
        player.closeInventory();
    }

    public enum SmeltingScreenType {
        SELECTION("レシピ選択", TCResourcePackData.UIFont.FURNACE),
        SMELTING("加工", TCResourcePackData.UIFont.CRAFTING_TABLE_CRAFTING);

        @Getter
        private final Component title;

        SmeltingScreenType(String rawTitle, TCResourcePackData.UIFont screenStr) {
            //タイトル作成
            String neg = TCResourcePackData.UIFont.NEGATIVE_SPACE.get_char();
            String neg2 = TCResourcePackData.UIFont.SUPER_NEGATIVE_SPACE.get_char();
            String main = screenStr.get_char();
            Component text = Component.text(neg + neg + main).font(TCResourcePackData.uiFontName).color(NamedTextColor.WHITE);
            text = text.append(Component.text(neg2 + neg2 + neg2 + neg2 + neg + neg).font(TCResourcePackData.uiFontName));
            text = text.append(Component.text(rawTitle).font(TCResourcePackData.defaultFontName).color(NamedTextColor.BLACK));
            this.title = text;
        }

        public static SmeltingScreenType titleToType(Component title) {
            return Arrays.stream(SmeltingScreenType.values()).filter(type -> type.getTitle().equals(title)).findFirst().orElse(null);
        }
    }
}
