package project.mobile
import com.google.gson.annotations.SerializedName

data class Nested_model(
    val location: Location?,
    val current: Current?
)
data class Location(
    @SerializedName("name")
    val name: String?,
    @SerializedName("region")
    val region: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("lat")
    val lat: String?,
    @SerializedName("lon")
    val lon: String?,
    @SerializedName("tz_id")
    val tz_id: String?,
    @SerializedName("localtime_epoch")
    val localtime_epoch: String?,
    @SerializedName("localtime")
    val localtime: String?
)

data class Current(
    @SerializedName("last_updated_epoch")
    val last_updated_epoch: String?,
    @SerializedName("last_updated")
    val last_updated: String?,
    @SerializedName("temp_c")
    val temp_c: String?,
    @SerializedName("temp_f")
    val temp_f: String?,
    @SerializedName("is_day")
    val is_day: String?,
    val condition: Condition?,
    @SerializedName("wind_mph")
    val wind_mph: String?,
    @SerializedName("wind_kph")
    val wind_kph: String?,
    @SerializedName("wind_degree")
    val wind_degree: String?,
    @SerializedName("wind_dir")
    val wind_dir: String?,
    @SerializedName("pressure_mb")
    val pressure_mb: String?,
    @SerializedName("pressure_in")
    val pressure_in: String?,
    @SerializedName("precip_mm")
    val precip_mm: String?,
    @SerializedName("precip_in")
    val precip_in: String?,
    @SerializedName("humidity")
    val humidity: String?,
    @SerializedName("cloud")
    val cloud: String?,
    @SerializedName("feelslike_c")
    val feelslike_c: String?,
    @SerializedName("feelslike_f")
    val feelslike_f: String?,
    @SerializedName("vis_km")
    val vis_km: String?,
    @SerializedName("vis_miles")
    val vis_miles: String?,
    @SerializedName("uv")
    val uv: String?,
    @SerializedName("gust_mph")
    val gust_mph: String?,
    @SerializedName("gust_kph")
    val gust_kph: String?
)

data class Condition(
    @SerializedName("text")
    val text: String?,
)

