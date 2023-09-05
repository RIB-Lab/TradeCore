package net.riblab.tradecore;

import lombok.Getter;
import net.riblab.tradecore.advancement.AdvancementInitializer;
import net.riblab.tradecore.block.BlockUtils;
import net.riblab.tradecore.command.TCCommands;
import net.riblab.tradecore.config.DataService;
import net.riblab.tradecore.craft.CraftingRecipesRegistry;
import net.riblab.tradecore.craft.TCCraftingRecipe;
import net.riblab.tradecore.craft.TCCraftingRecipes;
import net.riblab.tradecore.craft.VanillaCraftInitializer;
import net.riblab.tradecore.dungeon.DungeonService;
import net.riblab.tradecore.entity.mob.CustomMobService;
import net.riblab.tradecore.entity.projectile.CustomProjectileService;
import net.riblab.tradecore.general.TCTasksInitializer;
import net.riblab.tradecore.general.Utils;
import net.riblab.tradecore.integration.ProtocolInitializer;
import net.riblab.tradecore.integration.TCEconomy;
import net.riblab.tradecore.integration.VaultHook;
import net.riblab.tradecore.item.PlayerItemModService;
import net.riblab.tradecore.item.base.ITCItem;
import net.riblab.tradecore.item.base.TCItems;
import net.riblab.tradecore.job.skill.JobSkillService;
import net.riblab.tradecore.playerstats.PlayerStatsService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;


public class TradeCore extends JavaPlugin {

    private static TradeCore instance;
    private VaultHook vaultHook;
    
    @Getter
    private static boolean isUnitTest = false;

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
        if(!isUnitTest){
            DataService.getImpl().loadAll();
        }
        
        JobSkillService.getImpl().onDeserialize();
        PlayerStatsService.getImpl().init();
        VanillaCraftInitializer.INSTANCE.init(this);

        if(!isUnitTest){
            vaultHook = VaultHook.getImpl();
            vaultHook.hook();
            TCCommands.onEnable();
        }
        
        isWGLoaded = getServer().getPluginManager().isPluginEnabled("WorldGuard");
        CustomMobService.getImpl(); //ondisableでエラーが出ないように強制起動
        CustomProjectileService.getImpl();
        Utils.forceInit(BlockUtils.class);

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!TCEconomy.getImpl().hasAccount(player))
                TCEconomy.getImpl().createPlayerAccount(player);

            BlockUtils.addSlowDig(player);
            PlayerItemModService.getImpl().updateEquipment(player);
            PlayerItemModService.getImpl().updateMainHand(player, player.getInventory().getHeldItemSlot());
        });

        TCTasksInitializer.INSTANCE.init();

        if(!isUnitTest){
            AdvancementInitializer.INSTANCE.init();
            ProtocolInitializer.INSTANCE.init();
        }
    }

    @Override
    public void onDisable() {
        if(!isUnitTest){
            vaultHook.unhook();
            DataService.getImpl().saveAll();
        }

        CustomMobService.getImpl().deSpawnAll();
        CustomProjectileService.getImpl().deSpawnAll();

        Bukkit.getOnlinePlayers().forEach(BlockUtils::removeSlowDig);

        DungeonService.getImpl().destroyAll();
    }

    /**
     * ユニットテストモードの有効/無効を切り替える
     * @param flag フラグ
     */
    public static void setIsUnitTest(boolean flag){
        isUnitTest = flag;
        Bukkit.getLogger().info("TradeCoreのテストモードが有効になりました。");
        Bukkit.getLogger().info("このモードではコンフィグのロードがされないのと、外部ライブラリに依存した機能が無効化されます");
    }
}
