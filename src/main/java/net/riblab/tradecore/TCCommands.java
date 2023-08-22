package net.riblab.tradecore;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import net.riblab.tradecore.item.ITCItem;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.job.*;
import net.riblab.tradecore.mob.CustomMobService;
import net.riblab.tradecore.mob.TCMob;
import net.riblab.tradecore.mob.TCMobs;
import net.riblab.tradecore.ui.UIAdminShop;
import net.riblab.tradecore.ui.UIJobs;
import net.riblab.tradecore.ui.UIShop;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static net.riblab.tradecore.Materials.transparentBlocks;

/**
 * コマンド登録クラス
 */
public class TCCommands {
    
    public static final String merchantName = "買い取り商";

    private static JobHandler getJobHandler(){
        return TradeCore.getInstance().getJobHandler();
    }
    
    private static Economy getEconomy(){
        return TradeCore.getInstance().getEconomy();
    }
    
    public static void onLoad(){
        CommandAPI.onLoad(new CommandAPIBukkitConfig(TradeCore.getInstance()).verboseOutput(true)); // Load with verbose output

        CommandAPICommand setMoneyCommand = new CommandAPICommand("setmoney")
                .withArguments(new PlayerArgument("player"))
                .withArguments(new DoubleArgument("money", 0, Integer.MAX_VALUE))
                .executesPlayer((player, args) -> {
                    Player player1 = (Player) args.get(0);
                    Double money = (Double) args.get(1);

                    getEconomy().withdrawPlayer(player1, getEconomy().getBalance(player1));
                    getEconomy().depositPlayer(player1, money);
                });
        setMoneyCommand.setPermission(CommandPermission.OP);
        setMoneyCommand.register();

        CommandAPICommand tcGiveCommand = new CommandAPICommand("tcgive")
                .withArguments(TCItems.customITCItemArgument("item"))
                .withArguments(new IntegerArgument("amount", 1, 1000))
                .executesPlayer((player, args) -> {
                    ITCItem itcItem = (ITCItem) args.get(0);
                    int amount = (int) args.get(1);
                    ItemStack newStack = itcItem.getItemStack();
                    newStack.setAmount(amount);
                    player.getInventory().addItem(newStack);
                });
        tcGiveCommand.setPermission(CommandPermission.OP);
        tcGiveCommand.register();

        CommandAPICommand sellCommand = new CommandAPICommand("tcsell")
                .executesPlayer((player, args) -> {
                    Location spawnLocation = player.getTargetBlock(transparentBlocks, 2).getLocation().add(new Vector(0.5d, 0d, 0.5d));
                    spawnLocation.setY(player.getLocation().getY());

                    FakeVillagerService.spawnFakeVillager(player, merchantName, spawnLocation);
                    player.getWorld().spawnParticle(Particle.SMOKE_LARGE, spawnLocation, 10, 1, 1, 1);
                });
        sellCommand.setPermission(CommandPermission.NONE);
        sellCommand.register();

        CommandAPICommand spawnCommand = new CommandAPICommand("tcspawn")
                .withArguments(TCMobs.customITCMobArgument("mobname"))
                .executesPlayer((player, args) -> {
                    TCMob type = (TCMob) args.get(0);
                    Location spawnLocation = player.getTargetBlock(transparentBlocks, 2).getLocation().add(new Vector(0.5d, 0d, 0.5d));
                    spawnLocation.setY(player.getLocation().getY());

                    CustomMobService.spawn(player, spawnLocation, type);
                });
        spawnCommand.setPermission(CommandPermission.OP);
        spawnCommand.register();

        CommandAPICommand shopCommand = new CommandAPICommand("tcshop")
                .withPermission(CommandPermission.OP)
                .withArguments(Shops.customShopDataArgument("ショップの種類"))
                .executesPlayer((player, args) -> {
                    ShopData data = (ShopData) args.get(0);
                    UIShop.open(player, data);
                });
        shopCommand.register();

        CommandAPICommand adminShopCommand = new CommandAPICommand("tcadminshop")
                .withPermission(CommandPermission.NONE)
                .executesPlayer((player, args) -> {
                    UIAdminShop.open(player);
                });
        adminShopCommand.register();

        CommandAPICommand jobCommand = new CommandAPICommand("tcjob")
                .withPermission(CommandPermission.NONE)
                .executesPlayer((player, args) -> {
                    UIJobs.open(player);
                });
        CommandAPICommand jobSetCommand = new CommandAPICommand("setjoblv")
                .withPermission(CommandPermission.OP)
                .withArguments(new PlayerArgument("プレイヤー"))
                .withArguments(JobData.JobType.customJobTypeArgument("職業の種類"))
                .withArguments(new IntegerArgument("レベル"))
                .executesPlayer((player, args) -> {
                    Player targetPlayer = (Player) args.get(0);
                    JobData.JobType jobType = (JobData.JobType) args.get(1);
                    int level = (int) args.get(2);
                    JobData newData = new JobData();
                    newData.setJobType(jobType);
                    newData.setLevel(level);
                    newData.setExp(0);
                    getJobHandler().setJobData(targetPlayer, newData);
                });
        CommandAPICommand jobResetCommand = new CommandAPICommand("resetjoblv")
                .withPermission(CommandPermission.OP)
                .withArguments(new PlayerArgument("プレイヤー"))
                .executesPlayer((player, args) -> {
                    Player targetPlayer = (Player) args.get(0);
                    getJobHandler().resetJobData(targetPlayer);
                });
        CommandAPICommand skillResetCommand = new CommandAPICommand("resetskilllv")
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    TradeCore.getInstance().getJobSkillHandler().resetPlayerJobSkillData(player);
                });
        jobCommand.withSubcommand(jobSetCommand);
        jobCommand.withSubcommand(jobResetCommand);
        jobCommand.withSubcommand(skillResetCommand);
        jobCommand.register();
    }
    
    public static void onEnable(){
        CommandAPI.onEnable();
    }
    
    public static void onDisable(){
        CommandAPI.onDisable();
    }
}
