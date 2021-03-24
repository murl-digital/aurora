package digital.murl.aurora.http.models.particle;

import com.google.gson.annotations.SerializedName;
import digital.murl.aurora.effects.particle.RegionType;

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
}
