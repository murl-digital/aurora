package fyi.sorenneedscoffee.eyecandy.http.requests;

import com.google.gson.annotations.SerializedName;
import fyi.sorenneedscoffee.eyecandy.effects.EffectAction;

public class DragonRequest {
    @SerializedName("PointID")
    public int pointId;
    @SerializedName("Action")
    public EffectAction action;
    @SerializedName("Static")
    public boolean isStatic;
}
