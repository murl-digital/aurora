package fyi.sorenneedscoffee.aurora.http.models.particle;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;

public class ParticleModel {
    @SerializedName("name")
    @Schema(name = "name", description = "Name of the particle that will be used")
    public String name;

    @SerializedName("region")
    public RegionModel region;
}
