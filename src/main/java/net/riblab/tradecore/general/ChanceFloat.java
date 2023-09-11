/*
 * Copyright (c) 2023. RIBLaB
 */
package net.riblab.tradecore.general;

/**
 * 0より上から1以下の値しか取れない確率を扱う専用のFloat
 */
public class ChanceFloat {
    private final float value;

    public ChanceFloat(float value) {
        if(value <= 0 || value > 1)
            this.value = value;
        else 
            throw new IllegalArgumentException("0から1以外の確率は許可されていません");
    }
    
    public float get(){
        return value;
    }
}
