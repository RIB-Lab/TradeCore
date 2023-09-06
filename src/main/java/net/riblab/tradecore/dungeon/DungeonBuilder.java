/*
 * Copyright (c) 2023. RIBLaB 
 */
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public final class DungeonBuilder {

    private static final String TEMP_DIR_NAME = "dungeontemplate";
    private static final String COPY_SCHEM_DIR = "schematics";
    private static final File PASTE_SCHEM_DIR = new File(TradeCore.getInstance().getDataFolder(), "tmp");
    private static final Vector DUNGEON_GENERATE_LOC = new Vector(0, 100, 0);

    /**
     * ダンジョンを建設する
     *
     * @param data               ダンジョンのデータ
     * @param affixedDungeonName ダンジョンのインスタンスID込みの名前
     * @param schemName          ダンジョンを建設するのに使うschematicの名前
     * @return 建設されたダンジョン
     */
    public static World build(IDungeonData<?> data, String affixedDungeonName, String schemName) {
        World world = copyWorldFolder(affixedDungeonName);
                
        File instantiatedSchemFile = copySchem(schemName);
        if(Objects.isNull(instantiatedSchemFile)){
            return world;
        }
        
        generateTerrainAsync(world, instantiatedSchemFile);

        setupWorld(world, data);
        
        return world;
    }

    /**
     * 空のワールドをResourceフォルダからコピーして、新しいワールドのデータを作る
     */
    private static World copyWorldFolder(String affixedDungeonName){
        File destDir = new File(affixedDungeonName);
        try {
            Utils.copyFolder(TEMP_DIR_NAME, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File uidFile = new File(destDir, "uid.dat");
        uidFile.delete();
        WorldCreator wc = new WorldCreator(affixedDungeonName, new NamespacedKey(TradeCore.getInstance(), affixedDungeonName));
        wc.generator(new EmptyChunkGenerator());
        World world = Bukkit.getServer().createWorld(wc);

        Objects.requireNonNull(world, "ワールドの生成に失敗しました");
        
        return world;
    }

    /**
     * ダンジョン名に対応したschemをresourceからコピーする
     */
    private static File copySchem(String schemName){
        File instantiatedSchemFile = new File(PASTE_SCHEM_DIR, schemName + ".schem");
        boolean fileHasCopied = false;
        try {
            fileHasCopied = Utils.copyFile(COPY_SCHEM_DIR + "/" + schemName + ".schem", instantiatedSchemFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!fileHasCopied) {
            Bukkit.getLogger().severe("schemファイルが見つかりません：" + COPY_SCHEM_DIR + "/" + schemName + ".schem");
            return null;
        }
        
        return instantiatedSchemFile;
    }

    /**
     * schemから地形生成
     */
    private static void generateTerrainAsync(World world, File instantiatedSchemFile){
        Clipboard clipboard;
        ClipboardFormat format = ClipboardFormats.findByFile(instantiatedSchemFile);
        try {
            assert Objects.nonNull(format);
            try (ClipboardReader reader = format.getReader(new FileInputStream(instantiatedSchemFile))) {
                clipboard = reader.read();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        new BukkitRunnable(){
            @Override
            public void run() {
                try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(BlockVector3.at(DUNGEON_GENERATE_LOC.getX(), DUNGEON_GENERATE_LOC.getY(), DUNGEON_GENERATE_LOC.getZ()))
                            .copyEntities(true)
                            .build();
                    Operations.complete(operation);
                }
            }
        }.runTaskAsynchronously(TradeCore.getInstance());
    }

    /**
     * 生成したワールドの初期設定を行う
     */
    private static void setupWorld(World world, IDungeonData<?> data){
        world.setAutoSave(false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setTime(6000);
        Vector loc = data.getSpawnPoint();
        world.setSpawnLocation(new Location(world, loc.getX(), loc.getY(), loc.getZ()));
    }

    /**
     * 空のチャンクジェネレータ
     */
    static class EmptyChunkGenerator extends ChunkGenerator {
    }
}