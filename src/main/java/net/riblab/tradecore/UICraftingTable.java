package net.riblab.tradecore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.riblab.tradecore.craft.TCRecipe;
import net.riblab.tradecore.craft.TCRecipes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class UICraftingTable {
    
    public static void open(Player player, CraftingScreenType type){
        PaginatedGui gui = Gui.paginated()
                .title(type.getTitle())
                .rows(3)
                .disableAllInteractions()
                .create();

        if(type == CraftingScreenType.CATEGORY){
            //TODO:UIの実装をTypeのクラスごとにわける
            addCategoryScreen(gui, player);
        }
        else if(type == CraftingScreenType.CRAFTING){
            //TODO:ここに加工画面の関数を実装
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
                    event -> close(player));//TODO:加工画面に遷移
            gui.addItem(recipeButton);
        });

        gui.getFiller().fillBetweenPoints(1, 4, 3, 9, ItemBuilder.from(Material.AIR).asGuiItem());
        
        gui.setItem(22, ItemBuilder.from(Material.ARROW).setName("前のページ").asGuiItem(event -> gui.previous()));
        gui.setItem(24, ItemBuilder.from(Material.ARROW).setName("次のページ").asGuiItem(event -> gui.next()));//TODO:カスタムアイテム
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
