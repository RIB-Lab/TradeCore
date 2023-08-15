package net.riblab.tradecore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.*;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.riblab.tradecore.craft.VanillaCraftHandler;
import net.riblab.tradecore.item.ITCItem;
import net.riblab.tradecore.item.TCItems;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


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
    
    public TradeCore() {
        instance = this;
    }

    public static TradeCore getInstance() {
        return instance;
    }
    
    @Override
    public void onLoad(){
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(true)); // Load with verbose output

        CommandAPICommand setMoneyCommand = new CommandAPICommand("setmoney")
                .withArguments(new PlayerArgument("player"))
                .withArguments(new DoubleArgument("money", 0, Integer.MAX_VALUE))
                .executesPlayer((player, args) -> {
                    Player player1 = (Player) args.get(0);
                    Double money = (Double) args.get(1);

                    economy.withdrawPlayer(player1, economy.getBalance(player1));
                    economy.depositPlayer(player1, money);
                });
        setMoneyCommand.setPermission(CommandPermission.OP);
        setMoneyCommand.register();

        CommandAPICommand tcGiveCommand = new CommandAPICommand("tcgive")
                .withArguments(TCItems.customITCItemArgument("item"))
                .executesPlayer((player, args) -> {
                    ITCItem itcItem = (ITCItem) args.get(0);
                    player.getInventory().addItem(itcItem.getItemStack());
                });
        tcGiveCommand.setPermission(CommandPermission.OP);
        tcGiveCommand.register();
    }

    @Override
    public void onEnable() {
        configManager = new ConfigManager();
        configManager.load();
        eventHandler = new EventHandler();
        new VanillaCraftHandler();
        
        economy = new EconomyImplementer();
        vaultHook = new VaultHook();
        vaultHook.hook();

        CommandAPI.onEnable();

        Bukkit.getOnlinePlayers().forEach(player -> {
            if(!economy.hasAccount(player))
                economy.createPlayerAccount(player);
            
            addSlowDig(player);
        });

        protocolManager = ProtocolLibrary.getProtocolManager();

        //所持金と投票券表示
        new BukkitRunnable(){
            @Override
            public void run() {
                String negativeSpace = TCResourcePackData.IconsFont.NEGATIVE_SPACE.get_char();
                Bukkit.getOnlinePlayers().forEach(player ->{
                    int balance = (int)economy.getBalance(player);
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
        new BukkitRunnable(){
            @Override
            public void run() {
                configManager.save();
            }
        }.runTaskTimer(this, 0, 3600);
        
        //10分に1回プレイチケット配布
        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> economy.depositTickets(player, 1));
            }
        }.runTaskTimer(this, 0, 12000);
    }

    @Override
    public void onDisable() {
        vaultHook.unhook();
        CommandAPI.onDisable();
        configManager.save();
        
        Bukkit.getOnlinePlayers().forEach(player -> removeSlowDig(player));
    }

    /**
     * カスタムブロック破壊を実装
     * @param player
     */
    public static void addSlowDig(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, -1, -1, false, false), true);
    }

    /**
     * カスタムブロック破壊を除去
     * @param player
     */
    public static void removeSlowDig(Player player) {
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
    }

    /**
     * BukkitのOnDisableでエラーが出ないようにクラスを強制的にロードする
     * @param klass
     * @param <T>
     * @return
     */
    public static <T> Class<T> forceInit(Class<T> klass) {
        try {
            Class.forName(klass.getName(), true, klass.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);  // Can't happen
        }
        return klass;
    }
    
    public static void dropItemByLootTable(Block block, Map<Float, ITCItem> table){
        Random random = new Random();
        table.forEach((aFloat, itcItem) -> {
            float rand = random.nextFloat();
            if(rand < aFloat){
                block.getWorld().dropItemNaturally(block.getLocation(), itcItem.getItemStack());
            }
        });
    }
}
