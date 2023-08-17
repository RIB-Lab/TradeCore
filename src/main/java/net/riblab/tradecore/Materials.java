package net.riblab.tradecore;

import org.bukkit.Material;

import java.util.Set;

/**
 * ドロップテーブルなどで使うマテリアルの組み合わせ達
 */
public class Materials {
    /**
     * 壊すことのできないマテリアル
     */
    public static final Set<Material> unbreakableMaterial = Set.of(
            Material.BEDROCK, Material.COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK,
            Material.BARRIER, Material.END_PORTAL_FRAME, Material.END_PORTAL, Material.NETHER_PORTAL, Material.STRUCTURE_BLOCK,
            Material.LIGHT);

    /**
     * 葉っぱ
     */
    public static final Set<Material> leaves = Set.of(Material.ACACIA_LEAVES, Material.AZALEA_LEAVES, Material.BIRCH_LEAVES, Material.CHERRY_LEAVES, Material.DARK_OAK_LEAVES
            , Material.FLOWERING_AZALEA_LEAVES, Material.JUNGLE_LEAVES, Material.MANGROVE_LEAVES, Material.OAK_LEAVES, Material.SPRUCE_LEAVES);

    /**
     * 原木
     */
    public static final Set<Material> logs = Set.of(Material.ACACIA_LOG, Material.BIRCH_LOG, Material.CHERRY_LOG, Material.JUNGLE_LOG, Material.DARK_OAK_LOG
            , Material.MANGROVE_LOG, Material.OAK_LOG, Material.SPRUCE_LOG);

    /**
     * レイキャストを阻害しない通過ブロック
     */
    public static final Set<Material> transparentBlocks = Set.of(Material.WATER, Material.LAVA, Material.AIR);

    /**
     * 土系
     */
    public static final Set<Material> dirts = Set.of(Material.DIRT, Material.GRASS_BLOCK, Material.DIRT_PATH, Material.COARSE_DIRT, Material.ROOTED_DIRT, Material.PODZOL);

    /**
     * 石と丸石系
     */
    public static final Set<Material> primitiveStones = Set.of(Material.COBBLESTONE, Material.STONE, Material.COBBLESTONE_SLAB, Material.STONE_SLAB);

    /**
     * 単純木材系
     */
    public static final Set<Material> primitivePlanks = Set.of(Material.ACACIA_PLANKS, Material.BIRCH_PLANKS, Material.BAMBOO_PLANKS, Material.CHERRY_PLANKS, Material.CRIMSON_PLANKS,
            Material.JUNGLE_PLANKS, Material.MANGROVE_PLANKS, Material.OAK_PLANKS, Material.DARK_OAK_PLANKS, Material.SPRUCE_PLANKS);

    /**
     * 買えたらまずい便利ブロック
     */
    public static final Set<Material> coreBlock = Set.of(Material.CRAFTING_TABLE, Material.FURNACE, Material.TORCH);
}
