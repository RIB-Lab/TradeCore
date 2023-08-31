package net.riblab.tradecore.dungeon;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.general.Utils;
import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

final class DungeonBuilder {

    private static final String tmpDirName = "dungeontemplate";
    private static final String copySchemDir = "schematics";
    private static final File pasteSchemDir = TradeCore.getInstance().getDataFolder();
    private static final Vector dungeonGenerateLoc = new Vector(0, 100, 0);

    /**
     * ダンジョンを建設する
     *
     * @param data               ダンジョンのデータ
     * @param affixedDungeonName ダンジョンのインスタンスID込みの名前
     * @param schemName          ダンジョンを建設するのに使うschematicの名前
     * @return 建設されたダンジョン
     */
    public static World build(IDungeonData<?> data, String affixedDungeonName, String schemName) {
        //ワールドをresourceからコピー
        File destDir = new File(affixedDungeonName);
        try {
            Utils.copyFolder(tmpDirName, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File uidFile = new File(affixedDungeonName + "/uid.dat");
        uidFile.delete();
        WorldCreator wc = new WorldCreator(affixedDungeonName, new NamespacedKey(TradeCore.getInstance(), affixedDungeonName));
        wc.generator(new EmptyChunkGenerator());
        World world = Bukkit.getServer().createWorld(wc);
        
        if(world == null){
            Bukkit.getLogger().severe("ワールドの生成に失敗しました");
            return null;
        }

        //ダンジョン名に対応したschemをresourceからコピーする
        File instantiatedSchemFile = new File(pasteSchemDir + "/" + schemName + ".schem");
        boolean fileHasCopied = false;
        try {
            fileHasCopied = Utils.copyFile(copySchemDir + "/" + schemName + ".schem", instantiatedSchemFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!fileHasCopied) {
            Bukkit.getLogger().severe("schemファイルが見つかりません：" + copySchemDir + "/" + schemName + ".schem");
            return world;
        }

        //schemから地形生成
        Clipboard clipboard;
        ClipboardFormat format = ClipboardFormats.findByFile(instantiatedSchemFile);
        try {
            assert format != null;
            try (ClipboardReader reader = format.getReader(new FileInputStream(instantiatedSchemFile))) {
                clipboard = reader.read();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(dungeonGenerateLoc.getX(), dungeonGenerateLoc.getY(), dungeonGenerateLoc.getZ()))
                    .copyEntities(true)
                    .build();
            Operations.complete(operation);
        }

        world.setAutoSave(false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setTime(6000);
        Vector loc = data.getSpawnPoint();
        world.setSpawnLocation(new Location(world, loc.getX(), loc.getY(), loc.getZ()));
        return world;
    }

    /**
     * 空のチャンクジェネレータ
     */
    static class EmptyChunkGenerator extends ChunkGenerator {
    }
}
