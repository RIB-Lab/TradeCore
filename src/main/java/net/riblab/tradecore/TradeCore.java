package net.riblab.tradecore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.craft.TCCraftingRecipes;
import net.riblab.tradecore.craft.TCFurnaceRecipes;
import net.riblab.tradecore.craft.VanillaCraftHandler;
import net.riblab.tradecore.integration.EconomyImplementer;
import net.riblab.tradecore.integration.VaultHook;
import net.riblab.tradecore.item.*;
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
    private EventHandler eventHandler;
    @Getter
    private JobHandler jobHandler;
    @Getter
    private JobSkillHandler jobSkillHandler;
    @Getter
    private ItemModService itemModService;
    @Getter
    private PlayerStatsHandler playerStatsHandler;

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
        eventHandler = new EventHandler();
        jobHandler = new JobHandler();
        jobSkillHandler = new JobSkillHandler();
        jobSkillHandler.onDeserialize();
        itemModService = new ItemModService();
        playerStatsHandler = new PlayerStatsHandler();
        new VanillaCraftHandler();

        economy = new EconomyImplementer();
        vaultHook = new VaultHook();
        vaultHook.hook();
        isWGLoaded = getServer().getPluginManager().isPluginEnabled("WorldGuard");

        TCCommands.onEnable();

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!economy.hasAccount(player))
                economy.createPlayerAccount(player);

            Utils.addSlowDig(player);
            itemModService.updateEquipment(player);
            itemModService.updateMainHand(player, player.getInventory().getHeldItemSlot());
        });

        protocolManager = ProtocolLibrary.getProtocolManager();

        //所持金と投票券表示
        new BukkitRunnable() {
            @Override
            public void run() {
                String negativeSpace = TCResourcePackData.IconsFont.NEGATIVE_SPACE.get_char();
                Bukkit.getOnlinePlayers().forEach(player -> {
                    int balance = (int) economy.getBalance(player);
                    int tickets = economy.getPlayTickets(player);
                    Component text = Component.text("");
                    text = text.append(Component.text(negativeSpace + negativeSpace + negativeSpace + negativeSpace + TCResourcePackData.IconsFont.COIN.get_char()).font(TCResourcePackData.iconsFontName));
                    text = text.append(Component.text(" " + balance).font(TCResourcePackData.yPlus12FontName));
                    text = text.append(Component.text("                         " + TCResourcePackData.IconsFont.VOTE_TICKET.get_char()).font(TCResourcePackData.iconsFontName));
                    text = text.append(Component.text(" " + tickets).font(TCResourcePackData.yPlus12FontName));
                    player.sendActionBar(text);
                });
            }
        }.runTaskTimer(this, 0, 20);

        //定期的にコンフィグを保存
        new BukkitRunnable() {
            @Override
            public void run() {
                configManager.save();
            }
        }.runTaskTimer(this, 0, 3600);

        //10分に1回プレイチケット配布
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> economy.depositTickets(player, 1));
            }
        }.runTaskTimer(this, 0, 12000);

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
    }
}
