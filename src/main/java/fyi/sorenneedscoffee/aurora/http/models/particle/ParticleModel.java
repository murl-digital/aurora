package fyi.sorenneedscoffee.aurora.http.models.particle;

import com.google.gson.annotations.SerializedName;

public class ParticleModel {
    @SerializedName("name")
    public String name;

    @SerializedName("additionalOptions")
    public AdditionalOptionsModel options;

    @SerializedName("region")
    public RegionModel region;
}
