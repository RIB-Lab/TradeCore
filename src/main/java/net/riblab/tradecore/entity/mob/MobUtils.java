/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.entity.mob;

import de.tr7zw.nbtapi.NBTEntity;
import net.riblab.tradecore.general.NBTTagNames;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * モブ関連のユーティリティクラス
 */
public final class MobUtils {

    private MobUtils() {

    }

    /**
     * モブをプレイヤー周辺のランダムな場所にスポーンさせる
     *
     * @param player モブがターゲットとするプレイヤー
     * @param block  モブがスポーンする起点のブロック
     * @param mobs 沸かせたいモブのリスト
     * @param radius スポーンする半径
     */
    @ParametersAreNonnullByDefault
    public static void trySpawnMobInRandomArea(Player player, Block block, List<ITCMob> mobs, int radius) {
        for (ITCMob mob : mobs) {
            findSafeLocationToSpawn(block, radius).ifPresent(location -> CustomMobService.getImpl().spawn(player, location, mob));
        }
    }

    /**
     * モブがスポーンできる安全な場所を探す
     */
    @ParametersAreNonnullByDefault
    private static Optional<Location> findSafeLocationToSpawn(Block block, int radius) {
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            Block tryBlock = block.getRelative(random.nextInt(radius * 2) - radius + 1, random.nextInt(radius * 2) - radius, random.nextInt(radius * 2) - radius);
            if (tryBlock.getType() != Material.AIR || tryBlock.getRelative(BlockFace.UP).getType() != Material.AIR)
                continue;

            return Optional.of(tryBlock.getLocation().add(new Vector(0.5f, 0, 0.5f)));
        }

        return Optional.empty(); //何回探しても安全な場所がなかったらモブのスポーンを諦める
    }

    /**
     * モブにドロップ品を落とすかどうかの設定を追加
     */
    @ParametersAreNonnullByDefault
    public static void setLootableTag(Mob mob, boolean flag) {
        NBTEntity nbtEntity = new NBTEntity(mob);
        nbtEntity.getPersistentDataContainer().setBoolean(NBTTagNames.MOB_ISLOOTABLE.get(), flag);
    }

    /**
     * モブがアイテムを落とすことのできるタグがついているか
     */
    @ParametersAreNonnullByDefault
    public static boolean isLootable(Mob mob) {
        NBTEntity nbtEntity = new NBTEntity(mob);
        return nbtEntity.getPersistentDataContainer().getBoolean(NBTTagNames.MOB_ISLOOTABLE.get());
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

        setLootableTag(mob, true);
        mob.damage(damage);
        if (Objects.nonNull(knockback))
            mob.setVelocity(knockback);
    }
}
