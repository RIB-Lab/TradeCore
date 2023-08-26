package net.riblab.tradecore;

import com.google.common.collect.Multimap;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.modifier.IModifier;
import net.riblab.tradecore.modifier.IResourceChanceModifier;
import net.riblab.tradecore.mob.ITCMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

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
     * カスタムブロック破壊を実装
     *
     * @param player
     */
    public static void addSlowDig(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, -1, -1, false, false), true);
    }

    /**
     * カスタムブロック破壊を除去
     *
     * @param player
     */
    public static void removeSlowDig(Player player) {
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
    }

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

    public static void trySpawnMob(Player player, Block block, Map<ITCMob, Float> table) {
        Random random = new Random();
        table.forEach((itcmob, aFloat) -> {
            float rand = random.nextFloat();
            if (rand < aFloat) {
                Location safeLocation = findSafeLocationToSpawn(block, 5);
                if (safeLocation != null)
                    TradeCore.getInstance().getCustomMobService().spawn(player, safeLocation, itcmob);
            }
        });
    }

    public static Location findSafeLocationToSpawn(Block block, int radius) {
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            Block tryBlock = block.getRelative(random.nextInt(radius * 2) - radius + 1, random.nextInt(radius * 2) - radius, random.nextInt(radius * 2) - radius);
            if (tryBlock.getType() != Material.AIR || tryBlock.getRelative(BlockFace.UP).getType() != Material.AIR)
                continue;

            return tryBlock.getLocation().add(new Vector(0.5f, 0, 0.5f));
        }

        return null; //何回探しても安全な場所がなかったらモブのスポーンを諦める
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

    public static List<Block> getBlocksInRadius(Player player, int radius, Material material) {
        Location playerLocation = player.getLocation();
        World world = player.getWorld();
        int xCenter = playerLocation.getBlockX();
        int yCenter = playerLocation.getBlockY();
        int zCenter = playerLocation.getBlockZ();

        int startX = xCenter - radius;
        int startY = yCenter - radius;
        int startZ = zCenter - radius;

        int endX = xCenter + radius;
        int endY = yCenter + radius;
        int endZ = zCenter + radius;

        List<Block> blocks = new ArrayList<>();

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if(block.getType() == material)
                        blocks.add(block);
                }
            }
        }

        return blocks;
    }

    public static Location randomizeLocation(Location location, int range) {
        Random random = new Random();
        double randomOffsetX = (random.nextDouble() * range * 2) - range;
        double randomOffsetZ = (random.nextDouble() * range * 2) - range; 

        double newX = location.getX() + randomOffsetX;
        double newZ = location.getZ() + randomOffsetZ;

        return new Location(location.getWorld(), newX, location.getY(), newZ);
    }
}
