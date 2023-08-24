package net.riblab.tradecore.item.attribute;

import net.riblab.tradecore.mob.TCMob;

import java.util.Map;

public interface ICanSpawnMobOnUse {

    /**
     * 沸く敵の種類と確率(0~1)のテーブル
     */
    Map<TCMob, Float> getSpawnTable();
}
