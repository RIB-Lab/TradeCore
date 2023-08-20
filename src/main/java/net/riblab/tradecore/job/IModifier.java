package net.riblab.tradecore.job;

public interface IModifier<T> {
    /**
     * 値を変化させる
     * @param originalValue 元々の値
     * @param modifiedValue 他のIModifierを通って変化した値
     * @return
     */

    public T apply(T originalValue, T modifiedValue);
}