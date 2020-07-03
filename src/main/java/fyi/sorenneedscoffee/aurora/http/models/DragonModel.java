package fyi.sorenneedscoffee.aurora.http.models;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

public class DragonModel {
    @SerializedName("pointId")
    public int pointId;
    @SerializedName("static")
    public boolean isStatic;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("pointId", pointId)
                .add("isStatic", isStatic)
                .toString();
    }
}
