package net.riblab.tradecore;

import org.bukkit.Material;

import java.util.Set;

public class Materials {
    public static final Set<Material> unbreakableMaterial = Set.of(
            Material.BEDROCK, Material.COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK,
            Material.BARRIER, Material.END_PORTAL_FRAME, Material.END_PORTAL, Material.NETHER_PORTAL, Material.STRUCTURE_BLOCK);

    public static final Set<Material> leaves = Set.of(Material.ACACIA_LEAVES, Material.AZALEA_LEAVES, Material.BIRCH_LEAVES, Material.CHERRY_LEAVES, Material.DARK_OAK_LEAVES
            , Material.FLOWERING_AZALEA_LEAVES, Material.JUNGLE_LEAVES, Material.MANGROVE_LEAVES, Material.OAK_LEAVES, Material.SPRUCE_LEAVES);

    public static final Set<Material> logs = Set.of(Material.ACACIA_LOG, Material.BIRCH_LOG, Material.CHERRY_LOG, Material.JUNGLE_LOG, Material.DARK_OAK_LOG
            , Material.MANGROVE_LOG, Material.OAK_LOG, Material.SPRUCE_LOG);
}
