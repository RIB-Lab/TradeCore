package net.riblab.tradecore.general;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.dungeon.IDungeonData;
import net.riblab.tradecore.dungeon.DungeonDatas;
import net.riblab.tradecore.dungeon.DungeonService;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.TCItems;
import net.riblab.tradecore.job.data.JobData;
import net.riblab.tradecore.job.data.JobDataService;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.mob.ITCMob;
import net.riblab.tradecore.mob.TCMobs;
import net.riblab.tradecore.shop.ShopData;
import net.riblab.tradecore.shop.Shops;
import net.riblab.tradecore.ui.UIAdminShop;
import net.riblab.tradecore.ui.UIJobs;
import net.riblab.tradecore.ui.UIShop;
import net.riblab.tradecore.ui.UISkillRespec;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static net.riblab.tradecore.general.CommandArgDescs.*;
import static net.riblab.tradecore.general.CommandNames.*;
import static net.riblab.tradecore.general.utils.Materials.transparentBlocks;

/**
 * コマンド登録クラス。プラグインのonEnableの前に呼ぶ必要があるのでインスタンスを生成させない。
 */
public class TCCommands {
    
    private TCCommands(){
        throw new AssertionError();
    }
    
    public static final String merchantName = "買い取り商";

    private static JobDataService getJobHandler(){
        return TradeCore.getInstance().getJobService();
    }
    
    private static TCEconomy getEconomy(){
        return TradeCore.getInstance().getEconomy();
    }
    
    public static void onLoad(){
        registerCommands();
    }

    /**
     * このプラグインで使う全てのコマンドを登録する
     */
    private static void registerCommands(){
        CommandAPI.onLoad(new CommandAPIBukkitConfig(TradeCore.getInstance()).verboseOutput(true)); // Load with verbose output

        CommandAPICommand currencyCommand = new CommandAPICommand(CURRENCY.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                });
        CommandAPICommand setMoneyCommand = new CommandAPICommand(CURRENCY_SETMONEY.get())
                .withPermission(CommandPermission.OP)
                .withArguments(new PlayerArgument(PLAYER.get()))
                .withArguments(new DoubleArgument(MONEY.get(), 0, Integer.MAX_VALUE))
                .executesPlayer((player, args) -> {
                    Player player1 = (Player) args.get(0);
                    Double money = (Double) args.get(1);

                    getEconomy().withdrawPlayer(player1, getEconomy().getBalance(player1));
                    getEconomy().depositPlayer(player1, money);
                });
        CommandAPICommand setPlayTicketCommand = new CommandAPICommand(CURRENCY_SETTICKET.get())
                .withPermission(CommandPermission.OP)
                .withArguments(new PlayerArgument(PLAYER.get()))
                .withArguments(new IntegerArgument(TICKET.get(), 0, Integer.MAX_VALUE))
                .executesPlayer((player, args) -> {
                    Player player1 = (Player) args.get(0);
                    int ticket = (int) args.get(1);

                    getEconomy().withdrawPlayer(player1, getEconomy().getPlayTickets(player1));
                    getEconomy().depositPlayer(player1, ticket);
                });
        currencyCommand.withSubcommand(setMoneyCommand);
        currencyCommand.withSubcommand(setPlayTicketCommand);
        currencyCommand.register();

        CommandAPICommand tcGiveCommand = new CommandAPICommand(GIVE.get())
                .withArguments(TCItems.customITCItemArgument(TCITEM.get()))
                .withArguments(new IntegerArgument(AMOUNT.get(), 1, 1000))
                .executesPlayer((player, args) -> {
                    ITCItem itcItem = (ITCItem) args.get(0);
                    int amount = (int) args.get(1);
                    ItemStack newStack = itcItem.getItemStack();
                    newStack.setAmount(amount);
                    player.getInventory().addItem(newStack);
                });
        tcGiveCommand.setPermission(CommandPermission.OP);
        tcGiveCommand.register();

        CommandAPICommand sellCommand = new CommandAPICommand(SELL.get())
                .executesPlayer((player, args) -> {
                    Location spawnLocation = player.getTargetBlock(transparentBlocks, 2).getLocation().add(new Vector(0.5d, 0d, 0.5d));
                    spawnLocation.setY(player.getLocation().getY());

                    TradeCore.getInstance().getFakeVillagerService().spawnFakeVillager(player, merchantName, spawnLocation);
                    player.getWorld().spawnParticle(Particle.SMOKE_LARGE, spawnLocation, 10, 1, 1, 1);
                });
        sellCommand.setPermission(CommandPermission.NONE);
        sellCommand.register();

