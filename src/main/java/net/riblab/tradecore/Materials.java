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

    public static final Set<Material> transparentBlocks = Set.of(Material.WATER, Material.LAVA, Material.AIR);

    public static final Set<Material> dirts = Set.of(Material.DIRT, Material.GRASS_BLOCK, Material.DIRT_PATH, Material.COARSE_DIRT, Material.ROOTED_DIRT);
    
    public static final Set<Material> primitiveStones = Set.of(Material.COBBLESTONE, Material.STONE, Material.COBBLESTONE_SLAB, Material.STONE_SLAB);
    
    public static final Set<Material> primitivePlanks = Set.of(Material.ACACIA_PLANKS, Material.BIRCH_PLANKS, Material.BAMBOO_PLANKS, Material.CHERRY_PLANKS, Material.CRIMSON_PLANKS,
            Material.JUNGLE_PLANKS, Material.MANGROVE_PLANKS, Material.OAK_PLANKS, Material.DARK_OAK_PLANKS, Material.SPRUCE_PLANKS);
}
