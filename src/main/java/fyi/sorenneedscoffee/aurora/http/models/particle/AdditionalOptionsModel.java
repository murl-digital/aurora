package fyi.sorenneedscoffee.aurora.http.models.particle;

import com.google.gson.annotations.SerializedName;
import org.bukkit.Color;

public class AdditionalOptionsModel {
    @SerializedName("dustColor")
    public Color dustColor;

    @SerializedName("dustSize")
    public float dustSize;

    @SerializedName("materialName")
    public String materialName;
}
