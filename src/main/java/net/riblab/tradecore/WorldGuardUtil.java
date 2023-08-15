package net.riblab.tradecore;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;

/**
 * WG関連を隔離するクラス
 */
public class WorldGuardUtil {

    /**
     * プレイヤーがWGで保護された区域のブロックを破壊できるかどうか
     */
    public static boolean canBreakBlockWithWG(Player p, Block b) {
        BlockVector3 vector = BlockVector3.at(b.getX(), b.getY(), b.getZ());
        RegionManager rm = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(b.getWorld()));
        ApplicableRegionSet rs = rm.getApplicableRegions(vector);
        return rs.testState(TradeCore.getPlugin(WorldGuardPlugin.class).wrapPlayer(p), Flags.BUILD);
    }
}
