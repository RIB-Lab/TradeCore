/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.item.base;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.riblab.tradecore.item.mod.IItemMod;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * このプラグインに存在する全ての固有アイテムの韻テーフェース
 */
public sealed interface ITCItem permits TCItem {

    /**
     * この固有アイテムの表示名を取得
     *
     * @return 表示名
     */
    TextComponent getName();

    /**
     * この固有アイテムの元となっているバニラアイテムの種類を取得
     *
     * @return バニラアイテムの種類
     */
    Material getMaterial();

    /**
     * 固有アイテムのテンプレートのItemStackを取得(レシピ表示用など)
     *
     * @return 固有アイテムのテンプレートのコピー
     */
    ItemStack getTemplateItemStack();
    
    int getCustomModelData();

    /**
     * 固有アイテムの初期値がランダマイズされたItemStackを生成して取得(プレイヤーに渡す用)
     * 
     * @return 固有アイテムの新しい実体
     */
    ItemStack getItemStack();

    /**
     * 固有アイテムの内部名称を取得
     *
     * @return 内部名称
     */
    String getInternalName();

    /**
     * ItemStackがこの固有アイテムのインスタンスであるかどうか確認する
     *
     * @param itemStack 確認したいITemStack
     * @return インスタンスであるかどうか
     */
    boolean isSimilar(ItemStack itemStack);

    boolean isSimilar(String tcID);

    List<Component> getLore(List<IItemMod<?>> randomMods);

    /**
     * アイテムが既定で持つmodのリスト
     */
    List<IItemMod<?>> getDefaultMods();
}
