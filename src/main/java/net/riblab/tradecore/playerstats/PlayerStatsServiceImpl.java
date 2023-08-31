package net.riblab.tradecore.playerstats;

import net.riblab.tradecore.general.Utils;
import net.riblab.tradecore.item.PlayerItemModService;
import net.riblab.tradecore.job.skill.JobSkillService;
import net.riblab.tradecore.modifier.IHPModifier;
import net.riblab.tradecore.modifier.IWalkSpeedModifier;
import net.riblab.tradecore.modifier.IWaterBreatheLevelModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

/**
 * プレイヤーステータスの管理クラス
 */
enum PlayerStatsServiceImpl implements PlayerStatsService {
    INSTANCE;

    boolean isInit = false;

    /**
     * JobSkilやItemModで修飾済みのプレイヤーステータスが入ったmap
     */
    private final Map<Player, IPlayerStats> playerStatsMap = new HashMap<>();


    public void init() {
        if (isInit)
            return;

        JobSkillService.getImpl().getOnJobSkillChanged().add(this::update);
        PlayerItemModService.getImpl().getOnItemModUpdated().add(this::update);

        isInit = true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void update(Player player) {
        IPlayerStats iPlayerStats = playerStatsMap.get(player);
        if (iPlayerStats == null) {
            iPlayerStats = new PlayerStats();
            playerStatsMap.put(player, iPlayerStats);
        }

        iPlayerStats.setMaxHp(Utils.apply(player, iPlayerStats.getDefaultMaxHP(), IHPModifier.class));
        iPlayerStats.setWalkSpeed(Utils.apply(player, iPlayerStats.getDefaultWalkSpeed(), IWalkSpeedModifier.class));
        iPlayerStats.setWaterBreatheLevel(Utils.apply(player, iPlayerStats.getDefaultWaterBreatheLevel(), IWaterBreatheLevelModifier.class));

        apply(player);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void apply(Player player) {
        IPlayerStats IPlayerStats = playerStatsMap.get(player);
        if (IPlayerStats == null) {
            return;
        }

        player.setMaxHealth(IPlayerStats.getMaxHp());
        player.setWalkSpeed(IPlayerStats.getWalkSpeed());

        if (IPlayerStats.getWaterBreatheLevel() > 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, -1, IPlayerStats.getWaterBreatheLevel() - 1, false, false), true);
        } else {
            player.removePotionEffect(PotionEffectType.WATER_BREATHING);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void remove(Player player) {
        playerStatsMap.remove(player);
    }
}
