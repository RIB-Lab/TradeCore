/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.general;

import net.riblab.tradecore.item.base.ITCItem;

import java.util.Map;

/**
 * レジストリクラスの処理を共通化するためのインターフェース
 * @param <T>
 */
public interface IRegistry<T> {
    
    void clear();

    void addAll(T elements);
    
    T getUnmodifiableElements();
}
