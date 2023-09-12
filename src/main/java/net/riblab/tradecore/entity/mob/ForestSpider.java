/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.entity.mob;

import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.attribute.AttributeInstance;
import me.gamercoder215.mobchip.ai.attribute.EntityAttribute;
import me.gamercoder215.mobchip.ai.memories.EntityMemory;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.general.ChanceFloat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Map;

public class ForestSpider extends TCMob {
    public ForestSpider() {
        super(EntityType.SPIDER, Component.text("フォレストスパイダー"), 10, "forest_spider", Map.of("primal_string", new ChanceFloat(1f)));
    }

    @Override
    public void spawn(Mob mob) {
        super.spawn(mob);
        EntityBrain brain = BukkitBrain.getBrain(mob);
        AttributeInstance attributeInstance2 = brain.getAttributeInstance(EntityAttribute.GENERIC_ATTACK_DAMAGE);
        attributeInstance2.setBaseValue(2f);
    }
}
