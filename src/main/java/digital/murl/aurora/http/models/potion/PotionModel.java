package digital.murl.aurora.http.models.potion;

import com.google.gson.annotations.SerializedName;
import digital.murl.aurora.annotations.StaticModel;

@StaticModel("potion")
public class PotionModel {

  @SerializedName("type")
  public String potionType;

  @SerializedName("amplifier")
  public int amplifier;
}
