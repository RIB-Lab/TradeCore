/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.block;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public sealed interface BrokenBlocksService permits BrokenBlocksServiceImpl {

    static BrokenBlocksService getImpl() {
        return BrokenBlocksServiceImpl.INSTANCE;
    }

    /**
     * プレイヤーがブロックを壊し始めたという情報をBrokenBlockの形で保存する
     */
    @ParametersAreNonnullByDefault
    void createBrokenBlock(Block block, Player player);

    /**
     * プレイヤーがブロックにひびを入れたことをBrokenBlockに通知する
     *
     * @param amount ひびの大きさ(11が最大値)
     */
    @ParametersAreNonnullByDefault
    void incrementDamage(Player player, double amount);

    /**
     * プレイヤーが現在あるブロックを破壊中であるか
     */
    boolean isPlayerAlreadyBreaking(@Nullable Player player);

    /**
     * プレイヤーが他のブロックを破壊中であるか
     *
     * @param player   プレイヤー
     * @param location 判定したいブロックがある座標
     */
    boolean isPlayerBreakingAnotherBlock(@Nullable Player player, @Nullable Location location);

    /**
     * このサービスが持つプレイヤーの破壊情報を削除する(破壊中のブロックは削除されない)
     */
    @Nullable
    BrokenBlock removePlayerFromMap(@Nullable Player player);
}
