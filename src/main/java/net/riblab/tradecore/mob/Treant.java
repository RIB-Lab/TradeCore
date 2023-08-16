package net.riblab.tradecore.mob;

import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.attribute.AttributeInstance;
import me.gamercoder215.mobchip.ai.attribute.EntityAttribute;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.item.TCItems;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * 斧で木を殴った時に出てくるモンスター
 */
public class Treant extends TCMob {
    public Treant() {
        super(EntityType.ZOMBIE, Component.text("トレント"), 4, "basic_treant", Map.of(TCItems.BARK.get().getItemStack(), 1f));
    }

    @Override
    public void spawn(Mob mob) {
        super.spawn(mob);
        EntityEquipment equipment = mob.getEquipment();
        equipment.setHelmet(new ItemStack(Material.OAK_LOG));

        EntityBrain brain = BukkitBrain.getBrain(mob);
        AttributeInstance attributeInstance = brain.getAttributeInstance(EntityAttribute.GENERIC_MOVEMENT_SPEED);
        attributeInstance.setBaseValue(0.1f);
        AttributeInstance attributeInstance2 = brain.getAttributeInstance(EntityAttribute.GENERIC_ATTACK_DAMAGE);
        attributeInstance2.setBaseValue(2f);
    }
}
