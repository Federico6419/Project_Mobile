package project.mobile

import com.google.gson.annotations.SerializedName

data class Difference_Json(
    @SerializedName("winner")
    var winner: String?,
    @SerializedName("difference")
    var difference: Int?
)