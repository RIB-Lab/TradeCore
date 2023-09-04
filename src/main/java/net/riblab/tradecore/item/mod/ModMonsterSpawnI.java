package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.entity.mob.ITCMob;
import net.riblab.tradecore.modifier.IMonsterSpawnModifier;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 使用時に確率で敵が沸くツールのmod
 */
public class ModMonsterSpawnI extends ItemMod<Map<ITCMob, Float>> implements IMonsterSpawnModifier {

    /**
     * @param spawnTable 沸くモブとその確率
     */
    public ModMonsterSpawnI(Map<ITCMob, Float> spawnTable) {
        super(spawnTable);
    }

    @Override
    public String getLore() {
        return null;
    }

    @Override
    public List<ITCMob> apply(List<ITCMob> originalValue, List<ITCMob> modifiedValue) {
        float rand = new Random().nextFloat();
        getParam().forEach((itcmob, aFloat) -> {
            if (rand < aFloat) {
                modifiedValue.add(itcmob);
            }
        });
        
        return modifiedValue;
    }
}
