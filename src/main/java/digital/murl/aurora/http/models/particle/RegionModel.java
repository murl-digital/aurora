package digital.murl.aurora.http.models.particle;

import com.google.gson.annotations.SerializedName;
import digital.murl.aurora.effects.particle.RegionType;

public class RegionModel {

  @SerializedName("pointIDs")
  public int[] pointIds;

  @SerializedName("type")
  public RegionType type;

  // Relative particle count to cuboid size
  @SerializedName("density")
  public double density;

  // Given particle count (known result)
  @SerializedName("quantity")
  public long quantity;

  @SerializedName("randomize")
  public boolean randomized;

  @SerializedName("equation")
  public String equation;
}
