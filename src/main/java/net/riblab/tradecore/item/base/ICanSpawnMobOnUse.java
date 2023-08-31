package net.riblab.tradecore.item.base;

import net.riblab.tradecore.entity.mob.ITCMob;

import java.util.Map;

public interface ICanSpawnMobOnUse {

    /**
     * 沸く敵の種類と確率(0~1)のテーブル
     */
    Map<ITCMob, Float> getSpawnTable();
}
