package net.riblab.tradecore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import lombok.Getter;
import net.riblab.tradecore.block.BlockUtils;
import net.riblab.tradecore.block.BrokenBlocksService;
import net.riblab.tradecore.command.TCCommands;
import net.riblab.tradecore.config.ConfigService;
import net.riblab.tradecore.craft.VanillaCraftInitializer;
import net.riblab.tradecore.dungeon.DungeonService;
import net.riblab.tradecore.general.AdvancementInitializer;
import net.riblab.tradecore.general.EventReciever;
import net.riblab.tradecore.general.TCTasksInitializer;
import net.riblab.tradecore.general.utils.Utils;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.integration.VaultHook;
import net.riblab.tradecore.item.ItemModService;
import net.riblab.tradecore.item.ItemModServiceImpl;
import net.riblab.tradecore.job.data.JobDataService;
import net.riblab.tradecore.job.data.JobDataServiceImpl;
import net.riblab.tradecore.job.skill.JobSkillService;
import net.riblab.tradecore.job.skill.JobSkillServiceImpl;
import net.riblab.tradecore.mob.CustomMobService;
import net.riblab.tradecore.mob.CustomMobServiceImpl;
import net.riblab.tradecore.mob.FakeVillagerService;
import net.riblab.tradecore.mob.FakeVillagerServiceImpl;
import net.riblab.tradecore.playerstats.PlayerStatsService;
import net.riblab.tradecore.playerstats.PlayerStatsServiceImpl;
import net.riblab.tradecore.ui.UISell;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public final class TradeCore extends JavaPlugin {

    private static TradeCore instance;
    @Getter
    private TCEconomy economy;
    private VaultHook vaultHook;
    @Getter
    private ConfigService configService;
    @Getter
    private ProtocolManager protocolManager;
    @Getter
    private EventReciever eventReciever;
    @Getter
    private JobDataService jobService;
    @Getter
    private JobSkillService jobSkillService;
    @Getter
    private ItemModService itemModService;
    @Getter
    private PlayerStatsService playerStatsService;
    @Getter
    private UltimateAdvancementAPI advancementAPI;
    @Getter
    private AdvancementInitializer advancementInitializer;
    @Getter
    private DungeonService dungeonService;
    @Getter
    private BrokenBlocksService brokenBlocksService;
    @Getter
    private CustomMobService customMobService;
    @Getter
    private FakeVillagerService fakeVillagerService;

    public TradeCore() {
        instance = this;
    }

    public static TradeCore getInstance() {
        return instance;
    }

    @Getter
    private static boolean isWGLoaded;

    static {
        Utils.initializeEnumSafely();
    }

    @Override
    public void onLoad() {
        TCCommands.onLoad();
    }

    @Override
    public void onEnable() {
        configService = ConfigService.getImpl();
        configService.load();
        eventReciever = new EventReciever();
        jobService = new JobDataServiceImpl();
        jobSkillService = new JobSkillServiceImpl();
        jobSkillService.onDeserialize();
        itemModService = new ItemModServiceImpl();
        playerStatsService = new PlayerStatsServiceImpl();
        VanillaCraftInitializer.init();
        dungeonService = DungeonService.getImpl();
        brokenBlocksService = BrokenBlocksService.getImpl();
        customMobService = new CustomMobServiceImpl();
        fakeVillagerService = new FakeVillagerServiceImpl();

        economy = TCEconomy.getImpl();
        vaultHook = VaultHook.getImpl();
        vaultHook.hook();
        isWGLoaded = getServer().getPluginManager().isPluginEnabled("WorldGuard");
        advancementAPI = UltimateAdvancementAPI.getInstance(this);

        TCCommands.onEnable();

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!economy.hasAccount(player))
                economy.createPlayerAccount(player);

            BlockUtils.addSlowDig(player);
            itemModService.updateEquipment(player);
            itemModService.updateMainHand(player, player.getInventory().getHeldItemSlot());
        });

        protocolManager = ProtocolLibrary.getProtocolManager();

        new TCTasksInitializer();
        advancementInitializer = new AdvancementInitializer();

        //買い取り商人
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        Player player = event.getPlayer();
                        PacketContainer packet = event.getPacket();
                        int id = packet.getIntegers().read(0);
                        Integer integer = TradeCore.getInstance().getFakeVillagerService().getCurrentID(player);
                        if (integer != null && id == integer) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    UISell.open(event.getPlayer());
                                }
                            }.runTaskLater(TradeCore.getInstance(), 0);
                        }
                    }
                }
        );
    }

    @Override
    public void onDisable() {
        vaultHook.unhook();
        TCCommands.onDisable();
        configService.save();

        customMobService.deSpawnAll();

        Bukkit.getOnlinePlayers().forEach(BlockUtils::removeSlowDig);

        dungeonService.destroyAll();
    }
}
