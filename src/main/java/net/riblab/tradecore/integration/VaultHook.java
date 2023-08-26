package net.riblab.tradecore.integration;

public interface VaultHook {
    /**
     * Vaultに接続
     */
    void hook();

    /**
     * Vaultの接続を解除
     */
    void unhook();
}
