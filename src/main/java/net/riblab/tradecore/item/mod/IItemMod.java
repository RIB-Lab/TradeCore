package net.riblab.tradecore.item.mod;

public interface IItemMod {
    /**
     * modの説明文。
     */
    String getLore();

    int getLevel();

    void setLevel(int level);
}
