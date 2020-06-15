package fyi.sorenneedscoffee.eyecandy.http.requests;

import com.google.gson.annotations.SerializedName;

public class TimeShiftModel {
    @SerializedName("pointId")
    public int pointId;
    @SerializedName("amount")
    public long amount;
    @SerializedName("period")
    public long period;
}
