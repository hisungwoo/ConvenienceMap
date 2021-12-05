package com.ilsamil.conveniencemap.repository

import com.ilsamil.conveniencemap.model.FacInfoList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    companion object {
        const val FACL_BASE_URL = "http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/"
        const val EVALINFO_BASE_URL = "http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/"
    }

    @GET("getDisConvFaclList?serviceKey=E6PZth5Xxp14kb9K%2BcdqMVPdltgGfmjR5OY8gEi1ARAV7mibmfWj7lq54rPJx0wiWoNJ0jZHAyMMsto875iTPw%3D%3D")
    fun getFaclList(@Query("numOfRows") numOfRows: Int,
                    @Query("faclNm") faclNm : String)
    : Call<FacInfoList>

    @GET("getFacInfoOpenApiJpEvalInfoList?serviceKey=E6PZth5Xxp14kb9K%2BcdqMVPdltgGfmjR5OY8gEi1ARAV7mibmfWj7lq54rPJx0wiWoNJ0jZHAyMMsto875iTPw%3D%3D")
    fun getEvalInfoList(@Query("wfcltId") wfcltId : String)
    : Call<FacInfoList>

    @GET("getDisConvFaclList?serviceKey=E6PZth5Xxp14kb9K%2BcdqMVPdltgGfmjR5OY8gEi1ARAV7mibmfWj7lq54rPJx0wiWoNJ0jZHAyMMsto875iTPw%3D%3D")
    fun getLocationFaclList(@Query("cggNm") cggNm : String,
                            @Query("roadNm") roadNm : String)
    : Call<FacInfoList>

}