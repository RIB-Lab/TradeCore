package net.riblab.tradecore;

public interface IPlayerStats {

    int defaultMaxHP = 20;
    float defaultWalkSpeed = 0.2f;
    int defaultWaterBreatheLevel = 0;
    
    default int getDefaultMaxHP() {
        return defaultMaxHP;
    }

    default float getDefaultWalkSpeed() {
        return defaultWalkSpeed;
    }

    default int getDefaultWaterBreatheLevel() {
        return defaultWaterBreatheLevel;
    }

    int getMaxHp();

    float getWalkSpeed();

    int getWaterBreatheLevel();

    void setMaxHp(int maxHp);

    void setWalkSpeed(float walkSpeed);

    void setWaterBreatheLevel(int waterBreatheLevel);
}
