package net.riblab.tradecore;

import net.riblab.tradecore.item.EquipmentHandler;
import net.riblab.tradecore.job.JobSkillHandler;
import net.riblab.tradecore.modifier.IArmorModifier;
import net.riblab.tradecore.modifier.IHPModifier;
import net.riblab.tradecore.modifier.IWalkSpeedModifier;
import org.bukkit.entity.Player;

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
    
    private static EquipmentHandler getEquipmentHandler(){
        return TradeCore.getInstance().getEquipmentHandler();
    }

    public PlayerStatsHandler(){
        getJSHandler().onJobSkillChanged.add(this::update);
        getEquipmentHandler().onEquipmentModUpdated.add(this::update);
    }
    
    /**
     * 装備やジョブスキルでプレイヤーステータスを修飾し、それをプレイヤーに反映する
     */
    public void update(Player player){
        PlayerStats playerStats = playerStatsMap.get(player);
        if(playerStats == null){
            playerStats = new PlayerStats();
            playerStatsMap.put(player, playerStats);
        }
        
        double armor = getEquipmentHandler().apply(player, PlayerStats.getDefaultArmor(), IArmorModifier.class);
        armor = getJSHandler().apply(player, armor, IArmorModifier.class);
        playerStats.setArmor(armor);
        int hp = getEquipmentHandler().apply(player, PlayerStats.getDefaultMaxHP(), IHPModifier.class);
        hp = getJSHandler().apply(player, hp, IHPModifier.class);
        playerStats.setMaxHp(hp);
        float walkSpeed = getEquipmentHandler().apply(player, PlayerStats.getDefaultWalkSpeed(), IWalkSpeedModifier.class);
        walkSpeed = getJSHandler().apply(player, walkSpeed, IWalkSpeedModifier.class);
        playerStats.setWalkSpeed(walkSpeed);
        
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
        player.setWalkSpeed(playerStats.getWalkSpeed());
    }

    /**
     * あるプレイヤーのステータスをリストから削除する
     */
    public void remove(Player player){
        playerStatsMap.remove(player);
    }
}
