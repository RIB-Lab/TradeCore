/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.integration;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.riblab.tradecore.TradeCore;
import net.riblab.tradecore.entity.mob.FakeVillagerService;
import net.riblab.tradecore.general.ErrorMessages;
import net.riblab.tradecore.ui.UIs;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

/**
 * メインプラグインでエラーが出ないようにProtocolLibの初期化を隔離するクラス
 */
public enum ProtocolInitializer {
    INSTANCE;

    boolean isInit = false;

    public void init() {
        if (isInit)
            throw new RuntimeException(ErrorMessages.PROTOCOLLIB_INIT_TWO_TIMES.get());

        //買い取り商人
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(TradeCore.getInstance(), PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        Player player = event.getPlayer();
                        PacketContainer packet = event.getPacket();
                        int id = packet.getIntegers().read(0);
                        FakeVillagerService.getImpl().getCurrentID(player).ifPresent(integer -> {
                            if (!Objects.equals(id, integer)) {
                                return;
                            }

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    UIs.SELL.get().open(event.getPlayer());
                                }
                            }.runTaskLater(TradeCore.getInstance(), 0);
                        });
                    }
                }
        );

        isInit = true;
    }
}
