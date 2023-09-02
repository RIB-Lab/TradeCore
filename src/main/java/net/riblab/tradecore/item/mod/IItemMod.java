package net.riblab.tradecore.item.mod;

public interface IItemMod<T> {
    /**
     * modのツールチップ上での説明文。
     */
    String getLore();

    T getParam();

    void setParam(T level);
}
