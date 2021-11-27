package com.ilsamil.conveniencemap

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilsamil.conveniencemap.model.FacInfoList
import com.ilsamil.conveniencemap.model.ServList
import com.ilsamil.conveniencemap.repository.RetrofitService
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class MainViewModel : ViewModel() {
    val servLiveData = MutableLiveData<List<ServList>>()
    val bottomNavLiveData = MutableLiveData<Boolean>()
    private val service : RetrofitService

    init {
        var instance = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/B554287/DisabledPersonConvenientFacility/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
            .build()

        service = instance.create(RetrofitService::class.java)
    }

    fun searchFacl(searchText : String) {
        val facinfoCall : Call<FacInfoList> = service.getList(15, searchText)
        facinfoCall.enqueue(object : Callback<FacInfoList> {
            override fun onResponse(call: Call<FacInfoList>, response: Response<FacInfoList>) {
                if(response.isSuccessful()) {
                    val items = response.body()?.servList!!
                    Log.d("tttest" , "dd = 성공!!")
                    servLiveData.postValue(items)
//                    adapter.updateItems(items)

                } else { // code == 400
                    // 실패 처리
                    Log.d("tttest" , "dd = 실패")
                }
            }

            override fun onFailure(call: Call<FacInfoList>, t: Throwable) {
                Log.d("tttest" , "onFailure = " + t)
                t.printStackTrace()
            }

        })
    }



}