package net.riblab.tradecore;

import com.google.common.collect.Multimap;
import net.riblab.tradecore.item.attribute.ITCItem;
import net.riblab.tradecore.modifier.IModifier;
import net.riblab.tradecore.modifier.IResourceChanceModifier;
import net.riblab.tradecore.mob.CustomMobService;
import net.riblab.tradecore.mob.TCMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

    public static void trySpawnMob(Player player, Block block, Map<TCMob, Float> table) {
        Random random = new Random();
        table.forEach((itcmob, aFloat) -> {
            float rand = random.nextFloat();
            if (rand < aFloat) {
                Location safeLocation = findSafeLocationToSpawn(block, 5);
                if (safeLocation != null)
                    CustomMobService.spawn(player, safeLocation, itcmob);
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
        modifiedValue = TradeCore.getInstance().getJobSkillHandler().apply(player, originalValue, modifiedValue, clazz);
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
}
