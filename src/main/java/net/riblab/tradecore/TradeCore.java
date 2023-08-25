package net.riblab.tradecore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import lombok.Getter;
import net.riblab.tradecore.block.BlockStateEventHandler;
import net.riblab.tradecore.craft.TCCraftingRecipes;
import net.riblab.tradecore.craft.TCFurnaceRecipes;
import net.riblab.tradecore.craft.VanillaCraftHandler;
import net.riblab.tradecore.dungeon.DungeonEventHandler;
import net.riblab.tradecore.dungeon.DungeonService;
import net.riblab.tradecore.integration.EconomyImplementer;
import net.riblab.tradecore.integration.VaultHook;
import net.riblab.tradecore.item.*;
import net.riblab.tradecore.item.attribute.ITCTool;
import net.riblab.tradecore.job.JobData;
import net.riblab.tradecore.job.JobHandler;
import net.riblab.tradecore.job.JobSkillHandler;
import net.riblab.tradecore.mob.CustomMobService;
import net.riblab.tradecore.mob.FakeVillagerService;
import net.riblab.tradecore.mob.TCMobs;
import net.riblab.tradecore.ui.UISell;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public final class TradeCore extends JavaPlugin {

    private static TradeCore instance;
    @Getter
    private EconomyImplementer economy;
    private VaultHook vaultHook;
    @Getter
    private ConfigManager configManager;
    @Getter
    private ProtocolManager protocolManager;
    private GeneralEventHandler eventHandler;
    private BlockStateEventHandler blockStateEventHandler;
    @Getter
    private JobHandler jobHandler;
    @Getter
    private JobSkillHandler jobSkillHandler;
    @Getter
    private ItemModService itemModService;
    @Getter
    private PlayerStatsHandler playerStatsHandler;
    @Getter
    private UltimateAdvancementAPI advancementAPI;
    @Getter
    private AdvancementService advancementService;
    @Getter
    private DungeonService dungeonService;
    @Getter
    private DungeonEventHandler dungeonEventHandler;
    private TCTasks tcTasks;

    public TradeCore() {
        instance = this;
    }

    public static TradeCore getInstance() {
        return instance;
    }

    @Getter
    private static boolean isWGLoaded;

    static {
        //安全にenumを初期化
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
        configManager = new ConfigManager();
        configManager.load();
        eventHandler = new GeneralEventHandler();
        blockStateEventHandler = new BlockStateEventHandler();
        jobHandler = new JobHandler();
        jobSkillHandler = new JobSkillHandler();
        jobSkillHandler.onDeserialize();
        itemModService = new ItemModService();
        playerStatsHandler = new PlayerStatsHandler();
        new VanillaCraftHandler();
        dungeonService = new DungeonService();
        dungeonEventHandler = new DungeonEventHandler();

        economy = new EconomyImplementer();
        vaultHook = new VaultHook();
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

        tcTasks = new TCTasks();
        advancementService = new AdvancementService();

        //買い取り商人
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        Player player = event.getPlayer();
                        PacketContainer packet = event.getPacket();
                        int id = packet.getIntegers().read(0);
                        Integer integer = FakeVillagerService.getCurrentID(player);
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
        
        Utils.forceInit(CustomMobService.class);
    }

    @Override
    public void onDisable() {
        vaultHook.unhook();
        TCCommands.onDisable();
        configManager.save();

        CustomMobService.deSpawnAll();

        Bukkit.getOnlinePlayers().forEach(player -> Utils.removeSlowDig(player));
        
        dungeonService.destroyAll();
    }
}
