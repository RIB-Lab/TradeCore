package net.riblab.tradecore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import lombok.Getter;
import net.riblab.tradecore.block.BrokenBlocksServiceImpl;
import net.riblab.tradecore.block.BrokenBlocksService;
import net.riblab.tradecore.config.ConfigService;
import net.riblab.tradecore.config.ConfigServiceImpl;
import net.riblab.tradecore.craft.TCCraftingRecipes;
import net.riblab.tradecore.craft.TCFurnaceRecipes;
import net.riblab.tradecore.craft.VanillaCraftInitializer;
import net.riblab.tradecore.dungeon.DungeonServiceImpl;
import net.riblab.tradecore.dungeon.DungeonService;
import net.riblab.tradecore.general.AdvancementInitializer;
import net.riblab.tradecore.general.EventReciever;
import net.riblab.tradecore.general.TCCommands;
import net.riblab.tradecore.general.TCTasksInitializer;
import net.riblab.tradecore.integration.EconomyImpl;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.integration.VaultHook;
import net.riblab.tradecore.integration.VaultHookImpl;
import net.riblab.tradecore.item.*;
import net.riblab.tradecore.item.base.ITCTool;
import net.riblab.tradecore.job.*;
import net.riblab.tradecore.mob.*;
import net.riblab.tradecore.playerstats.PlayerStatsService;
import net.riblab.tradecore.playerstats.PlayerStatsServiceImpl;
import net.riblab.tradecore.ui.UISell;
import net.riblab.tradecore.utils.Utils;
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
    private TCTasksInitializer tcTasksInitializer;

    public TradeCore() {
        instance = this;
    }

    public static TradeCore getInstance() {
        return instance;
    }

    @Getter
    private static boolean isWGLoaded;

    static {
        initializeEnumSafely();
    }

    /**
     * ロード順によって競合の可能性のあるenumを安全に初期化する
     */
    private static void initializeEnumSafely(){
        TCItems.values();
        TCMobs.values();
        LootTables.values();
        TCCraftingRecipes.values();
        TCFurnaceRecipes.values();

        JobData.JobType.values();
        ITCTool.ToolType.values();
    }

    @Override
    public void onLoad() {
        TCCommands.onLoad();
    }

    @Override
    public void onEnable() {
        configService = new ConfigServiceImpl();
        configService.load();
        eventReciever = new EventReciever();
        jobService = new JobDataDataServiceImpl();
        jobSkillService = new JobSkillServiceImpl();
        jobSkillService.onDeserialize();
        itemModService = new ItemModServiceImpl();
        playerStatsService = new PlayerStatsServiceImpl();
        new VanillaCraftInitializer();
        dungeonService = new DungeonServiceImpl();
        brokenBlocksService = new BrokenBlocksServiceImpl();
        customMobService = new CustomMobServiceImpl();
        fakeVillagerService = new FakeVillagerServiceImpl();

        economy = new EconomyImpl();
        vaultHook = new VaultHookImpl();
        vaultHook.hook();
        isWGLoaded = getServer().getPluginManager().isPluginEnabled("WorldGuard");
        advancementAPI = UltimateAdvancementAPI.getInstance(this);

        TCCommands.onEnable();

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!economy.hasAccount(player))
                economy.createPlayerAccount(player);

            Utils.addSlowDig(player);
            itemModService.updateEquipment(player);
            itemModService.updateMainHand(player, player.getInventory().getHeldItemSlot());
        });

        protocolManager = ProtocolLibrary.getProtocolManager();

        tcTasksInitializer = new TCTasksInitializer();
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

        Bukkit.getOnlinePlayers().forEach(player -> Utils.removeSlowDig(player));
        
        dungeonService.destroyAll();
    }
}
