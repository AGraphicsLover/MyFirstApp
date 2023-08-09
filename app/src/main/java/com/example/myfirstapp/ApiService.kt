import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
  @FormUrlEncoded
  @POST("user/login")
  fun login(
    @Field("username") username: String,
    @Field("password") password: String
  ): Call<ApiResponse<UserData>>
}