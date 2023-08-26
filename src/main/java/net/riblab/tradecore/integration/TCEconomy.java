package net.riblab.tradecore.integration;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

public interface TCEconomy extends Economy {
    
    int getPlayTickets(OfflinePlayer offlinePlayer);

    void depositTickets(OfflinePlayer offlinePlayer, int amount);

    void withdrawTickets(OfflinePlayer offlinePlayer, int amount);
}
