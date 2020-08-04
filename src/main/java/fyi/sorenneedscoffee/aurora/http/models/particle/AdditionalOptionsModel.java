package fyi.sorenneedscoffee.aurora.http.models.particle;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.bukkit.Color;

public class AdditionalOptionsModel {
    @SerializedName("dustColor")
    @Schema(name = "dustColor", description = "Used if the particle type is REDSTONE, this sets the particle's color.", implementation = Color.class)
    public Color dustColor;

    @SerializedName("dustSize")
    @Schema(name = "dustSize", description = "Used if the particle type is REDSTONE, this sets the particle's size.")
    public float dustSize;

    @SerializedName("materialName")
    @Schema(name = "materialName", description = "Used if the particle type is BLOCK_CRACK, BLOCK_DUST, FALLING_DUST, and ITEM_CRACK. Please note that if you are using ITEM_CRACK the material must be an item.")
    public String materialName;
}
