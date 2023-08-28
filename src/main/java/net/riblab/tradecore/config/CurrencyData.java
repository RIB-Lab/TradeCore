package net.riblab.tradecore.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 保存するコンフィグの型
 */
@Configuration
public class CurrencyData {
    @Comment({"所持金"})
    public Map<UUID, Double> playerBank = new HashMap<>();
    @Comment({"所持プレイチケット数"})
    public Map<UUID, Integer> playerTickets = new HashMap<>();
}
