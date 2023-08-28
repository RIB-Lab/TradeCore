package net.riblab.tradecore.dungeon;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * ダンジョンの進捗トラッカー
 */
public abstract class DungeonProgressionTracker<T> {

    /**
     * 目標をコンプリートした時のイベント
     */
    public Consumer<World> onComplete;

    /**
     * ダンジョンの目標
     */
    @Getter
    private final T objective;

    /**
     * このトラッカーが追跡しているダンジョンのインスタンス
     */
    @Getter
    private final World instance;
    
    public DungeonProgressionTracker(T objective, World instance) {
        this.objective = objective;
        this.instance = instance;
    }

    public abstract void onDungeonSecond(Player player);
}
