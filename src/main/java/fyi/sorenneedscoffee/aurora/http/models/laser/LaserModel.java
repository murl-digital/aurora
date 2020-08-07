package fyi.sorenneedscoffee.aurora.http.models.laser;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;

public class LaserModel {
    @SerializedName("start")
    @Schema(name = "start", description = "The id of the starting point")
    public int startId;

    @SerializedName("end")
    @Schema(name = "end", description = "The id of the ending point")
    public int endId;
}
