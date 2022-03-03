package com.ilsamil.conveniencemap

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ilsamil.conveniencemap.model.FacInfoList
import com.ilsamil.conveniencemap.repository.RetrofitService
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import kotlinx.coroutines.launch
import org.junit.Test

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val locationFaclService : RetrofitService
        val locationFaclInstance = Retrofit.Builder()
            .baseUrl(RetrofitService.FACL_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
            .build()
        locationFaclService = locationFaclInstance.create(RetrofitService::class.java)
        val facinfoCall : Call<FacInfoList> = locationFaclService.getLocationFaclList("구로구", "", "1000")
        System.out.println("시작")


        val dd = facinfoCall.execute().body()?.servList?.get(0)?.faclNm
        System.out.println("dd = " + dd)





    }

}