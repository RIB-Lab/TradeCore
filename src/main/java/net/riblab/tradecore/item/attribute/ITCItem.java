package net.riblab.tradecore.item.attribute;

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
     * 固有アイテムのテンプレートを取得
     *
     * @return 固有アイテムの実体のコピー
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

    /**
     * 固有アイテムの実体をプレイヤーに渡すイベントが発火された時の挙動<br>
     * ここをオーバーライドして固有アイテムの実体に色んなデータを書きこんでからプレイヤーに渡すことができる
     *
     * @param sender   固有アイテムを召喚したプレイヤー
     * @param argments 召喚コマンドの引数
     */
    void onGiveCommand(CommandSender sender, String[] argments);
}
