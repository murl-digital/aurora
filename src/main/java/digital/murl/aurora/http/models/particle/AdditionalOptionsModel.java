package digital.murl.aurora.http.models.particle;

import com.google.gson.annotations.SerializedName;

public class AdditionalOptionsModel {

  @SerializedName("dustColor")
  public int[] dustColor;

  @SerializedName("dustSize")
  public float dustSize;

  @SerializedName("materialName")
  public String materialName;

  @SerializedName("toColor")
  public int[] toColor;
}
