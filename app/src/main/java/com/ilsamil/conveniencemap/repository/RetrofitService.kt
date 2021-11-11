package com.ilsamil.conveniencemap.repository

import com.ilsamil.conveniencemap.model.FacInfoList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

public interface RetrofitService {
    @GET("http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/getDisConvFaclList?serviceKey=E6PZth5Xxp14kb9K%2BcdqMVPdltgGfmjR5OY8gEi1ARAV7mibmfWj7lq54rPJx0wiWoNJ0jZHAyMMsto875iTPw%3D%3D")
    fun getList(@Query("numOfRows") numOfRows: Int) : Call<FacInfoList>
}