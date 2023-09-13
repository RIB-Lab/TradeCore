/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.loottable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class LootTableMod<T> implements ILootTableMod<T> {

    /**
     * このmodパラメータ
     */
    @Getter
    @Setter
    private T param;
}
