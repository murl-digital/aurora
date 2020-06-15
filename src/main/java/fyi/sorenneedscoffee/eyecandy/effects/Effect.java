package fyi.sorenneedscoffee.eyecandy.effects;

public abstract class Effect {

    public abstract void init();

    public abstract void execute(EffectAction action);

    public abstract void cleanup();
}
