package net.riblab.tradecore;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.YamlConfigurations;
import lombok.Getter;

public class ConfigManager {
    
    @Configuration
    public static class CurrencyData {
        @Comment({"所持金"})
        public Map<UUID,Double> playerBank = new HashMap<>();
        @Comment({"所持プレイチケット数"})
        public Map<UUID,Integer> playerTickets = new HashMap<>();
    }
    
    @Getter
    private CurrencyData currencyData;
    private static final Path currencyConfigFile = new File(TradeCore.getInstance().getDataFolder(), "currency.yml").toPath();
    
    public void save(){
        YamlConfigurations.save(currencyConfigFile, CurrencyData.class, currencyData);
    }
    
    public void load(){
        // Load a new instance from the configuration file
        currencyData = YamlConfigurations.update(currencyConfigFile, CurrencyData.class);
    }
}