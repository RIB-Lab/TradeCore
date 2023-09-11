/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.integration;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

import javax.annotation.ParametersAreNonnullByDefault;

public sealed interface TCEconomy extends Economy permits EconomyImpl {

    static TCEconomy getImpl() {
        return EconomyImpl.INSTANCE;
    }

    /**
     * プレイヤーの持っているプレイチケットの数を取得する
     */
    @ParametersAreNonnullByDefault
    int getPlayTickets(OfflinePlayer offlinePlayer);

    /**
     * プレイヤーにプレイチケットを渡す
     *
     * @param offlinePlayer プレイヤー
     * @param amount        量
     */
    @ParametersAreNonnullByDefault
    void depositTickets(OfflinePlayer offlinePlayer, int amount);

    /**
     * プレイヤーからプレイチケットを差し引く
     */
    @ParametersAreNonnullByDefault
    void withdrawTickets(OfflinePlayer offlinePlayer, int amount);
}
