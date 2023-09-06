/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.entity.projectile;

import de.tr7zw.nbtapi.NBTEntity;
import lombok.Getter;
import net.riblab.tradecore.general.NBTTagNames;
import org.bukkit.entity.Projectile;

import java.util.Objects;

public class TCProjectile implements ITCProjectile {

    /**
     * エンティティの種族
     */
    @Getter
    private final Class<? extends Projectile> baseClass;

    /**
     * エンティティの内部名称
     */
    @Getter
    private final String internalName;

    public TCProjectile(Class<? extends Projectile> baseClass, String internalName) {
        this.baseClass = baseClass;
        this.internalName = internalName;
    }

    @Override
    public void onSpawn(Projectile projectile, double damage) {
        NBTEntity nbtEntity = new NBTEntity(projectile);
        nbtEntity.getPersistentDataContainer().setString(NBTTagNames.PROJECTILEID.get(), internalName);
        nbtEntity.getPersistentDataContainer().setDouble(NBTTagNames.PROJECTILEDAMAGE.get(), damage);
    }

    @Override
    public boolean isSimilar(Projectile projectile) {
        if (Objects.isNull(projectile))
            return false;

        NBTEntity nbtEntity = new NBTEntity(projectile);
        String id = nbtEntity.getPersistentDataContainer().getString(NBTTagNames.PROJECTILEID.get());

        return Objects.equals(id, internalName);
    }

    @Override
    public double getDamage(Projectile projectile) {
        NBTEntity nbtEntity = new NBTEntity(projectile);
        return nbtEntity.getPersistentDataContainer().getDouble(NBTTagNames.PROJECTILEDAMAGE.get());
    }
}
