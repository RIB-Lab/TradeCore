package net.riblab.tradecore.block;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import lombok.Getter;
import net.riblab.tradecore.TradeCore;
import org.bukkit.SoundCategory;
import org.bukkit.SoundGroup;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * クライアントが破壊中のブロックをサーバーサイドで保存するためのクラス
 */
public class BrokenBlock {

    /**
     * 現在のひび割れの度合(-1~10)
     */
    @Getter
    private double damage = -1;

    /**
     * 破壊されているブロック
     */
    @Getter
    private final Block block;

    public BrokenBlock(Block block) {
        this.block = block;
    }

    private IBrokenBlocksService getService(){
        return TradeCore.getInstance().getBrokenBlocksService();
    }
    
    /**
     * ブロックの破壊度を上げる
     * @param from ブロックを破壊しているプレイヤー
     * @param amount 上げる破壊度
     */
    public void incrementDamage(Player from, double amount) {
        if (isBroken()) return;

        damage += amount;

        if (damage < 10) {
            sendBreakPacket(from);
        } else {
            breakBlock(from);
        }
    }

    /**
     * ブロックが完全に破壊されたどうか
     */
    public boolean isBroken() {
        return getDamage() >= 10;
    }

    /**
     * ブロックを完全に破壊する
     * @param breaker 破壊者
     */
    public void breakBlock(Player breaker) {
        damage = -1;
        sendBreakPacket(breaker);
        getService().removePlayerFromMap(breaker);
//        SoundPlayerUtils.playBlockSound(block);
        if (breaker == null) return;

        SoundGroup soundGroup = block.getBlockData().getSoundGroup();
        breaker.playSound(block.getLocation(), soundGroup.getBreakSound(), SoundCategory.BLOCKS, 1f, 1f);
        breaker.breakBlock(block);
    }

    /**
     * ブロックの破壊を中止し、ひび割れを0にする
     */
    public void resetBlockObject(Player player) {
        damage = -1;
        sendBreakPacket(player);
        getService().removePlayerFromMap(player);
    }

    /**
     * プレイヤーにブロックのひび割れのパケットを送る
     */
    public void sendBreakPacket(Player player) {
        PacketContainer blockBreak = TradeCore.getInstance().getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);

        blockBreak.getBlockPositionModifier().write(0, getBlockPosition(block));

        blockBreak.getIntegers()
                .write(0, block.getLocation().hashCode())
                .write(1, (int) getDamage());

        TradeCore.getInstance().getProtocolManager().sendServerPacket(player, blockBreak);
    }

    /**
     * ブロックの場所をBlockPositionの形に変換
     */
    private BlockPosition getBlockPosition(Block block) {
        return new BlockPosition(block.getX(), block.getY(), block.getZ());
    }
}