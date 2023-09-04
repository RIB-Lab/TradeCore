package net.riblab.tradecore.item;

import net.riblab.tradecore.modifier.IModifier;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public sealed interface PlayerItemModService permits PlayerItemModServiceImpl{

    static PlayerItemModService getImpl() {
        return PlayerItemModServiceImpl.INSTANCE;
    }

    List<Consumer<Player>> getOnItemModUpdated();

    /**
     * プレイヤーをスキャンして装備modをリストアップして保存する
     */
    void updateEquipment(Player player);

    /**
     * プレイヤーのメインハンドをスキャンしてアイテムのmodをリストアップして保存する
     *
     * @param newSlot (新しい)メインハンドの場所のスロット番号
     */
    void updateMainHand(Player player, int newSlot);

    /**
     * 保存しているプレイヤーの全てのmodを消去する
     */
    void remove(Player player);

    /**
     * ある値をプレイヤーの持つアイテムmodで修飾する
     */
    <T> T apply(Player player, T originalValue, Class<? extends IModifier<T>> clazz);
}
