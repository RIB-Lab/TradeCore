/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.item.mod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

/**
 * ゲームで何らかの意味がある値を保存するアイテムの拡張要素
 */
@AllArgsConstructor
public class ItemMod<T> implements IItemMod<T> {

    /**
     * このmodパラメータ（ランダムmodとしてアイテム内に焼く場合、Gson対応型である必要がある）
     */
    @Getter
    @Setter
    private T param;

    @Override
    public Optional<String> getLore() {
        return Optional.of("ダミー");
    }
}
