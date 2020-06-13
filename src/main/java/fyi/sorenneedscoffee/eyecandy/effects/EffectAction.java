package fyi.sorenneedscoffee.eyecandy.effects;

public enum EffectAction {
    START("started"), STOP("stopped"), RESTART("restarted"), TRIGGER("executed");

    public String verb;

    private EffectAction(String verb) {
        this.verb = verb;
    }
}
