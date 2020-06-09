package fyi.sorenneedscoffee.eyecandy.util;

import fyi.sorenneedscoffee.eyecandy.effects.Effect;
import fyi.sorenneedscoffee.eyecandy.effects.EffectAction;
import fyi.sorenneedscoffee.eyecandy.effects.Toggleable;
import fyi.sorenneedscoffee.eyecandy.effects.implementations.DragonEffect;

import java.util.ArrayList;
import java.util.List;

public class EffectManager {
    private static List<Toggleable> activeEffects = new ArrayList<>();

    public static void doAction(EffectAction action, Effect effect, Point point) {
        if(action == EffectAction.START) {
            effect.init(point);
            if(effect instanceof Toggleable) {
                ((Toggleable) effect).execute(action);
                activeEffects.add((Toggleable) effect);
            }
        } else if(action == EffectAction.STOP) {
            if(effect instanceof DragonEffect) {
                DragonEffect dragonEffect = (DragonEffect) effect;
                dragonEffect.point = point;
                activeEffects.get(activeEffects.indexOf(dragonEffect)).execute(action);
                activeEffects.get(activeEffects.indexOf(dragonEffect)).cleanup();
                activeEffects.remove(activeEffects.get(activeEffects.indexOf(dragonEffect)));
            }
        }
    }
}
