/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.dungeon;

import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

/**
 * 敵を殲滅するダンジョンの目標を定義する
 */
class DPTExtermination extends DungeonProgressionTracker<Integer> implements IPlayerKillHandler {
    /**
     * 殺した数
     */
    int killedCount = 0;

    public DPTExtermination(Integer objective, World instance) {
        super(objective, instance);
    }

    public void onPlayerKill(Mob mob) {
        killedCount++;
        if (killedCount >= getObjective() && isActive) {
            onComplete.accept(getInstance());
            isActive = false;
        }
    }

    public void onDungeonSecond(Player player) {
        player.sendActionBar(Component.text("敵を倒せ：" + killedCount + "/" + getObjective()));
    }
}
