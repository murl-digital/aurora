package fyi.sorenneedscoffee.aurora.http.models.potion;

import com.google.gson.annotations.SerializedName;

public class GlobalPotionModel {
    @SerializedName("type")
    public String potionType;
    @SerializedName("amplifier")
    public int amplifier;
}
