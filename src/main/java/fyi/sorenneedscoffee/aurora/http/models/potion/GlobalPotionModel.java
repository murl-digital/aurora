package fyi.sorenneedscoffee.aurora.http.models.potion;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

public class GlobalPotionModel {
    @SerializedName("type")
    @Schema(name = "type", description = "Potion effect type. See external documentation.", externalDocs = @ExternalDocumentation(
            description = "List of available potion effect types",
            url = "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html"
    ))
    public String potionType;

    @Schema(name = "amplifier", description = "Potion effect amplifier")
    @SerializedName("amplifier")
    public int amplifier;
}
