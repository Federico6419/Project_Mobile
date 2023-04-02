package project.mobile

import com.google.gson.annotations.SerializedName

data class Flask_Json(
    @SerializedName("numberOfUsers")
    var numberOfUsers: Int?,
    @SerializedName("users")
    var users: List<Users>?
)
data class Users(
    @SerializedName("username")
    val username: String?,
    @SerializedName("score")
    val score: Int?
)