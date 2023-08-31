package net.riblab.tradecore.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.riblab.tradecore.dungeon.DungeonDatas;
import net.riblab.tradecore.dungeon.DungeonService;
import net.riblab.tradecore.dungeon.IDungeonData;
import net.riblab.tradecore.general.Utils;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.item.Materials;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.job.data.JobData;
import net.riblab.tradecore.job.data.JobDataService;
import net.riblab.tradecore.job.data.JobType;
import net.riblab.tradecore.job.skill.JobSkillService;
import net.riblab.tradecore.entity.mob.CustomMobService;
import net.riblab.tradecore.entity.mob.FakeVillagerService;
import net.riblab.tradecore.entity.mob.ITCMob;
import net.riblab.tradecore.entity.mob.TCMobs;
import net.riblab.tradecore.shop.IShopData;
import net.riblab.tradecore.shop.Shops;
import net.riblab.tradecore.ui.*;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static net.riblab.tradecore.command.CommandArgDescs.*;
import static net.riblab.tradecore.command.CommandNames.*;

/**
 * コマンド登録クラス。プラグインのonEnableの前に呼ぶ必要があるのでインスタンスを生成させない。
 */
public final class TCCommands {

    private TCCommands() {
        throw new AssertionError();
    }

    public static final String merchantName = "買い取り商";

    private static TCEconomy getEconomy() {
        return TCEconomy.getImpl();
    }
    
    public static void onEnable() {
        registerCommands();
    }
    
    /**
     * このプラグインで使う全てのコマンドを登録する
     */
    private static void registerCommands() {
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
                    Location spawnLocation = player.getTargetBlock(Materials.TRANSPARENT.get(), 2).getLocation().add(new Vector(0.5d, 0d, 0.5d));
                    spawnLocation.setY(player.getLocation().getY());

                    FakeVillagerService.getImpl().spawnFakeVillager(player, merchantName, spawnLocation);
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
                    Location spawnLocation = player.getTargetBlock(Materials.TRANSPARENT.get(), 2).getLocation().add(new Vector(0.5d, 0d, 0.5d));
                    spawnLocation.setY(player.getLocation().getY());

                    CustomMobService.getImpl().spawn(player, spawnLocation, type);
                });
        CommandAPICommand mobResetCommand = new CommandAPICommand(MOBS_RESET.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    CustomMobService.getImpl().deSpawnAll();
                    player.sendMessage("モブシステムをリセットしました");
                });
        mobCommand.withSubcommand(spawnCommand);
        mobCommand.withSubcommand(mobResetCommand);
        mobCommand.register();

        CommandAPICommand shopCommand = new CommandAPICommand(SHOP.get())
                .withPermission(CommandPermission.OP)
                .withArguments(Shops.customShopDataArgument(SHOPDATA.get()))
                .executesPlayer((player, args) -> {
                    IShopData data = (IShopData) args.get(0);
                    UIShop.open(player, data);
                });
        CommandAPICommand adminShopCommand = new CommandAPICommand(SHOP_ADMIN.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    UIs.ADMINSHOP.get().open(player);
                });
        CommandAPICommand respecShopCommand = new CommandAPICommand(SHOP_RESPEC.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    UIs.SKILLRESPEC.get().open(player);
                });
        CommandAPICommand dungeonShopCommand = new CommandAPICommand(SHOP_DUNGEON.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    UIs.DUNGEONENTER.get().open(player);
                });
        shopCommand.withSubcommand(adminShopCommand);
        shopCommand.withSubcommand(respecShopCommand);
        shopCommand.withSubcommand(dungeonShopCommand);
        shopCommand.register();

        CommandAPICommand jobCommand = new CommandAPICommand(JOB.get())
                .withPermission(CommandPermission.NONE)
                .executesPlayer((player, args) -> {
                    UIs.JOBS.get().open(player);
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
                    JobDataService.getImpl().setJobData(targetPlayer, newData);
                });
        CommandAPICommand jobResetCommand = new CommandAPICommand(JOB_RESETJOBLV.get())
                .withPermission(CommandPermission.OP)
                .withArguments(new PlayerArgument(PLAYER.get()))
                .executesPlayer((player, args) -> {
                    Player targetPlayer = (Player) args.get(0);
                    JobDataService.getImpl().resetJobData(targetPlayer);
                });
        CommandAPICommand skillResetCommand = new CommandAPICommand(JOB_RESETSKILLLV.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    JobSkillService.getImpl().resetPlayerJobSkillData(player);
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
                    IDungeonData<?> data = (IDungeonData<?>) args.get(0);
                    int id = (int) args.get(1);
                    DungeonService IDungeonService = DungeonService.getImpl();
                    if (!IDungeonService.isDungeonExist(data, id)) {
                        World instance = IDungeonService.create(data, id);
                        IDungeonService.enter(player, instance);
                    } else {
                        IDungeonService.enter(player, data, id);
                    }
                });
        CommandAPICommand leaveDungeonCommand = new CommandAPICommand(DUNGEON_LEAVE.get())
                .withPermission(CommandPermission.NONE)
                .executesPlayer((player, args) -> {
                    DungeonService IDungeonService = DungeonService.getImpl();
                    IDungeonService.tryLeave(player);
                });
        CommandAPICommand evacuateDungeonCommand = new CommandAPICommand(DUNGEON_EVACUATE.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    DungeonService IDungeonService = DungeonService.getImpl();
                    IDungeonService.evacuate(player);
                });
        CommandAPICommand dungeonListCommand = new CommandAPICommand(DUNGEON_LIST.get())
                .withPermission(CommandPermission.OP)
                .executesPlayer((player, args) -> {
                    player.sendMessage("～ダンジョンリスト～");
                    DungeonService IDungeonService = DungeonService.getImpl();
                    IDungeonService.getDungeonListInfo().forEach(player::sendMessage);
                });
        dungeonCommand.withSubcommand(enterDungeonCommand);
        dungeonCommand.withSubcommand(leaveDungeonCommand);
        dungeonCommand.withSubcommand(evacuateDungeonCommand);
        dungeonCommand.withSubcommand(dungeonListCommand);
        dungeonCommand.register();

        CommandAPICommand wikiCommand = new CommandAPICommand(WIKI.get())
                .withPermission(CommandPermission.NONE)
                .executesPlayer((player, args) -> {
                    player.sendMessage("wiki url: https://www.riblab.net/trade/");
                });
        wikiCommand.register();

        CommandAPICommand versionCommand = new CommandAPICommand(VERSION.get())
                .withPermission(CommandPermission.NONE)
                .executesPlayer((player, args) -> {
                    player.sendMessage(Utils.getVersion());
                });
        versionCommand.register();
    }
}
