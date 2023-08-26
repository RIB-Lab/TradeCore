package net.riblab.tradecore.ui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.Utils;
import net.riblab.tradecore.mob.FakeVillagerService;
import net.riblab.tradecore.item.ItemCreator;
import net.riblab.tradecore.TCResourcePackData;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.craft.ITCCraftingRecipe;
import net.riblab.tradecore.craft.TCCraftingRecipes;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.modifier.ICraftFeeModifier;
import net.riblab.tradecore.modifier.IIngredientAmountModifier;
import net.riblab.tradecore.job.JobData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

import static net.riblab.tradecore.Materials.transparentBlocks;

/**
 * 作業台UI
 */
public class UICraftingTable {

    /**
     * レシピや材料を置くことが可能なスロット番号のリスト
     */
    private static final Set<Integer> allowedIngredientSlotSet = Set.of(0, 1, 2, 9, 10, 11, 18, 19, 20);

    /**
     * カテゴリやレシピ選択画面を開く
     *
     * @param player
     * @param type
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
                FakeVillagerService.tryDeSpawnFakeVillager(player);
        });

        gui.open(player);

        Location spawnLocation = player.getTargetBlock(transparentBlocks, 5).getRelative(0, 1, 0).getLocation().add(new Vector(0.5d, 0d, 0.5d));
        FakeVillagerService.spawnFakeVillager(player, "職人", spawnLocation);
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
                FakeVillagerService.tryDeSpawnFakeVillager(player);
        });

        gui.open(player);

        Location spawnLocation = player.getTargetBlock(transparentBlocks, 5).getRelative(0, 1, 0).getLocation().add(new Vector(0.5d, 0d, 0.5d));
        FakeVillagerService.spawnFakeVillager(player, "職人", spawnLocation);
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
        List<ITCCraftingRecipe> recipeList = TCCraftingRecipes.getRecipes(type.getRecipeType());
        if (recipeList == null || recipeList.size() == 0)
            return;

        gui.setPageSize(9);
        gui.getFiller().fillBetweenPoints(1, 4, 3, 9, ItemBuilder.from(Material.AIR).asGuiItem());

        GuiItem previousPageButton = new GuiItem(TCItems.PREVIOUS_PAGE.get().getItemStack(),
                event -> gui.previous());
        gui.setItem(22, previousPageButton);
        GuiItem nextPageButton = new GuiItem(TCItems.NEXT_PAGE.get().getItemStack(),
                event -> gui.next());
        gui.setItem(24, nextPageButton);

        recipeList.forEach(tcCraftingRecipe -> {
            ItemStack recipeStack = tcCraftingRecipe.getResult().clone();
            GuiItem recipeButton = new GuiItem(recipeStack,
                    event -> open(player, tcCraftingRecipe));
            gui.addItem(recipeButton);
        });
    }

    /**
     * クラフト確認画面を実装
     */
    private static void addCraftingScreen(PaginatedGui gui, Player player, ITCCraftingRecipe recipe) {
        int slot = 0;
        for (Map.Entry<ITCItem, Integer> entry : recipe.getIngredients().entrySet()) {
            ItemStack ingredientStack = entry.getKey().getItemStack();
            
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

        ItemStack resultStack = recipe.getResult().clone();
        Component craftTip = Component.text("<<クリックで製作>>").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        resultStack = new ItemCreator(resultStack).addLore(craftTip).create();
        resultStack.setAmount(recipe.getResultAmount());
        ItemStack finalResultStack = resultStack;
        GuiItem craftButton = new GuiItem(resultStack, event -> {
            tryCraft(gui, player, recipe, finalResultStack);
        });
        gui.setItem(14, craftButton);

        ICraftFeeModifier.PackedCraftFee packedCraftFee = new ICraftFeeModifier.PackedCraftFee();
        packedCraftFee.setRecipe(recipe);
        packedCraftFee.setFee(recipe.getFee());
        double skillAppliedFee = Utils.apply(player, packedCraftFee, ICraftFeeModifier.class).getFee();
        
        ItemStack feeStack = TCItems.COIN.get().getItemStack();
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
        List<Component> missingLore = new ArrayList<>();
        for (Map.Entry<ITCItem, Integer> entry : recipe.getIngredients().entrySet()) {
            IIngredientAmountModifier.PackedRecipeData packedRecipeData = new IIngredientAmountModifier.PackedRecipeData();
            packedRecipeData.setRecipe(recipe);
            packedRecipeData.setAmount(entry.getValue());
            int amountSkillApplied = Utils.apply(player, packedRecipeData, IIngredientAmountModifier.class).getAmount();
            
            boolean playerHasItem = player.getInventory().containsAtLeast(entry.getKey().getItemStack(), amountSkillApplied);
            if (playerHasItem)
                continue;

            missingLore.add(Component.text(entry.getKey().getName().content() + "が足りません！").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }

        double balance = TradeCore.getInstance().getEconomy().getBalance(player);
        ICraftFeeModifier.PackedCraftFee packedCraftFee = new ICraftFeeModifier.PackedCraftFee();
        packedCraftFee.setRecipe(recipe);
        packedCraftFee.setFee(recipe.getFee());
        double skillAppliedFee = Utils.apply(player, packedCraftFee, ICraftFeeModifier.class).getFee();
        if (skillAppliedFee > balance) {
            missingLore.add(Component.text("所持金が足りません！ " + Math.floor(balance * 100) / 100 + "/" + recipe.getFee()).color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }

        if (missingLore.size() > 0) {
            ItemStack newResultStack = new ItemCreator(resultStack).setLores(missingLore).create();
            gui.updatePageItem(14, newResultStack);
            gui.update();
            return;
        }

        for (Map.Entry<ITCItem, Integer> entry : recipe.getIngredients().entrySet()) {
            ItemStack itemStack = entry.getKey().getItemStack();

            IIngredientAmountModifier.PackedRecipeData packedRecipeData = new IIngredientAmountModifier.PackedRecipeData();
            packedRecipeData.setRecipe(recipe);
            packedRecipeData.setAmount(entry.getValue());
            int amountSkillApplied = Utils.apply(player, packedRecipeData, IIngredientAmountModifier.class).getAmount();
            
            itemStack.setAmount(amountSkillApplied);
            player.getInventory().removeItemAnySlot(itemStack);
        }
        TradeCore.getInstance().getEconomy().withdrawPlayer(player, skillAppliedFee);

        TradeCore.getInstance().getJobHandler().addJobExp(player, JobData.JobType.Crafter, (int)recipe.getFee());

        HashMap<Integer, ItemStack> remains = player.getInventory().addItem(recipe.getResult());
        if(remains.size() == 0)
            return;
        
        remains.forEach((integer, itemStack) -> {
            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
        });
    }

    private static void close(Player player) {
        player.closeInventory();
    }

    public enum CraftingScreenType {
        CATEGORY("作業台", TCResourcePackData.UIFont.CRAFTING_TABLE_CATEGORY, null),
        ARMOR("装備品", TCResourcePackData.UIFont.CRAFTING_TABLE_ARMOR, TCCraftingRecipes.RecipeType.ARMOR),
        TOOL("ツール", TCResourcePackData.UIFont.CRAFTING_TABLE_TOOL, TCCraftingRecipes.RecipeType.TOOL),
        WEAPON("武器", TCResourcePackData.UIFont.CRAFTING_TABLE_WEAPON, TCCraftingRecipes.RecipeType.WEAPON),
        MISC("その他", TCResourcePackData.UIFont.CRAFTING_TABLE_MISC, TCCraftingRecipes.RecipeType.MISC),
        CRAFTING("加工", TCResourcePackData.UIFont.CRAFTING_TABLE_CRAFTING, null);

        @Getter
        private final Component title;

        @Getter
        private final TCCraftingRecipes.RecipeType recipeType;

        CraftingScreenType(String rawTitle, TCResourcePackData.UIFont screenStr, TCCraftingRecipes.RecipeType recipeType) {
            //タイトル作成
            String neg = TCResourcePackData.UIFont.NEGATIVE_SPACE.get_char();
            String neg2 = TCResourcePackData.UIFont.SUPER_NEGATIVE_SPACE.get_char();
            String main = screenStr.get_char();
            Component text = Component.text(neg + neg + main).font(TCResourcePackData.uiFontName).color(NamedTextColor.WHITE);
            text = text.append(Component.text(neg2 + neg2 + neg2 + neg2 + neg + neg).font(TCResourcePackData.uiFontName));
            text = text.append(Component.text(rawTitle).font(TCResourcePackData.defaultFontName).color(NamedTextColor.BLACK));
            this.title = text;
            this.recipeType = recipeType;
        }

        public static CraftingScreenType titleToType(Component title) {
            return Arrays.stream(CraftingScreenType.values()).filter(type -> type.getTitle().equals(title)).findFirst().orElse(null);
        }
    }
}
