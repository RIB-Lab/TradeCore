package net.riblab.tradecore;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.riblab.tradecore.craft.TCRecipe;
import net.riblab.tradecore.craft.TCRecipes;
import net.riblab.tradecore.item.ITCItem;
import net.riblab.tradecore.item.TCItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class UICraftingTable {

    private static final Set<Integer> allowedIngredientSlotSet = Set.of(0,1,2,9,10,11,18,19,20);
    
    /**
     * カテゴリやレシピ選択画面を開く
     * @param player
     * @param type
     */
    public static void open(Player player, CraftingScreenType type){
        PaginatedGui gui = Gui.paginated()
                .title(type.getTitle())
                .rows(3)
                .disableAllInteractions()
                .create();

        if(type == CraftingScreenType.CATEGORY){
            addCategoryScreen(gui, player);
        }
        else if(type == CraftingScreenType.CRAFTING){
            throw new IllegalArgumentException();
        }
        else{
            addRecipeListScreen(type, gui, player);
        }

        //タイトルを強制的にcustomfontにする
        gui.setUpdating(true);
        gui.setInventory(Bukkit.createInventory(gui, gui.getInventory().getSize(), type.getTitle()));
        gui.setUpdating(false);
        
        gui.open(player);
        
        FakeVillagerService.spawnFakeVillager(player);
    }

    /**
     * クラフト確認画面を開く
     */
    public static void open(Player player, TCRecipe recipe){
        PaginatedGui gui = Gui.paginated()
                .title(CraftingScreenType.CRAFTING.getTitle())
                .rows(3)
                .disableAllInteractions()
                .create();

        gui.setUpdating(true);
        gui.setInventory(Bukkit.createInventory(gui, gui.getInventory().getSize(), CraftingScreenType.CRAFTING.getTitle()));
        gui.setUpdating(false);
        
        addCraftingScreen(gui, player, recipe);

        gui.open(player);

        FakeVillagerService.spawnFakeVillager(player);
    }

    /**
     * カテゴリ選択画面を実装
     */
    private static void addCategoryScreen(PaginatedGui gui, Player player){
        ItemStack armorCategory = new ItemCreator(Material.IRON_CHESTPLATE).setName(Component.text("装備品"))
                .create();
        GuiItem armorButton = new GuiItem(armorCategory,
                event -> open(player, CraftingScreenType.ARMOR));
        gui.setItem(0, armorButton);

        ItemStack toolCategory = new ItemCreator(Material.IRON_PICKAXE).setName(Component.text("ツール"))
                .create();
        GuiItem toolButton = new GuiItem(toolCategory,
                event -> open(player, CraftingScreenType.TOOL));
        gui.setItem(1, toolButton);

        ItemStack weaponCategory = new ItemCreator(Material.IRON_SWORD).setName(Component.text("武器"))
                .create();
        GuiItem weaponButton = new GuiItem(weaponCategory,
                event -> open(player, CraftingScreenType.WEAPON));
        gui.setItem(2, weaponButton);

        ItemStack miscCategory = new ItemCreator(Material.FLOWER_POT).setName(Component.text("その他"))
                .create();
        GuiItem miscButton = new GuiItem(miscCategory,
                event -> open(player, CraftingScreenType.MISC));
        gui.setItem(9, miscButton);
    }

    /**
     * レシピリスト画面を実装
     */
    private static void addRecipeListScreen(CraftingScreenType type, PaginatedGui gui, Player player){
        List<TCRecipe> recipeList = TCRecipes.getRecipes(type.getRecipeType());
        if(recipeList == null || recipeList.size() == 0)
            return;
        
        recipeList.forEach(tcRecipe -> {
            ItemStack recipeStack = tcRecipe.getResult().getItemStack();
            GuiItem recipeButton = new GuiItem(recipeStack,
                    event -> open(player, tcRecipe));
            gui.addItem(recipeButton);
        });

        gui.getFiller().fillBetweenPoints(1, 4, 3, 9, ItemBuilder.from(Material.AIR).asGuiItem());
        
        gui.setItem(22, ItemBuilder.from(Material.ARROW).setName("前のページ").asGuiItem(event -> gui.previous()));
        gui.setItem(24, ItemBuilder.from(Material.ARROW).setName("次のページ").asGuiItem(event -> gui.next()));//TODO:カスタムアイテム
    }

    /**
     * クラフト確認画面を実装
     */
    private static void addCraftingScreen(PaginatedGui gui, Player player, TCRecipe recipe){
        int slot = 0;
        for (Map.Entry<ITCItem, Integer> entry : recipe.getIngredients().entrySet()) {
            ItemStack ingredientStack = entry.getKey().getItemStack();
            ingredientStack.setAmount(entry.getValue());
            GuiItem ingredientDisplay = new GuiItem(ingredientStack);
            gui.setItem(slot, ingredientDisplay);
            
            do {
                slot++;
            }while(!allowedIngredientSlotSet.contains(slot));
        }

        ItemStack resultStack = recipe.getResult().getItemStack();
        Component craftTip = Component.text("<<クリックで製作>>").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        resultStack = new ItemCreator(resultStack).addLore(craftTip).create();
        resultStack.setAmount(recipe.getResultAmount());
        ItemStack finalResultStack = resultStack;
        GuiItem craftButton = new GuiItem(resultStack, event -> {
            tryCraft(gui, player, recipe, finalResultStack);
        });
        gui.setItem(14, craftButton);

        ItemStack feeStack = TCItems.COIN.get().getItemStack(); //カスタムアイテム
        Component name = Component.text("工費: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(recipe.getFee()).color(NamedTextColor.YELLOW));
        Component lore = Component.text("職人に報酬を支払います").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY);
        feeStack = new ItemCreator(feeStack).setName(name).setLore(lore).create();
        GuiItem feeDisplay = new GuiItem(feeStack);
        gui.setItem(23, feeDisplay);
    }

    /**
     * クラフトの決済処理を行う
     */
    private static void tryCraft(PaginatedGui gui, Player player, TCRecipe recipe, ItemStack resultStack){
        List<Component> missingLore = new ArrayList<>();
        for (Map.Entry<ITCItem, Integer> entry : recipe.getIngredients().entrySet()) {
            boolean playerHasItem = player.getInventory().containsAtLeast(entry.getKey().getItemStack(), entry.getValue());
            if(playerHasItem)
                continue;
            
            missingLore.add(Component.text(entry.getKey().getName().content() + "が足りません！").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }
        
        double balance = TradeCore.getInstance().getEconomy().getBalance(player);
        if(recipe.getFee() > balance){
            missingLore.add(Component.text("所持金が足りません！ " + balance + "/" + recipe.getFee()).color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        }
        
        if(missingLore.size() > 0){
            ItemStack newResultStack = new ItemCreator(resultStack).setLores(missingLore).create();
            gui.updatePageItem(14, newResultStack);
            gui.update();
            return;
        }

        for (Map.Entry<ITCItem, Integer> entry : recipe.getIngredients().entrySet()) {
            ItemStack itemStack = entry.getKey().getItemStack();
            itemStack.setAmount(entry.getValue());
            player.getInventory().removeItemAnySlot(itemStack);
        }
        TradeCore.getInstance().getEconomy().withdrawPlayer(player, recipe.getFee());
        
        player.getInventory().addItem(recipe.getResult().getItemStack());
    }
    
    private static void close(Player player){
        player.closeInventory();
    }
    
    public enum CraftingScreenType {
        CATEGORY("作業台", TCResourcePackData.UIFont.CRAFTING_TABLE_CATEGORY, null),
        ARMOR("装備品", TCResourcePackData.UIFont.CRAFTING_TABLE_ARMOR, TCRecipes.RecipeType.ARMOR),
        TOOL("ツール", TCResourcePackData.UIFont.CRAFTING_TABLE_TOOL, TCRecipes.RecipeType.TOOL),
        WEAPON("武器", TCResourcePackData.UIFont.CRAFTING_TABLE_WEAPON, TCRecipes.RecipeType.WEAPON),
        MISC("その他", TCResourcePackData.UIFont.CRAFTING_TABLE_MISC, TCRecipes.RecipeType.MISC),
        CRAFTING("加工", TCResourcePackData.UIFont.CRAFTING_TABLE_CRAFTING, null);

        @Getter
        private final Component title;
        
        @Getter
        private final TCRecipes.RecipeType recipeType;

        CraftingScreenType(String rawTitle, TCResourcePackData.UIFont screenStr, TCRecipes.RecipeType recipeType) {
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
        
        public static CraftingScreenType titleToType(Component title){
            return Arrays.stream(CraftingScreenType.values()).filter(type -> type.getTitle().equals(title)).findFirst().orElse(null);
        }
    }
}
