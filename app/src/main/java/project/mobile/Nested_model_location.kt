package project.mobile
import com.google.gson.annotations.SerializedName

data class Nested_model_location(
    @SerializedName("features")
    var features : List<Features>?
)
data class Features(
    @SerializedName("properties")
    var properties: Properties?
)
data class Properties(
    @SerializedName("city")
    val city: String?
)
