/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.modifier;

import net.riblab.tradecore.item.mod.IItemMod;

import java.util.List;

/**
 * 数値がランダム化されたあるItemModを製作するクラス
 */
public interface IRandomItemModCreator extends IModifier<List<IItemMod<?>>> {
}
