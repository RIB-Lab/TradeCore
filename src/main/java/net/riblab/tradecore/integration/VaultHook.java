/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.integration;

public sealed interface VaultHook permits VaultHookImpl {
    static VaultHook getImpl() {
        return VaultHookImpl.INSTANCE;
    }

    /**
     * Vaultに接続
     */
    void hook();

    /**
     * Vaultの接続を解除
     */
    void unhook();
}
