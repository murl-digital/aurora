package fyi.sorenneedscoffee.aurora.http.models.dragon;

import com.google.gson.annotations.SerializedName;

public class DragonModel {

  @SerializedName("pointId")
  public int pointId;

  @SerializedName("static")
  public boolean isStatic;
}
