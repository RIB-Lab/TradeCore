package net.riblab.tradecore.item.mod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.job.JobData;

import java.util.List;

/**
 * ゲームに影響を与えるアイテムのmod
 */
@AllArgsConstructor
public class ItemMod {

    /**
     * 現在習得したこのmodのレベル
     */
    @Getter @Setter
    private int level;
    
    /**
     * modの説明文。必ず子クラスで実装する！
     */
    public String getLore(){
        return "ダミーを" + level + "増加させる";
    }
}
