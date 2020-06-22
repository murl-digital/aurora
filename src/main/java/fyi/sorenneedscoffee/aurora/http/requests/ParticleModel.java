package fyi.sorenneedscoffee.aurora.http.requests;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

public class ParticleModel {
    @SerializedName("name")
    public String name;
    @SerializedName("region")
    public RegionModel region;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("region", region)
                .toString();
    }
}
