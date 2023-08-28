package net.riblab.tradecore.dungeon;

import net.riblab.tradecore.TradeCore;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.List;

public interface DungeonService {

    String dungeonPrefix = "dungeons";
    
    static DungeonService getImpl(){
        return new DungeonServiceImpl();
    }
    
    /**
     * データを基にダンジョンを作る
     */
    @ParametersAreNonnullByDefault
    void create(IDungeonData<?> data);

    /**
     * ダンジョンのインスタンスを作る
     *
     * @param data       名前
     * @param instanceID インスタンスのID。0未満なら0以上の最初に空いているインスタンス
     */
    @ParametersAreNonnullByDefault
    @Nullable World create(IDungeonData<?> data, int instanceID);

    /**
     * ダンジョンのインスタンスが存在するかどうか
     */
    @ParametersAreNonnullByDefault
    boolean isDungeonExist(IDungeonData<?> data, int id);

    /**
     * ダンジョンのインスタンスに入る
     */
    @ParametersAreNonnullByDefault
    void enter(Player player, IDungeonData<?> data, int id);
    
    @ParametersAreNonnullByDefault
    void enter(Player player, @Nullable World world);

    /**
     * プレイヤーをダンジョンから退出させる
     */
    @ParametersAreNonnullByDefault
    void tryLeave(Player player);

    /**
     * プレイヤーをダンジョンから強制脱出させる、または無理矢理メインワールドに転送する
     */
    @ParametersAreNonnullByDefault
    void evacuate(Player player);

    /**
     * プレイヤーがダンジョン内にいるかどうか
     */
    boolean isPlayerInDungeon(@Nullable Player player);

    /**
     * 全てのダンジョンを削除する
     */
    void destroyAll();

    /**
     * 特定のダンジョンを削除する
     *
     * @param world
     */
    void destroySpecific(@Nullable World world);

    /**
     * 接辞がついていないダンジョン名を取得する
     *
     * @param affixedDungeonName
     * @return
     */
    @ParametersAreNonnullByDefault
    @Nonnull
    String getUnfixedDungeonName(String affixedDungeonName);

    /**
     * ダンジョンリストを文字列で取得
     *
     * @return
     */
    @Nonnull
    List<String> getDungeonListInfo();

    /**
     * 誰もいないダンジョンを削除する
     */
    void killEmptyDungeons();

    /**
     * ダンジョンのトラッカーを取得
     */
    @Nullable DungeonProgressionTracker<?> getTracker(World world);
}
