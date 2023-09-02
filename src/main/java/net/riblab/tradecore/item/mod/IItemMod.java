package net.riblab.tradecore.item.mod;

public interface IItemMod {
    /**
     * modの説明文。
     */
    String getLore();

    double getLevel();

    void setLevel(double level);
}
