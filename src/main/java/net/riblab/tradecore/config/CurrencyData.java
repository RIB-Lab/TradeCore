/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.config;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 保存するコンフィグの型
 */
public class CurrencyData {
    public Map<UUID, Double> playerBank = new HashMap<>();
    public Map<UUID, Integer> playerTickets = new HashMap<>();
}
