package net.riblab.tradecore.item.mod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * ゲームに影響を与えるアイテムのmod
 */
@AllArgsConstructor
public class ItemMod implements IItemMod {

    /**
     * 現在習得したこのmodのレベル
     */
    @Getter
    @Setter
    private double level;

    @Override
    public String getLore() {
        return "ダミーを" + level + "増加させる";
    }
}
