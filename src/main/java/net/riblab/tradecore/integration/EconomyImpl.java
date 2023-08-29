package net.riblab.tradecore.integration;


import net.milkbowl.vault.economy.EconomyResponse;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.config.CurrencyData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * 他プラグインと連携するためにVaultが定義した経済システムを実装する
 */
enum EconomyImpl implements TCEconomy {
    INSTANCE;

    private final CurrencyData data = TradeCore.getInstance().getConfigService().getCurrencyData();

    /**
     * 初期所持金
     */
    private static final double startingBalance = 50;

    @Override
    public boolean isEnabled() {
        return TradeCore.getInstance().isEnabled();
    }

    @Override
    public String getName() {
        return "TradeCore";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double v) {
        return Integer.toString((int) v);
    }

    @Override
    public String currencyNamePlural() {
        return "rib";
    }

    @Override
    public String currencyNameSingular() {
        return "rib";
    }

    @Override
    public boolean hasAccount(String s) {
        return true;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        return data.playerBank.get(uuid) != null;
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return true;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return true;
    }

    @Override
    public double getBalance(String s) {
        Player player = Bukkit.getPlayer(s);
        UUID uuid = player.getUniqueId();
        return data.playerBank.get(uuid);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        UUID uuid = offlinePlayer.getUniqueId();
        return data.playerBank.get(uuid);
    }

    @Override
    public double getBalance(String s, String s1) {
        Player player = Bukkit.getPlayer(s);
        UUID uuid = player.getUniqueId();
        return data.playerBank.get(uuid);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        UUID uuid = offlinePlayer.getUniqueId();
        return data.playerBank.get(uuid);
    }

    @Override
    public boolean has(String s, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        UUID uuid = offlinePlayer.getUniqueId();
        return data.playerBank.get(uuid) > v;
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        UUID uuid = offlinePlayer.getUniqueId();
        return data.playerBank.get(uuid) > v;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        Player player = Bukkit.getPlayer(s);
        UUID uuid = player.getUniqueId();
        double oldBalance = data.playerBank.get(uuid);
        double newBalance = oldBalance - v;
        if (newBalance < 0) {
            return new EconomyResponse(0, oldBalance, EconomyResponse.ResponseType.FAILURE, null);
        }
        data.playerBank.put(uuid, newBalance);
        return new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        UUID uuid = offlinePlayer.getUniqueId();
        double oldBalance = data.playerBank.get(uuid);
        double newBalance = oldBalance - v;
        if (newBalance < 0) {
            return new EconomyResponse(0, oldBalance, EconomyResponse.ResponseType.FAILURE, null);
        }
        data.playerBank.put(uuid, newBalance);
        return new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        Player player = Bukkit.getPlayer(s);
        UUID uuid = player.getUniqueId();
        double oldBalance = data.playerBank.get(uuid);
        double newBalance = oldBalance - v;
        if (newBalance < 0) {
            return new EconomyResponse(0, oldBalance, EconomyResponse.ResponseType.FAILURE, null);
        }
        data.playerBank.put(uuid, newBalance);
        return new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        UUID uuid = offlinePlayer.getUniqueId();
        double oldBalance = data.playerBank.get(uuid);
        double newBalance = oldBalance - v;
        if (newBalance < 0) {
            return new EconomyResponse(0, oldBalance, EconomyResponse.ResponseType.FAILURE, null);
        }
        data.playerBank.put(uuid, newBalance);
        return new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        Player player = Bukkit.getPlayer(s);
        UUID uuid = player.getUniqueId();
        double oldBalance = data.playerBank.get(uuid);
        double newBalance = oldBalance + v;
        data.playerBank.put(uuid, newBalance);
        return new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        UUID uuid = offlinePlayer.getUniqueId();
        double oldBalance = data.playerBank.get(uuid);
        double newBalance = oldBalance + v;
        data.playerBank.put(uuid, newBalance);
        return new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        Player player = Bukkit.getPlayer(s);
        UUID uuid = player.getUniqueId();
        double oldBalance = data.playerBank.get(uuid);
        double newBalance = oldBalance + v;
        data.playerBank.put(uuid, newBalance);
        return new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        UUID uuid = offlinePlayer.getUniqueId();
        double oldBalance = data.playerBank.get(uuid);
        double newBalance = oldBalance + v;
        data.playerBank.put(uuid, newBalance);
        return new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        if (hasAccount(offlinePlayer))
            return false;

        UUID uuid = offlinePlayer.getUniqueId();
        data.playerBank.put(uuid, startingBalance);
        data.playerTickets.put(uuid, 0);
        return true;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        if (hasAccount(offlinePlayer))
            return false;

        UUID uuid = offlinePlayer.getUniqueId();
        data.playerBank.put(uuid, startingBalance);
        data.playerTickets.put(uuid, 0);
        return true;
    }

    /**
     * プレイヤーの所持チケットを確認
     */
    @Override
    public int getPlayTickets(@Nonnull OfflinePlayer offlinePlayer) {
        return data.playerTickets.get(offlinePlayer.getUniqueId());
    }

    /**
     * プレイヤーにチケットを与える
     */
    @Override
    public void depositTickets(@Nonnull OfflinePlayer offlinePlayer, int amount) {
        UUID uuid = offlinePlayer.getUniqueId();
        int oldTickets = data.playerTickets.get(uuid);
        int newTickets = oldTickets + amount;
        data.playerTickets.put(uuid, newTickets);
    }

    /**
     * プレイヤーからチケットを引く
     */
    @Override
    public void withdrawTickets(@Nonnull OfflinePlayer offlinePlayer, int amount) {
        UUID uuid = offlinePlayer.getUniqueId();
        int oldTickets = data.playerTickets.get(uuid);
        int newTickets = oldTickets - amount;
        data.playerTickets.put(uuid, newTickets);
    }
}