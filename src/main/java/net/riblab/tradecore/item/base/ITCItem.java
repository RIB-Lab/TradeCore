package net.riblab.tradecore.item.base;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

/**
 * このプラグインに存在する全ての固有アイテムの親
 */
public interface ITCItem {

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

    /**
     * 固有アイテムの初期値がランダマイズされた実体を取得(プレイヤーに渡す用)
     * 
     * @return 固有アイテムの新しい実体
     */
    default ItemStack getItemStack(){
        return getTemplateItemStack();
    };

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
}
