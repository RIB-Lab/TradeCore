/*
 * Copyright (c) 2023. RIBLaB 
 */
package net.riblab.tradecore.item.mod;

import net.riblab.tradecore.modifier.IToolStatsModifier;

public class ModToolStatsI extends ItemMod<IToolStatsModifier.ToolStats> implements IToolStatsModifier {


    public ModToolStatsI(ToolStats param) {
        super(param);
    }

    @Override
    public String getLore() {
        return null;
    } //TODO:何らかの方法で採掘レベルを表示する

    @Override
    public ToolStats apply(ToolStats originalValue, ToolStats modifiedValue) {
        return getParam();
    }
}
