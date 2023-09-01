package net.riblab.tradecore.general;

import org.bukkit.entity.Player;

public interface AttackCooldownService {
    static AttackCooldownService getImpl(){
        return AttackCooldownServiceImpl.INSTANCE;
    }
    
    void add(Player player, double duration);

    double getCooldown(Player player);

    /**
     * Removeしたフレームと同時に別のクールダウンを差し込むとRunnableがバグるので注意
     */
    void forceRemove(Player player);
}
