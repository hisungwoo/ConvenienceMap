package com.ilsamil.conveniencemap

import com.ilsamil.conveniencemap.model.FacInfoList
import com.ilsamil.conveniencemap.repository.RetrofitService
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import org.junit.Test

import retrofit2.Call
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
        var instance: Retrofit? = null
        instance = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
            .build()

        val aapi = instance.create(RetrofitService::class.java)
        val ttest : Call<FacInfoList> =aapi.getFaclList(1000, "")
        var st = ttest.execute().body()?.servList?.get(0)?.faclNm

        System.out.println("st = " + st)

    }

}