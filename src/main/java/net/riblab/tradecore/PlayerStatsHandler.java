package net.riblab.tradecore;

import net.riblab.tradecore.item.ItemModService;
import net.riblab.tradecore.item.ItemModServiceImpl;
import net.riblab.tradecore.job.JobSkillHandler;
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
public class PlayerStatsHandler {

    /**
     * JobSkilやItemModで修飾済みのプレイヤーステータスが入ったmap
     */
    private static final Map<Player, PlayerStats> playerStatsMap = new HashMap<>();
    
    private static JobSkillHandler getJSHandler(){
        return TradeCore.getInstance().getJobSkillHandler();
    }
    
    private static ItemModService getEquipmentHandler(){
        return TradeCore.getInstance().getItemModService();
    }

    public PlayerStatsHandler(){
        getJSHandler().onJobSkillChanged.add(this::update);
        getEquipmentHandler().getOnItemModUpdated().add(this::update);
    }
    
    /**
     * 装備やジョブスキルが更新された時、それらでプレイヤーステータスを修飾し、それをプレイヤーに反映する
     */
    public void update(Player player){
        PlayerStats playerStats = playerStatsMap.get(player);
        if(playerStats == null){
            playerStats = new PlayerStats();
            playerStatsMap.put(player, playerStats);
        }
        
        playerStats.setMaxHp(Utils.apply(player, PlayerStats.getDefaultMaxHP(), IHPModifier.class));
        playerStats.setWalkSpeed(Utils.apply(player, PlayerStats.getDefaultWalkSpeed(), IWalkSpeedModifier.class));
        playerStats.setWaterBreatheLevel(Utils.apply(player, PlayerStats.getDefaultWaterBreatheLevel(), IWaterBreatheLevelModifier.class));
        
        apply(player);
    }
    
    public PlayerStats get(Player player){
        return playerStatsMap.get(player);
    }

    /**
     * プレイヤーステータスをプレイヤーに反映する
     */
    public void apply(Player player){
        PlayerStats playerStats = playerStatsMap.get(player);
        if(playerStats == null){
            return;
        }
        
        player.setMaxHealth(playerStats.getMaxHp());
        player.setWalkSpeed(playerStats.getWalkSpeed());
        
        if(playerStats.getWaterBreatheLevel() > 0){
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, -1, playerStats.getWaterBreatheLevel() - 1, false, false), true);
        }
        else{
            player.removePotionEffect(PotionEffectType.WATER_BREATHING);
        }
    }

    /**
     * あるプレイヤーのステータスをリストから削除する
     */
    public void remove(Player player){
        playerStatsMap.remove(player);
    }
}
