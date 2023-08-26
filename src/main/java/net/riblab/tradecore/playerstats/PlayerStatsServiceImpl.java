package net.riblab.tradecore.playerstats;

import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.general.utils.Utils;
import net.riblab.tradecore.item.ItemModService;
import net.riblab.tradecore.job.skill.JobSkillService;
import net.riblab.tradecore.modifier.IHPModifier;
import net.riblab.tradecore.modifier.IWalkSpeedModifier;
import net.riblab.tradecore.modifier.IWaterBreatheLevelModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * プレイヤーステータスの管理クラス
 */
public class PlayerStatsServiceImpl implements PlayerStatsService {

    /**
     * JobSkilやItemModで修飾済みのプレイヤーステータスが入ったmap
     */
    private static final Map<Player, IPlayerStats> playerStatsMap = new HashMap<>();
    
    private static JobSkillService getJSHandler(){
        return TradeCore.getInstance().getJobSkillService();
    }
    
    private static ItemModService getEquipmentHandler(){
        return TradeCore.getInstance().getItemModService();
    }

    public PlayerStatsServiceImpl(){
        getJSHandler().getOnJobSkillChanged().add(this::update);
        getEquipmentHandler().getOnItemModUpdated().add(this::update);
    }
    
    @Override
    public void update(Player player){
        IPlayerStats iPlayerStats = playerStatsMap.get(player);
        if(iPlayerStats == null){
            iPlayerStats = new PlayerStats();
            playerStatsMap.put(player, iPlayerStats);
        }
        
        iPlayerStats.setMaxHp(Utils.apply(player, iPlayerStats.getDefaultMaxHP(), IHPModifier.class));
        iPlayerStats.setWalkSpeed(Utils.apply(player, iPlayerStats.getDefaultWalkSpeed(), IWalkSpeedModifier.class));
        iPlayerStats.setWaterBreatheLevel(Utils.apply(player, iPlayerStats.getDefaultWaterBreatheLevel(), IWaterBreatheLevelModifier.class));
        
        apply(player);
    }
    
    @Override
    public IPlayerStats get(Player player){
        return playerStatsMap.get(player);
    }

    @Override
    public void apply(Player player){
        IPlayerStats IPlayerStats = playerStatsMap.get(player);
        if(IPlayerStats == null){
            return;
        }
        
        player.setMaxHealth(IPlayerStats.getMaxHp());
        player.setWalkSpeed(IPlayerStats.getWalkSpeed());
        
        if(IPlayerStats.getWaterBreatheLevel() > 0){
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, -1, IPlayerStats.getWaterBreatheLevel() - 1, false, false), true);
        }
        else{
            player.removePotionEffect(PotionEffectType.WATER_BREATHING);
        }
    }

    @Override
    public void remove(Player player){
        playerStatsMap.remove(player);
    }
}
