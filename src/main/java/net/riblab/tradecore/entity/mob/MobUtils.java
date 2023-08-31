package net.riblab.tradecore.entity.mob;

import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Random;

/**
 * モブ関連のユーティリティクラス
 */
public final class MobUtils {

    private static final String lootableTag = "lootable";

    private MobUtils() {

    }

    /**
     * モブをプレイヤー周辺のランダムな場所にスポーンさせる
     *
     * @param player モブがターゲットとするプレイヤー
     * @param block  モブがスポーンする起点のブロック
     * @param table  スポーンテーブル
     * @param radius スポーンする半径
     */
    @ParametersAreNonnullByDefault
    public static void trySpawnMobInRandomArea(Player player, Block block, Map<ITCMob, Float> table, int radius) {
        Random random = new Random();
        table.forEach((itcmob, aFloat) -> {
            float rand = random.nextFloat();
            if (rand < aFloat) {
                Location safeLocation = findSafeLocationToSpawn(block, radius);
                if (safeLocation != null)
                    CustomMobService.getImpl().spawn(player, safeLocation, itcmob);
            }
        });
    }

    /**
     * モブがスポーンできる安全な場所を探す
     */
    @Nullable
    @ParametersAreNonnullByDefault
    private static Location findSafeLocationToSpawn(Block block, int radius) {
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            Block tryBlock = block.getRelative(random.nextInt(radius * 2) - radius + 1, random.nextInt(radius * 2) - radius, random.nextInt(radius * 2) - radius);
            if (tryBlock.getType() != Material.AIR || tryBlock.getRelative(BlockFace.UP).getType() != Material.AIR)
                continue;

            return tryBlock.getLocation().add(new Vector(0.5f, 0, 0.5f));
        }

        return null; //何回探しても安全な場所がなかったらモブのスポーンを諦める
    }

    /**
     * モブにドロップ品を落とすかどうかの設定を追加
     */
    @ParametersAreNonnullByDefault
    public static void setLootableTag(Mob mob, boolean flag) {
        NBTEntity nbtEntity = new NBTEntity(mob);
        nbtEntity.getPersistentDataContainer().setBoolean(lootableTag, flag);
    }

    /**
     * モブがアイテムを落とすことのできるタグがついているか
     */
    @ParametersAreNonnullByDefault
    public static boolean isLootable(Mob mob) {
        NBTEntity nbtEntity = new NBTEntity(mob);
        return nbtEntity.getPersistentDataContainer().getBoolean(lootableTag);
    }

    /**
     * プレイヤーがモブにダメージを与える
     *
     * @param mob       モブ
     * @param damage    ダメージ量
     * @param knockback ノックバックのベクトル
     */
    @ParametersAreNonnullByDefault
    public static void tryDealDamageByPlayer(Mob mob, double damage, @Nullable Vector knockback) {
        if (mob.getType() == EntityType.VILLAGER) //ショップ店員を殴るの防止
            return;

        mob.damage(damage);
        if (knockback != null)
            mob.setVelocity(knockback);
        setLootableTag(mob, true);
    }
}