        CommandAPICommand mobCommand = new CommandAPICommand(MOBS.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                });

        CommandAPICommand spawnCommand = new CommandAPICommand(MOBS_SPAWN.get())
                .withPermission(CommandPermission.OP)
                .withArguments(TCMobs.customITCMobArgument(MOBNAME.get()))
                .executesPlayer((player, args) -> {
                    ITCMob type = (ITCMob) args.get(0);
                    Location spawnLocation = player.getTargetBlock(transparentBlocks, 2).getLocation().add(new Vector(0.5d, 0d, 0.5d));
                    spawnLocation.setY(player.getLocation().getY());

                    TradeCore.getInstance().getCustomMobService().spawn(player, spawnLocation, type);
                });
        CommandAPICommand mobResetCommand = new CommandAPICommand(MOBS_RESET.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    TradeCore.getInstance().getCustomMobService().deSpawnAll();
                    player.sendMessage("モブシステムをリセットしました");
                });
        mobCommand.withSubcommand(spawnCommand);
        mobCommand.withSubcommand(mobResetCommand);
        mobCommand.register();

        CommandAPICommand shopCommand = new CommandAPICommand(SHOP.get())
                .withPermission(CommandPermission.OP)
                .withArguments(Shops.customShopDataArgument(SHOPDATA.get()))
                .executesPlayer((player, args) -> {
                    ShopData data = (ShopData) args.get(0);
                    UIShop.open(player, data);
                });
        CommandAPICommand adminShopCommand = new CommandAPICommand(SHOP_ADMIN.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    UIAdminShop.open(player);
                });
        shopCommand.withSubcommand(adminShopCommand);
        CommandAPICommand respecShopCommand = new CommandAPICommand(SHOP_RESPEC.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    UISkillRespec.open(player);
                });
        shopCommand.withSubcommand(adminShopCommand);
        shopCommand.withSubcommand(respecShopCommand);
        shopCommand.register();

        CommandAPICommand jobCommand = new CommandAPICommand(JOB.get())
                .withPermission(CommandPermission.NONE)
                .executesPlayer((player, args) -> {
                    UIJobs.open(player);
                });
        CommandAPICommand jobSetCommand = new CommandAPICommand(JOB_SETJOBLV.get())
                .withPermission(CommandPermission.OP)
                .withArguments(new PlayerArgument(PLAYER.get()))
                .withArguments(JobType.customJobTypeArgument(JOBTYPE.get()))
                .withArguments(new IntegerArgument(LEVEL.get()))
                .executesPlayer((player, args) -> {
                    Player targetPlayer = (Player) args.get(0);
                    JobType jobType = (JobType) args.get(1);
                    int level = (int) args.get(2);
                    JobData newData = new JobData();
                    newData.setJobType(jobType);
                    newData.setLevel(level);
                    newData.setExp(0);
                    getJobHandler().setJobData(targetPlayer, newData);
                });
        CommandAPICommand jobResetCommand = new CommandAPICommand(JOB_RESETJOBLV.get())
                .withPermission(CommandPermission.OP)
                .withArguments(new PlayerArgument(PLAYER.get()))
                .executesPlayer((player, args) -> {
                    Player targetPlayer = (Player) args.get(0);
                    getJobHandler().resetJobData(targetPlayer);
                });
        CommandAPICommand skillResetCommand = new CommandAPICommand(JOB_RESETSKILLLV.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    TradeCore.getInstance().getJobSkillService().resetPlayerJobSkillData(player);
                });
        jobCommand.withSubcommand(jobSetCommand);
        jobCommand.withSubcommand(jobResetCommand);
        jobCommand.withSubcommand(skillResetCommand);
        jobCommand.register();

        CommandAPICommand dungeonCommand = new CommandAPICommand(DUNGEON.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                });
        CommandAPICommand enterDungeonCommand = new CommandAPICommand(DUNGEON_ENTER.get())
                .withPermission(CommandPermission.OP)
                .withArguments(DungeonDatas.customDungeonDataArgument(DUNGEONDATA.get()))
                .withArguments(new IntegerArgument(INSTANCEID.get()))
                .executesPlayer((player, args) -> {
                    IDungeonData data = (IDungeonData) args.get(0);
                    int id = (int) args.get(1);
                    DungeonService IDungeonService = TradeCore.getInstance().getDungeonService();
                    if(!IDungeonService.isDungeonExist(data, id)){
                        IDungeonService.create(data, id);
                        IDungeonService.enter(player, data, id);
                    }
                    else {
                        IDungeonService.enter(player, data, id);
                    }
                });
        CommandAPICommand leaveDungeonCommand = new CommandAPICommand(DUNGEON_LEAVE.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    DungeonService IDungeonService = TradeCore.getInstance().getDungeonService();
                    IDungeonService.tryLeave(player);
                });
        CommandAPICommand evacuateDungeonCommand = new CommandAPICommand(DUNGEON_EVACUATE.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    DungeonService IDungeonService = TradeCore.getInstance().getDungeonService();
                    IDungeonService.evacuate(player);
                });
        CommandAPICommand dungeonListCommand = new CommandAPICommand(DUNGEON_LIST.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    player.sendMessage("～ダンジョンリスト～");
                    DungeonService IDungeonService = TradeCore.getInstance().getDungeonService();
                    IDungeonService.getDungeonListInfo().forEach(s -> player.sendMessage(s));
                });
        dungeonCommand.withSubcommand(enterDungeonCommand);
        dungeonCommand.withSubcommand(leaveDungeonCommand);
        dungeonCommand.withSubcommand(evacuateDungeonCommand);
        dungeonCommand.withSubcommand(dungeonListCommand);
        dungeonCommand.register();
    }
    
    public static void onEnable(){
        CommandAPI.onEnable();
    }
    
    public static void onDisable(){
        CommandAPI.onDisable();
    }
}
