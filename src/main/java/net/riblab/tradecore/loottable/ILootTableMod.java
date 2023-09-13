/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.loottable;

public interface ILootTableMod<T> {

    T getParam();

    void setParam(T level);
}
