package fyi.sorenneedscoffee.aurora.http.models.bossbar;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;

public class BossBarModel {
    @SerializedName("color")
    @Schema(name = "color", description = "The color of the boss bar", allowableValues = {
            "PINK",
            "BLUE",
            "RED",
            "GREEN",
            "YELLOW",
            "PURPLE",
            "WHITE"
    })
    public String color;

    @SerializedName("title")
    @Schema(name = "title", description = "The title that will display above the bar.")
    public String title;
}
