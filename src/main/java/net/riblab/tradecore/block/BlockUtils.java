package net.riblab.tradecore.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public final class BlockUtils {

    private BlockUtils() {

    }


    /**
     * カスタムブロック破壊を実装
     *
     * @param player
     */
    @ParametersAreNonnullByDefault
    public static void addSlowDig(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, -1, -1, false, false), true);
    }

    /**
     * カスタムブロック破壊を除去
     *
     * @param player
     */
    @ParametersAreNonnullByDefault
    public static void removeSlowDig(Player player) {
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
    }

    /**
     * あるプレイヤー周辺にあるとあるマテリアルのブロックを洗い出す
     *
     * @param player   プレイヤー
     * @param radius   半径
     * @param material マテリアル
     * @return 見つかったブロック
     */
    @ParametersAreNonnullByDefault
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
                    if (block.getType() == material)
                        blocks.add(block);
                }
            }
        }

        return blocks;
    }
}
