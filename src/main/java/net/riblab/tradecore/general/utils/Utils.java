package net.riblab.tradecore.general.utils;

import com.google.common.collect.Multimap;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.craft.TCCraftingRecipes;
import net.riblab.tradecore.craft.TCFurnaceRecipes;
import net.riblab.tradecore.item.LootTables;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.ITCTool;
import net.riblab.tradecore.job.data.JobData;
import net.riblab.tradecore.mob.TCMobs;
import net.riblab.tradecore.modifier.IModifier;
import net.riblab.tradecore.modifier.IResourceChanceModifier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * 汎用関数を詰めたユーティリティクラス
 */
public class Utils {

    /**
     * BukkitのOnDisableでエラーが出ないようにクラスを強制的にロードする
     *
     * @param klass
     * @param <T>
     * @return
     */
    public static <T> Class<T> forceInit(Class<T> klass) {
        try {
            Class.forName(klass.getName(), true, klass.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);  // Can't happen
        }
        return klass;
    }

    public static void dropItemByLootTable(Player player, Block block, Multimap<Float, ITCItem> table) {
        Random random = new Random();
        table.forEach((aFloat, itcItem) -> {
            float skillAppliedChance = Utils.apply(player, aFloat, IResourceChanceModifier.class);
            float rand = random.nextFloat();
            if (rand < skillAppliedChance) {
                block.getWorld().dropItemNaturally(block.getLocation(), itcItem.getItemStack());
            }
        });
    }
    
    /**
     * プレイヤーの起こした行動によって発生した値をジョブスキルや装備modによって修飾する
     */
    public static  <T> T apply(Player player, T originalValue, Class<? extends IModifier<T>> clazz){
        T modifiedValue = TradeCore.getInstance().getItemModService().apply(player, originalValue, clazz);
        modifiedValue = TradeCore.getInstance().getJobSkillService().apply(player, originalValue, modifiedValue, clazz);
        return modifiedValue;
    }

    public static void copyFolder(String srcDirName, File destDir) throws IOException {
        final File jarFile = new File("plugins/TradeCore.jar");
        JarFile jar = null;
        try {
            jar = new JarFile(jarFile);
        } catch (FileNotFoundException e){
            Bukkit.getLogger().severe("プラグインの名前を変えないで下さい！リソースが展開できません！");
            e.printStackTrace();
        }
        for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().startsWith(srcDirName + "/") && !entry.isDirectory()) {
                File dest = new File(destDir, entry.getName().substring(srcDirName.length() + 1));
                File parent = dest.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }
                FileOutputStream out = new FileOutputStream(dest);
                InputStream in = jar.getInputStream(entry);
                try {
                    byte[] buffer = new byte[8 * 1024];
                    int s = 0;
                    while ((s = in.read(buffer)) > 0) {
                        out.write(buffer, 0, s);
                    }
                } finally {
                    in.close();
                    out.close();
                }
            }
        }
        jar.close();
    }

    public static boolean copyFile(String srcFileName, File destDir) throws IOException {
        final File jarFile = new File("plugins/TradeCore.jar");
        JarFile jar = null;
        boolean copied = false;
        try {
            jar = new JarFile(jarFile);
        } catch (FileNotFoundException e){
            Bukkit.getLogger().severe("プラグインの名前を変えないで下さい！リソースが展開できません！");
            e.printStackTrace();
        }
        for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().equals(srcFileName) && !entry.isDirectory()) {
                File parent = destDir.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }
                FileOutputStream out = new FileOutputStream(destDir);
                InputStream in = jar.getInputStream(entry);
                try {
                    byte[] buffer = new byte[8 * 1024];
                    int s = 0;
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
    
    public static boolean deleteFolder(File file) {
        try (Stream<Path> files = Files.walk(file.toPath())) {
            files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Location randomizeLocation(Location location, int range) {
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
    public static void initializeEnumSafely(){
        TCItems.values();
        TCMobs.values();
        LootTables.values();
        TCCraftingRecipes.values();
        TCFurnaceRecipes.values();

        JobData.JobType.values();
        ITCTool.ToolType.values();
    }
}
