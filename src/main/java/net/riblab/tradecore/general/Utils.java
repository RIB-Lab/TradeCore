/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.general;

import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.craft.TCFurnaceRecipes;
import net.riblab.tradecore.entity.mob.TCMobs;
import net.riblab.tradecore.item.LootTables;
import net.riblab.tradecore.item.PlayerItemModService;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.job.skill.JobSkillService;
import net.riblab.tradecore.modifier.IModifier;
import net.riblab.tradecore.modifier.IToolStatsModifier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * 汎用関数を詰めたユーティリティクラス
 */
public final class Utils {

    /**
     * BukkitのOnDisableでエラーが出ないようにクラスを強制的にロードする
     */
    @ParametersAreNonnullByDefault
    @Nonnull
    public static <T> Class<T> forceInit(Class<T> klass) {
        try {
            Class.forName(klass.getName(), true, klass.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);  // Can't happen
        }
        return klass;
    }

    /**
     * プレイヤーの起こした行動によって発生した値をジョブスキルや装備modによって修飾する
     */
    @ParametersAreNonnullByDefault
    public static <T> T apply(Player player, T originalValue, Class<? extends IModifier<T>> clazz) {
        T modifiedValue = PlayerItemModService.getImpl().apply(player, originalValue, clazz);
        modifiedValue = JobSkillService.getImpl().apply(player, originalValue, modifiedValue, clazz);
        return modifiedValue;
    }

    /**
     * .jar内フォルダーを.jar外にコピーする
     *
     * @param srcDirName コピー前の.jar内のディレクトリの名前
     * @param destDir    .jarの外のディレクトリのパス
     * @throws IOException　コピーに失敗
     */
    @ParametersAreNonnullByDefault
    public static void copyFolder(String srcDirName, File destDir) throws IOException {
        final File jarFile = new File("plugins/TradeCore.jar");
        JarFile jar = null;
        try {
            jar = new JarFile(jarFile);
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().severe("プラグインの名前を変えないで下さい！リソースが展開できません！");
            e.printStackTrace();
        }
        for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().startsWith(srcDirName + "/") && !entry.isDirectory()) {
                File dest = new File(destDir, entry.getName().substring(srcDirName.length() + 1));
                File parent = dest.getParentFile();
                if (Objects.nonNull(parent)) {
                    parent.mkdirs();
                }
                try (FileOutputStream out = new FileOutputStream(dest); InputStream in = jar.getInputStream(entry)) {
                    byte[] buffer = new byte[8 * 1024];
                    int s = 0;
                    while ((s = in.read(buffer)) > 0) {
                        out.write(buffer, 0, s);
                    }
                }
            }
        }
        jar.close();
    }

    /**
     * .jar内ファイルを.jar外にコピーする
     *
     * @param srcFileName コピー前の.jar内のファイルの名前
     * @param destDir     .jarの外のディレクトリのパス
     * @return コピーに成功したかどうか
     * @throws IOException コピーに失敗
     */
    @ParametersAreNonnullByDefault
    public static boolean copyFile(String srcFileName, File destDir) throws IOException {
        final File jarFile = new File("plugins/TradeCore.jar");
        JarFile jar = null;
        boolean copied = false;
        try {
            jar = new JarFile(jarFile);
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().severe("プラグインの名前を変えないで下さい！リソースが展開できません！");
            e.printStackTrace();
        }
        for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().equals(srcFileName) && !entry.isDirectory()) {
                File parent = destDir.getParentFile();
                if (Objects.nonNull(parent)) {
                    parent.mkdirs();
                }
                FileOutputStream out = new FileOutputStream(destDir);
                InputStream in = jar.getInputStream(entry);
                try {
                    byte[] buffer = new byte[8 * 1024];
                    int s;
                    while ((s = in.read(buffer)) > 0) {
                        out.write(buffer, 0, s);
                    }
                } finally {
                    in.close();
                    out.close();
                }
                copied = true;
                break;
            }
        }
        jar.close();
        return copied;
    }

    /**
     * フォルダーを完全に削除する
     *
     * @param file　削除したいフォルダーのパス
     * @return 削除に成功したかどうか
     */
    @ParametersAreNonnullByDefault
    public static boolean deleteFolder(File file) {
        try (Stream<Path> files = Files.walk(file.toPath())) {
            files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 座標をxz軸にランダム化する
     *
     * @param location 座標
     * @param range    半径
     */
    @ParametersAreNonnullByDefault
    public static @Nonnull Location randomizeLocationXZ(Location location, int range) {
        Random random = new Random();
        double randomOffsetX = (random.nextDouble() * range * 2) - range;
        double randomOffsetZ = (random.nextDouble() * range * 2) - range;

        double newX = location.getX() + randomOffsetX;
        double newZ = location.getZ() + randomOffsetZ;

        return new Location(location.getWorld(), newX, location.getY(), newZ);
    }

    /**
     * ロード順によって競合の可能性のあるenumを安全に初期化する
     */
    public static void initializeEnumSafely() {
        TCItems.values();
        TCMobs.values();
        LootTables.values();
        TCFurnaceRecipes.values();

        JobType.values();
        IToolStatsModifier.ToolType.values();
    }

    /**
     * プラグインのバージョンを取得する
     * @return プラグインのバージョン
     */
    public static String getVersion(){
        return TradeCore.getInstance().getDescription().getVersion();
    }
}
