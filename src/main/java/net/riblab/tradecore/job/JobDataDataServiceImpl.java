package net.riblab.tradecore.job;

import net.kyori.adventure.text.Component;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.Utils;
import net.riblab.tradecore.modifier.IJobExpModifier;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JobDataDataServiceImpl implements JobDataService {

    private final Map<UUID, List<IJobData>> datasMap = TradeCore.getInstance().getConfigManager().getJobDatas().getPlayerJobs();

    @Override
    public IJobData initPlayerJobData(OfflinePlayer offlinePlayer, JobData.JobType type) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobData> datas = datasMap.get(uuid);
        JobData data = new JobData();
        data.jobType = type;
        data.level = 0;
        data.exp = 0;
        datas.add(data);
        return data;
    }

    @Override
    public void addJobExp(OfflinePlayer offlinePlayer, JobData.JobType type, int amount) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobData> datas = datasMap.get(uuid);
        if (datas == null) {
            datasMap.put(uuid, new ArrayList<>());
            datas = datasMap.get(uuid);
        }

        IJobData data = datas.stream().filter(jobData -> jobData.getJobType() == type).findAny().orElse(null);
        if (data == null) {
            data = initPlayerJobData(offlinePlayer, type);
        }

        if (data.getLevel() >= JobData.requiredExp.size()) {
            data.setExp(0);
            if(offlinePlayer instanceof Player player){
                player.sendExperienceChange(0, data.getLevel());
            }
            return;
        }
        
        int newExp;
        if(!(offlinePlayer instanceof Player player)){
            newExp = data.getExp() + amount;
        }
        else{
            int amountSkillApplied = Utils.apply(player, amount, IJobExpModifier.class);
            newExp = data.getExp() + amountSkillApplied;
        }
                
        double expRequiredForNextLevel = JobData.requiredExp.get(data.getLevel());
        if (newExp > expRequiredForNextLevel) {
            int newLevel = data.getLevel() + 1;
            data.setLevel(newLevel);
            data.setExp(0);
            if (offlinePlayer instanceof Player player){
                player.sendMessage(Component.text(type.getName() + "のレベルが" + newLevel + "に上がりました！"));
                player.sendExperienceChange(0, newLevel);
            }
        } else {
            data.setExp(newExp);
            if(offlinePlayer instanceof Player player){
                player.sendExperienceChange((float)( newExp / expRequiredForNextLevel), data.getLevel());
            }
        }
    }

    @Override
    public IJobData getJobData(OfflinePlayer offlinePlayer, JobData.JobType type) {
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobData> datas = datasMap.get(uuid);
        if (datas == null) {
            datasMap.put(uuid, new ArrayList<>());
            datas = datasMap.get(uuid);
        }

        IJobData data = datas.stream().filter(jobData -> jobData.getJobType() == type).findAny().orElse(null);
        if (data == null) {
            data = initPlayerJobData(offlinePlayer, type);
        }

        return data;
    }

    @Override
    public void setJobData(OfflinePlayer offlinePlayer, JobData dataToSet){
        if(dataToSet == null)
            return;
        
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobData> datas = datasMap.get(uuid);
        if (datas == null) {
            datasMap.put(uuid, new ArrayList<>());
            datas = datasMap.get(uuid);
        }

        IJobData data = datas.stream().filter(jobData -> jobData.getJobType() == dataToSet.jobType).findAny().orElse(null);
        if (data == null) {
            data = initPlayerJobData(offlinePlayer, dataToSet.jobType);
        }
        
        data.setLevel(dataToSet.getLevel());
        data.setExp(dataToSet.exp);
    }

    @Override
    public void resetJobData(OfflinePlayer offlinePlayer){
        UUID uuid = offlinePlayer.getUniqueId();
        List<IJobData> datas = datasMap.get(uuid);
        if (datas == null) {
            return;
        }
        
        datas.clear();
    }
}
