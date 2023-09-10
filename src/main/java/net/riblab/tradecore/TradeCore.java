/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore;

import lombok.Getter;
import net.riblab.tradecore.advancement.AdvancementInitializer;
import net.riblab.tradecore.block.BlockUtils;
import net.riblab.tradecore.command.TCCommands;
import net.riblab.tradecore.config.DataService;
import net.riblab.tradecore.craft.VanillaCraftInitializer;
import net.riblab.tradecore.dungeon.DungeonService;
import net.riblab.tradecore.entity.mob.CustomMobService;
import net.riblab.tradecore.entity.projectile.CustomProjectileService;
import net.riblab.tradecore.general.Utils;
import net.riblab.tradecore.general.task.TCTasksInitializer;
import net.riblab.tradecore.integration.ProtocolInitializer;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.integration.VaultHook;
import net.riblab.tradecore.item.ILootTable;
import net.riblab.tradecore.item.LootTables;
import net.riblab.tradecore.item.PlayerItemModService;
import net.riblab.tradecore.job.skill.JobSkillService;
import net.riblab.tradecore.playerstats.PlayerStatsService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;


public class TradeCore extends JavaPlugin {

    private static final String WORLDGUARD_PLUGIN_NAME = "WorldGuard";

    private static TradeCore instance;

    public static TradeCore getInstance() {
        return instance;
    }

    @Getter
    private static boolean isWGLoaded;

    static {
        Utils.initializeEnumSafely();
    }

    private VaultHook vaultHook;

    public TradeCore() {
        instance = this;
    }

    @Override
    public void onEnable() {
        if (!isJUnitTest()) {
            DataService.getImpl().loadAll();
        }

        JobSkillService.getImpl().onDeserialize();
        PlayerStatsService.getImpl().init();
        VanillaCraftInitializer.INSTANCE.init(this);

        if (!isJUnitTest()) {
            vaultHook = VaultHook.getImpl();
            vaultHook.hook();
            TCCommands.onEnable();
        }

        isWGLoaded = getServer().getPluginManager().isPluginEnabled(WORLDGUARD_PLUGIN_NAME);
        CustomMobService.getImpl(); //ondisableでエラーが出ないように強制起動
        CustomProjectileService.getImpl();
        Utils.forceInit(BlockUtils.class);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!TCEconomy.getImpl().hasAccount(player))
                TCEconomy.getImpl().createPlayerAccount(player);

            BlockUtils.addSlowDig(player);
            PlayerItemModService.getImpl().updateEquipment(player);
            PlayerItemModService.getImpl().updateMainHand(player, player.getInventory().getHeldItemSlot());
        }

        TCTasksInitializer.INSTANCE.init();

        if (!isJUnitTest()) {
            AdvancementInitializer.INSTANCE.init();
            ProtocolInitializer.INSTANCE.init();
        }
    }

    @Override
    public void onDisable() {
        if (!isJUnitTest()) {
            vaultHook.unhook();
            DataService.getImpl().saveAll();
        }

        CustomMobService.getImpl().deSpawnAll();
        CustomProjectileService.getImpl().deSpawnAll();

        Bukkit.getOnlinePlayers().forEach(BlockUtils::removeSlowDig);

        DungeonService.getImpl().destroyAll();
    }

    /**
     * テスト環境であるか確認する。<br>
     * (テスト環境では他のプラグインが使えない)
     */
    public static boolean isJUnitTest() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }
        }
        return false;
    }
}
