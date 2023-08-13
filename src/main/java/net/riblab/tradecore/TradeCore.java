package net.riblab.tradecore;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;


public final class TradeCore extends JavaPlugin implements Listener {

    private static TradeCore instance;
    public EconomyImplementer economy;
    private VaultHook vaultHook;

    public final HashMap<UUID,Double> playerBank = new HashMap<>();

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
    }

    @Override
    public void onEnable() {
        economy = new EconomyImplementer();
        vaultHook = new VaultHook();
        vaultHook.hook();

        CommandAPI.onEnable();
        
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        vaultHook.unhook();
        CommandAPI.onDisable();
    }
    
    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event){
        if(!economy.hasAccount(event.getPlayer()))
            economy.createPlayerAccount(event.getPlayer());
    }
}
