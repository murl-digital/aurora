package fyi.sorenneedscoffee.eyecandy.http.requests;

import com.google.gson.annotations.SerializedName;

public class GlobalPotionModel {
    @SerializedName("type")
    public String potionType;
    @SerializedName("amplifier")
    public int amplifier;
}
