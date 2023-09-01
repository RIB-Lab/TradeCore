package net.riblab.tradecore.entity.projectile;

import de.tr7zw.nbtapi.NBTEntity;
import lombok.Getter;
import net.riblab.tradecore.general.NBTTagNames;
import org.bukkit.entity.Projectile;

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
        if (projectile == null)
            return false;

        NBTEntity nbtEntity = new NBTEntity(projectile);
        String ID = nbtEntity.getPersistentDataContainer().getString(NBTTagNames.PROJECTILEID.get());

        if (ID == null)
            return false;

        return ID.equals(internalName);
    }

    @Override
    public double getDamage(Projectile projectile) {
        NBTEntity nbtEntity = new NBTEntity(projectile);
        return nbtEntity.getPersistentDataContainer().getDouble(NBTTagNames.PROJECTILEDAMAGE.get());
    }
}
