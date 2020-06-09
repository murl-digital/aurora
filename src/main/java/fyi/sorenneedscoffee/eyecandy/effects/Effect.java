package fyi.sorenneedscoffee.eyecandy.effects;

import fyi.sorenneedscoffee.eyecandy.util.Point;

public interface Effect {
    public void init(Point point);
    public void cleanup();
}
