package net.riblab.tradecore;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;

/**
 * Vaultプラグインと連携するクラス
 */
public class VaultHook {

    private TradeCore plugin = TradeCore.getInstance();

    private Economy provider;

    /**
     * Vaultに接続
     */
    public void hook() {
        provider = plugin.getEconomy();
        Bukkit.getServicesManager().register(Economy.class, this.provider, this.plugin, ServicePriority.Normal);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "VaultAPI hooked into " + ChatColor.AQUA + plugin.getName());
    }

    /**
     * Vaultの接続を解除
     */
    public void unhook() {
        Bukkit.getServicesManager().unregister(Economy.class, this.provider);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "VaultAPI unhooked from " + ChatColor.AQUA + plugin.getName());

    }
}
