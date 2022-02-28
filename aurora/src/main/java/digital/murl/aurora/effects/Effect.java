package digital.murl.aurora.effects;

public abstract class Effect<T> {
    public abstract void init(T params);
    public abstract void start();
    public abstract void stop();
    public abstract void cleanup();
}
