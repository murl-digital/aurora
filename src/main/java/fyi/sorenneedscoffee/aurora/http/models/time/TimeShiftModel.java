package fyi.sorenneedscoffee.aurora.http.models.time;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;

public class TimeShiftModel {
    @SerializedName("amount")
    @Schema(name = "amount", description = "Amount to shift the daylight cycle by")
    public long amount;
}
