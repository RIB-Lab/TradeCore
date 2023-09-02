package net.riblab.tradecore.item.mod;

import javax.annotation.Nullable;

public interface IItemMod<T> {
    /**
     * modのツールチップ上での説明文。
     * 隠したい時はnullでoverride
     */
    @Nullable String getLore();

    T getParam();

    void setParam(T level);
}
