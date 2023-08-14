package net.riblab.tradecore;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UICraftingTable {
    
    public static void open(Player player, CraftingScreenType type){
        PaginatedGui gui = Gui.paginated()
                .title(type.getTitle())
                .rows(3)
                .pageSize(27)
                .disableAllInteractions()
                .create();

        if(type == CraftingScreenType.CATEGORY){
            //TODO:UIの実装をTypeのクラスごとにわける
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

        //タイトルを強制的にcustomfontにする
        gui.setUpdating(true);
        gui.setInventory(Bukkit.createInventory(gui, gui.getInventory().getSize(), type.getTitle()));
        gui.setUpdating(false);
        
        gui.open(player);
    }
    
    public enum CraftingScreenType{
        CATEGORY("作業台", TCResourcePackData.UIFont.CRAFTING_TABLE_CATEGORY),
        ARMOR("装備品", TCResourcePackData.UIFont.CRAFTING_TABLE_ARMOR),
        TOOL("ツール", TCResourcePackData.UIFont.CRAFTING_TABLE_TOOL),
        WEAPON("武器", TCResourcePackData.UIFont.CRAFTING_TABLE_WEAPON),
        MISC("その他", TCResourcePackData.UIFont.CRAFTING_TABLE_MISC),
        CRAFTING("加工", TCResourcePackData.UIFont.CRAFTING_TABLE_CRAFTING);

        @Getter
        private final Component title;

        CraftingScreenType(String rawTitle, TCResourcePackData.UIFont screenStr) {
            //タイトル作成
            String neg = TCResourcePackData.UIFont.NEGATIVE_SPACE.get_char();
            String neg2 = TCResourcePackData.UIFont.SUPER_NEGATIVE_SPACE.get_char();
            String main = screenStr.get_char();
            Component text = Component.text(neg + neg + main).font(TCResourcePackData.uiFontName).color(NamedTextColor.WHITE);
            text = text.append(Component.text(neg2 + neg2 + neg2 + neg2 + neg + neg).font(TCResourcePackData.uiFontName));
            text = text.append(Component.text(rawTitle).font(TCResourcePackData.defaultFontName).color(NamedTextColor.BLACK));
            this.title = text;
        }
        
        public static CraftingScreenType titleToType(Component title){
            return Arrays.stream(CraftingScreenType.values()).filter(type -> type.getTitle().equals(title)).findFirst().orElse(null);
        }
    }
}
