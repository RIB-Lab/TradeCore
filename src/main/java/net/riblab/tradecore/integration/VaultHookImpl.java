/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.integration;

import net.milkbowl.vault.economy.Economy;
import net.riblab.tradecore.TradeCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;

/**
 * Vaultプラグインと連携するクラス
 */
enum VaultHookImpl implements VaultHook {
    INSTANCE;

    private final TradeCore plugin = TradeCore.getInstance();

    private Economy provider;

    /**
     * プラグインが起動する途中にエラーを吐いた時、disableの時unhookでエラーが出るのを防止する変数
     */
    private boolean isHooked = false;

    @Override
    public void hook() {
        if (isHooked)
            return;

        provider = TCEconomy.getImpl();
        Bukkit.getServicesManager().register(Economy.class, this.provider, this.plugin, ServicePriority.Normal);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "VaultAPI hooked into " + ChatColor.AQUA + plugin.getName());
        isHooked = true;
    }

    @Override
    public void unhook() {
        if (!isHooked)
            return;

        Bukkit.getServicesManager().unregister(Economy.class, this.provider);
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "VaultAPI unhooked from " + ChatColor.AQUA + plugin.getName());
        isHooked = false;
    }
}
