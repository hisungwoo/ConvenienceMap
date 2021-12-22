package com.ilsamil.conveniencemap

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilsamil.conveniencemap.model.EvalInfoList
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
    private val faclService : RetrofitService
    private val evalInfoService : RetrofitService
    private val locationFaclService : RetrofitService

    val faclLiveData = MutableLiveData<List<ServList>>()
    val evalInfoLiveData = MutableLiveData<List<EvalInfoList>>()
    val locationFaclLiveData = MutableLiveData<List<ServList>>()
    val bottomNavLiveData = MutableLiveData<Boolean>()
    val mainStatus = MutableLiveData<Int>()

    val movePin = MutableLiveData<ServList>()


    init {
        val faclInstance = Retrofit.Builder()
            .baseUrl(RetrofitService.FACL_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
            .build()
        faclService = faclInstance.create(RetrofitService::class.java)

        val evalInfoInstance = Retrofit.Builder()
            .baseUrl(RetrofitService.EVALINFO_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
            .build()
        evalInfoService = evalInfoInstance.create(RetrofitService::class.java)

        val locationFaclInstance = Retrofit.Builder()
            .baseUrl(RetrofitService.FACL_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
            .build()
        locationFaclService = locationFaclInstance.create(RetrofitService::class.java)

    }

    fun getFacl(searchText : String) {
        val facinfoCall : Call<FacInfoList> = faclService.getFaclList(15, searchText)
        facinfoCall.enqueue(object : Callback<FacInfoList> {
            override fun onResponse(call: Call<FacInfoList>, response: Response<FacInfoList>) {
                if(response.isSuccessful()) {
                    val items = response.body()?.servList!!
                    faclLiveData.postValue(items)

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

    fun getEvalInfo(wfcltId : String) {
        val evalInfoCall : Call<FacInfoList> = evalInfoService.getEvalInfoList(wfcltId)
        evalInfoCall.enqueue(object : Callback<FacInfoList> {
            override fun onResponse(call: Call<FacInfoList>, response: Response<FacInfoList>) {
                if(response.isSuccessful()) {
                    val items = response.body()?.servList!!
                    var evalinfo = items[0].evalInfo.toString()
                    Log.d("ttest", evalinfo)

                    val evalinfoList = arrayListOf<EvalInfoList>()
                    var evalinfos = evalinfo.split(",")

                    for (i in evalinfos) {
                        evalinfoList.add(EvalInfoList(i.trim()))
                    }

                    evalInfoLiveData.postValue(evalinfoList)


                } else { // code == 400
                    // 실패 처리
                    Log.d("tttest" , "dd = 실패")
                }
            }

            override fun onFailure(call: Call<FacInfoList>, t: Throwable) {
                Log.d("tttest" , "실패코드 : " + t)
                t.printStackTrace()
            }
        })
    }


    // 파라미터 주소의 정보 표시
    fun getLocationFacl(cggNm : String, roadNm : String) {
        val facinfoCall : Call<FacInfoList> = locationFaclService.getLocationFaclList(cggNm, roadNm)
        facinfoCall.enqueue(object : Callback<FacInfoList> {
            override fun onResponse(call: Call<FacInfoList>, response: Response<FacInfoList>) {
                if(response.isSuccessful()) {
                    val items = response.body()?.servList!!
                    locationFaclLiveData.postValue(items)

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