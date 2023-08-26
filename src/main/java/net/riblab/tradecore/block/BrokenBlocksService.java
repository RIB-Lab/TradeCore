package net.riblab.tradecore.block;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * ワールド中の破壊中のブロックを管理するサービス
 */
public class BrokenBlocksService implements IBrokenBlocksService {
    
    private static final Map<Player, BrokenBlock> brokenBlocks = new HashMap<>();

    @Override
    public void createBrokenBlock(Block block, Player player) {
        if (isPlayerAlreadyBreaking(player)) {
            removeBrokenBlock(player);
        }
        BrokenBlock brokenBlock = new BrokenBlock(block);
        brokenBlocks.put(player, brokenBlock);
    }

    private void removeBrokenBlock(Player player) {
        brokenBlocks.get(player).resetBlockObject(player);
    }

    @Override
    public BrokenBlock getBrokenBlock(Player player) {
        return brokenBlocks.get(player);
    }

    @Override
    public boolean isPlayerAlreadyBreaking(Player player) {
        return brokenBlocks.containsKey(player);
    }

    @Override
    public boolean isPlayerBreakingAnotherBlock(Player player, Location location) {
        return brokenBlocks.containsKey(player) && !brokenBlocks.get(player).getBlock().getLocation().equals(location);
    }
    
    @Override
    public BrokenBlock removePlayerFromMap(Player player){
        return brokenBlocks.remove(player);
    }
}
 
