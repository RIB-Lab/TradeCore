package net.riblab.tradecore.ui;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.riblab.tradecore.craft.TCCraftingRecipes;
import net.riblab.tradecore.integration.TCResourcePackData;

import java.util.Arrays;

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
