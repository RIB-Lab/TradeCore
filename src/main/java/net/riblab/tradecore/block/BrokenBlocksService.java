package net.riblab.tradecore.block;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface BrokenBlocksService {
    void createBrokenBlock(Block block, Player player);

    BrokenBlock getBrokenBlock(Player player);

    boolean isPlayerAlreadyBreaking(Player player);

    boolean isPlayerBreakingAnotherBlock(Player player, Location location);

    BrokenBlock removePlayerFromMap(Player player);
}
