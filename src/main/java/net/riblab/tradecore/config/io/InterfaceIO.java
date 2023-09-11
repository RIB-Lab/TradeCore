/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.config.io;

import java.io.File;

/**
 * オブジェクトをファイルから読み取ったり書きこんだりするインターフェース
 */
public interface InterfaceIO<T> {
    T deserialize(File fileToLoad);

    void serialize(T objectToSave, File fileToSave);
}
