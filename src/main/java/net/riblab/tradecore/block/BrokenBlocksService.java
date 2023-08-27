package net.riblab.tradecore.block;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface BrokenBlocksService {

    /**
     * プレイヤーがブロックを壊し始めたという情報をBrokenBlockの形で保存する
     */
    void createBrokenBlock(Block block, Player player);

    /**
     * プレイヤーがブロックにひびを入れたことをBrokenBlockに通知する
     * @param amount ひびの大きさ(11が最大値)
     */
    void incrementDamage(Player player, double amount);

    /**
     * プレイヤーが現在あるブロックを破壊中であるか
     */
    boolean isPlayerAlreadyBreaking(Player player);

    /**
     * プレイヤーが他のブロックを破壊中であるか
     * @param player プレイヤー
     * @param location 判定したいブロックがある座標
     */
    boolean isPlayerBreakingAnotherBlock(Player player, Location location);

    /**
     * このサービスが持つプレイヤーの破壊情報を削除する(破壊中のブロックは削除されない)
     */
    BrokenBlock removePlayerFromMap(Player player);
}
