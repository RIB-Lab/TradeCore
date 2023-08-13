package net.riblab.tradecore;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BrokenBlocksService {

    @Getter
    private static final Map<Player, BrokenBlock> brokenBlocks = new HashMap<>();

    public static void createBrokenBlock(Block block, Player player) {
        if (isPlayerAlreadyBreaking(player)){
            removeBrokenBlock(player);
        }
        BrokenBlock brokenBlock = new BrokenBlock(block);
        brokenBlocks.put(player, brokenBlock);
    }

    public static void removeBrokenBlock(Player player) {
        brokenBlocks.get(player).resetBlockObject(player);
    }

    public static BrokenBlock getBrokenBlock(Player player) {
        return brokenBlocks.get(player);
    }

    public static boolean isPlayerAlreadyBreaking(Player player) {
        return brokenBlocks.containsKey(player);
    }

    public static boolean isPlayerBreakingAnotherBlock(Player player, Location location) {
        return brokenBlocks.containsKey(player) && !brokenBlocks.get(player).getBlock().getLocation().equals(location);
    }
}
 
