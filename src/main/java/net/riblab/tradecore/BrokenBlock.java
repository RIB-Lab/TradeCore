package net.riblab.tradecore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import lombok.Getter;
import org.bukkit.SoundCategory;
import org.bukkit.SoundGroup;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class BrokenBlock {
    @Getter
    private double damage = -1;
    
    @Getter
    private final Block block;

    public BrokenBlock(Block block) {
        this.block = block;
    }
    
    public void incrementDamage(Player from, double amount) {
        if (isBroken()) return;
        
        damage += amount;

        if (damage < 10) {
            sendBreakPacket(from);
        } else {
            breakBlock(from);
        }
    }

    public boolean isBroken() {
        return getDamage() >= 10;
    }

    public void breakBlock(Player breaker) {
        damage = -1;
        sendBreakPacket(breaker);
        BrokenBlocksService.getBrokenBlocks().remove(breaker);
//        SoundPlayerUtils.playBlockSound(block);
        if (breaker == null) return;

        SoundGroup soundGroup = block.getBlockData().getSoundGroup();
        breaker.playSound(block.getLocation(), soundGroup.getBreakSound(), SoundCategory.BLOCKS, 1f, 1f);
        breaker.breakBlock(block);
    }

    public void resetBlockObject(Player player) {
        damage = -1;
        sendBreakPacket(player);
        BrokenBlocksService.getBrokenBlocks().remove(player);
    }

    public void sendBreakPacket(Player player) {
        PacketContainer blockBreak = TradeCore.getInstance().getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
        
        blockBreak.getBlockPositionModifier().write(0, getBlockPosition(block));

        blockBreak.getIntegers()
                .write(0, block.getLocation().hashCode())
                .write(1, (int)getDamage());
        
        try {
            TradeCore.getInstance().getProtocolManager().sendServerPacket(player, blockBreak);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
    
    private BlockPosition getBlockPosition(Block block) {
        return new BlockPosition(block.getX(), block.getY(), block.getZ());
    }
}