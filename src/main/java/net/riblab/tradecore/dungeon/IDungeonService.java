package net.riblab.tradecore.dungeon;

import net.riblab.tradecore.TradeCore;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.List;

public interface IDungeonService {
    String tmpDirName = "dungeontemplate";
    String dungeonPrefix = "dungeons";
    String copySchemDir = "schematics";
    File pasteSchemDir = TradeCore.getInstance().getDataFolder();
    Vector dungeonGenerateLoc = new Vector(0,100,0);
    
    void create(IDungeonData data);

    /**
     * ダンジョンを作る
     *
     * @param data       名前
     * @param instanceID インスタンスのID。0未満なら0以上の最初に空いているインスタンス
     */
    World create(IDungeonData data, int instanceID);

    boolean isDungeonExist(IDungeonData data, int id);

    void enter(Player player, IDungeonData data, int id);

    void enter(Player player, World world);

    void tryLeave(Player player);

    void evacuate(Player player);

    boolean isPlayerInDungeon(Player player);

    void destroyAll();

    void destroySpecific(World world);

    String getUnfixedDungeonName(String affixedDungeonName);

    List<String> getDungeonListInfo();

    void killEmptyDungeons();
}
