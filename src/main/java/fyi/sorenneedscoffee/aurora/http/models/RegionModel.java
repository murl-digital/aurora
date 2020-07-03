package fyi.sorenneedscoffee.aurora.http.models;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;
import fyi.sorenneedscoffee.aurora.effects.particle.RegionType;

public class RegionModel {
    @SerializedName("pointIDs")
    public int[] pointIds;
    @SerializedName("type")
    public RegionType type;
    @SerializedName("density")
    public double density;
    @SerializedName("randomize")
    public boolean randomized;
    @SerializedName("equation")
    public String equation;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("pointIds", pointIds)
                .add("type", type)
                .add("density", density)
                .add("equation", equation)
                .toString();
    }
}
