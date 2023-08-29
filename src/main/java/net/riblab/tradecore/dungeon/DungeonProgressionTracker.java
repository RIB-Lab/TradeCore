package net.riblab.tradecore.dungeon;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.TradeCore;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.ParametersAreNonnullByDefault;
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

    /**
     * 最大死亡可能回数
     */
    private static final int maxDeathCount = 5;

    /**
     * このダンジョンでの残り蘇生可能回数
     */
    int leftReviveCount = maxDeathCount;

    /**
     * このダンジョンがアクティブ(イベントを受け付ける状態)であるか
     */
    boolean isActive = true;
    
    public DungeonProgressionTracker(T objective, World instance) {
        this.objective = objective;
        this.instance = instance;
    }

    public abstract void onDungeonSecond(Player player);

    /**
     * プレイヤーをダンジョン内にスポーンさせる
     */
    @ParametersAreNonnullByDefault
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if(!isActive)
            return;
        
        if(leftReviveCount >= 0){
            event.getPlayer().sendMessage(Component.text("残り蘇生可能回数:" + leftReviveCount));
            leftReviveCount--;
            event.setRespawnLocation(event.getPlayer().getWorld().getSpawnLocation());
        }
        else{
            event.getPlayer().getWorld().sendMessage(Component.text("残り蘇生回数を使い切りました。ダンジョンが崩れていく..."));
            event.setRespawnLocation(event.getPlayer().getWorld().getSpawnLocation());
            isActive = false;
            new BukkitRunnable(){
                @Override
                public void run() {
                    TradeCore.getInstance().getDungeonService().destroySpecific(event.getPlayer().getWorld());
                }
            }.runTaskLater(TradeCore.getInstance(), 0);
        }
    }
}
