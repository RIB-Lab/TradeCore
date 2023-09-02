package net.riblab.tradecore.item.mod;

import javax.annotation.Nullable;

/**
 * アイテムに取り付けることのできるパーツ。modifierと併用することでアイテムに様々な性質を付与できる
 * @param <T>
 */
public interface IItemMod<T> {
    /**
     * modのツールチップ上での説明文。
     * 隠したい時はnullでoverride
     */
    @Nullable String getLore();

    T getParam();

    void setParam(T level);
}
