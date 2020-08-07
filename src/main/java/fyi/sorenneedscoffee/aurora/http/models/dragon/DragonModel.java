package fyi.sorenneedscoffee.aurora.http.models.dragon;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;

public class DragonModel {
    @SerializedName("pointId")
    @Schema(name = "pointId", description = "Id of the point that the effect will originate from")
    public int pointId;

    @SerializedName("static")
    @Schema(name = "static", description = "Whether or not the dragon will rise into the air")
    public boolean isStatic;
}
