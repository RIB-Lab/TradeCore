package net.riblab.tradecore;

import net.riblab.tradecore.item.ITCItem;
import net.riblab.tradecore.mob.CustomMobService;
import net.riblab.tradecore.mob.TCMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Random;

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

    public static void dropItemByLootTable(Block block, Map<Float, ITCItem> table) {
        Random random = new Random();
        table.forEach((aFloat, itcItem) -> {
            float rand = random.nextFloat();
            if (rand < aFloat) {
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
}
