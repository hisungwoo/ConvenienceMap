package com.ilsamil.conveniencemap.repository

import com.ilsamil.conveniencemap.BuildConfig
import com.ilsamil.conveniencemap.model.FacInfoList
import com.ilsamil.conveniencemap.model.OpenAPIServiceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    companion object {
        const val FACL_BASE_URL = "http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/"
        const val EVALINFO_BASE_URL = "http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/"
    }

    @GET("getDisConvFaclList?serviceKey=${BuildConfig.OPEN_API_KEY}")
    fun getFaclList(@Query("numOfRows") numOfRows: Int,
                    @Query("faclNm") faclNm : String)
    : Call<FacInfoList>

    @GET("getFacInfoOpenApiJpEvalInfoList?serviceKey=${BuildConfig.OPEN_API_KEY}")
    fun getEvalInfoList(@Query("wfcltId") wfcltId : String)
    : Call<FacInfoList>

    @GET("getDisConvFaclList?serviceKey=${BuildConfig.OPEN_API_KEY}")
    fun getLocationFaclList(@Query("cggNm") cggNm : String,
                            @Query("roadNm") roadNm : String,
                            @Query("numOfRows") numOfRows : String)
    : Call<FacInfoList>

    @GET("getDisConvFaclList?serviceKey=${BuildConfig.OPEN_API_KEY}")
    suspend fun getLocationFaclList2(@Query("cggNm") cggNm : String,
                            @Query("numOfRows") numOfRows : String)
    : FacInfoList

    @GET("getFacInfoOpenApiJpEvalInfoList?serviceKey=${BuildConfig.OPEN_API_KEY}")
    suspend fun getEvalInfoList2(@Query("wfcltId") wfcltId : String)
    : FacInfoList

}