package net.riblab.tradecore.mob;

import net.riblab.tradecore.TradeCore;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Random;

/**
 * モブ関連のユーティリティクラス
 */
public class MobUtils {
    
    private MobUtils(){
        
    }
    
    public static void trySpawnMobInRandomArea(Player player, Block block, Map<ITCMob, Float> table, int radius) {
        Random random = new Random();
        table.forEach((itcmob, aFloat) -> {
            float rand = random.nextFloat();
            if (rand < aFloat) {
                Location safeLocation = findSafeLocationToSpawn(block, radius);
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
}
