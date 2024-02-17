package ru.sample.duckapp.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import ru.sample.duckapp.domain.Duck
import okhttp3.ResponseBody

interface DucksApi {
    @GET("random")
    fun getRandomDuck(): Call<Duck>

    @GET("http/{code}")
    fun getCodeDuck(@Path("code") code: String): Call<ResponseBody>
}