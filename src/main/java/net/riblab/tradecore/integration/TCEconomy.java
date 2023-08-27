package net.riblab.tradecore.integration;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

public interface TCEconomy extends Economy {

    /**
     * プレイヤーの持っているプレイチケットの数を取得する
     */
    int getPlayTickets(OfflinePlayer offlinePlayer);

    /**
     * プレイヤーにプレイチケットを渡す
     * @param offlinePlayer プレイヤー
     * @param amount 量
     */
    void depositTickets(OfflinePlayer offlinePlayer, int amount);

    /**
     * プレイヤーからプレイチケットを差し引く
     */
    void withdrawTickets(OfflinePlayer offlinePlayer, int amount);
}
