package fyi.sorenneedscoffee.aurora.http.models.potion;

import com.google.gson.annotations.SerializedName;
import fyi.sorenneedscoffee.aurora.util.annotation.StaticModel;

@StaticModel("potion")
public class PotionModel {
    @SerializedName("type")
    public String potionType;

    @SerializedName("amplifier")
    public int amplifier;
}
