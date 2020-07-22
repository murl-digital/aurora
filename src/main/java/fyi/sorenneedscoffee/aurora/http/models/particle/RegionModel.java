package fyi.sorenneedscoffee.aurora.http.models.particle;

import com.google.gson.annotations.SerializedName;
import fyi.sorenneedscoffee.aurora.effects.particle.RegionType;
import io.swagger.v3.oas.annotations.media.Schema;

public class RegionModel {
    @SerializedName("pointIDs")
    @Schema(name = "pointIDs", description = "IDs of points that will be used to build the region.")
    public int[] pointIds;

    @SerializedName("type")
    @Schema(name = "type", description = "This region's RegionType")
    public RegionType type;

    @SerializedName("density")
    @Schema(name = "density", description = "The density of the region. This value is ignored when the POINTS type is used.")
    public double density;

    @SerializedName("randomize")
    @Schema(name = "randomize", description = "Whether or not the exact position of the particles in the region is somewhat randomized. " +
            "This value is ignored when the POINTS type is used.")
    public boolean randomized;

    @SerializedName("equation")
    @Schema(name = "equation", description = "The mathematical function that is applied to the region when the EQUATION type is used. Internally, the format is f(x,y) = {YOUR EQUATION HERE}.")
    public String equation;
}
