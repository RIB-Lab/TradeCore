/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.modifier;

public interface IModifier<T> {
    /**
     * 値を変化させる
     *
     * @param originalValue 元々の値
     * @param modifiedValue 他のIModifierを通って変化した値(注意：Tが参照型の時originalValueとmodifiedValueは常に同じ！)
     * @return このmodifierを通って変化した値
     */

    T apply(T originalValue, T modifiedValue);
}