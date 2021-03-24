package digital.murl.aurora.http.models.lightning;

import com.google.gson.annotations.SerializedName;

public class LightningModel {

  @SerializedName("pointIds")
  public int[] pointIds;

  @SerializedName("spigotStrike")
  public boolean spigotStrike;
}
