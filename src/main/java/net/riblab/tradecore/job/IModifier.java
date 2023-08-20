package net.riblab.tradecore.job;

public interface IModifier<T> {

    public T apply(T value);
}