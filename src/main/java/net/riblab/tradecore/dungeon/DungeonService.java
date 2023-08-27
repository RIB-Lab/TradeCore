package net.riblab.tradecore.dungeon;

import net.riblab.tradecore.TradeCore;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.List;

public interface DungeonService {
    String tmpDirName = "dungeontemplate";
    String dungeonPrefix = "dungeons";
    String copySchemDir = "schematics";
    File pasteSchemDir = TradeCore.getInstance().getDataFolder();
    Vector dungeonGenerateLoc = new Vector(0,100,0);

    /**
     * データを基にダンジョンを作る
     */
    void create(IDungeonData data);

    /**
     * ダンジョンのインスタンスを作る
     *
     * @param data       名前
     * @param instanceID インスタンスのID。0未満なら0以上の最初に空いているインスタンス
     */
    void create(IDungeonData data, int instanceID);

    /**
     * ダンジョンのインスタンスが存在するかどうか
     */
    boolean isDungeonExist(IDungeonData data, int id);

    /**
     * ダンジョンのインスタンスに入る
     */
    void enter(Player player, IDungeonData data, int id);

    /**
     * プレイヤーをダンジョンから退出させる
     */
    void tryLeave(Player player);

    /**
     * プレイヤーをダンジョンから強制脱出させる、または無理矢理メインワールドに転送する
     */
    void evacuate(Player player);

    /**
     * プレイヤーがダンジョン内にいるかどうか
     */
    boolean isPlayerInDungeon(Player player);

    /**
     * 全てのダンジョンを削除する
     */
    void destroyAll();

    /**
     * 特定のダンジョンを削除する
     * @param world
     */
    void destroySpecific(World world);

    /**
     * 接辞がついていないダンジョン名を取得する
     * @param affixedDungeonName
     * @return
     */
    String getUnfixedDungeonName(String affixedDungeonName);

    /**
     * ダンジョンリストを文字列で取得
     * @return
     */
    List<String> getDungeonListInfo();

    /**
     * 誰もいないダンジョンを削除する
     */
    void killEmptyDungeons();
}
