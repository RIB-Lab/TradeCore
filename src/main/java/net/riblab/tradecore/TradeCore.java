package net.riblab.tradecore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import lombok.Getter;
import net.riblab.tradecore.block.BlockUtils;
import net.riblab.tradecore.command.TCCommands;
import net.riblab.tradecore.config.ConfigService;
import net.riblab.tradecore.craft.VanillaCraftInitializer;
import net.riblab.tradecore.dungeon.DungeonService;
import net.riblab.tradecore.entity.projectile.CustomProjectileService;
import net.riblab.tradecore.advancement.AdvancementInitializer;
import net.riblab.tradecore.entity.projectile.TCProjectile;
import net.riblab.tradecore.general.TCTasksInitializer;
import net.riblab.tradecore.general.Utils;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.integration.VaultHook;
import net.riblab.tradecore.item.PlayerItemModService;
import net.riblab.tradecore.job.skill.JobSkillService;
import net.riblab.tradecore.entity.mob.CustomMobService;
import net.riblab.tradecore.entity.mob.FakeVillagerService;
import net.riblab.tradecore.playerstats.PlayerStatsService;
import net.riblab.tradecore.ui.UIs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public final class TradeCore extends JavaPlugin {

    private static TradeCore instance;
    private VaultHook vaultHook;
    @Getter
    private ConfigService configService;
    @Getter
    private UltimateAdvancementAPI advancementAPI;

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
    public void onEnable() {
        configService = ConfigService.getImpl(getDataFolder());
        configService.load();
        JobSkillService.getImpl().onDeserialize();
        PlayerStatsService.getImpl().init();
        VanillaCraftInitializer.INSTANCE.init(this);

        vaultHook = VaultHook.getImpl();
        vaultHook.hook();
        isWGLoaded = getServer().getPluginManager().isPluginEnabled("WorldGuard");
        advancementAPI = UltimateAdvancementAPI.getInstance(this);
        CustomMobService.getImpl(); //ondisableでエラーが出ないように強制起動
        CustomProjectileService.getImpl();

        TCCommands.onEnable();

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!TCEconomy.getImpl().hasAccount(player))
                TCEconomy.getImpl().createPlayerAccount(player);

            BlockUtils.addSlowDig(player);
            PlayerItemModService.getImpl().updateEquipment(player);
            PlayerItemModService.getImpl().updateMainHand(player, player.getInventory().getHeldItemSlot());
        });

        TCTasksInitializer.INSTANCE.init();
        AdvancementInitializer.INSTANCE.init();

        //買い取り商人
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        Player player = event.getPlayer();
                        PacketContainer packet = event.getPacket();
                        int id = packet.getIntegers().read(0);
                        Integer integer = FakeVillagerService.getImpl().getCurrentID(player);
                        if (integer != null && id == integer) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    UIs.SELL.get().open(event.getPlayer());
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
        configService.save();

        CustomMobService.getImpl().deSpawnAll();
        CustomProjectileService.getImpl().deSpawnAll();

        Bukkit.getOnlinePlayers().forEach(BlockUtils::removeSlowDig);

        DungeonService.getImpl().destroyAll();
    }
}
