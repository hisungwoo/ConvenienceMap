package com.ilsamil.conveniencemap

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilsamil.conveniencemap.model.EvalInfoList
import com.ilsamil.conveniencemap.model.FacInfoList
import com.ilsamil.conveniencemap.model.ServList
import com.ilsamil.conveniencemap.repository.RetrofitService
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import net.daum.mf.map.api.MapPOIItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


class MainViewModel : ViewModel() {
    private val faclService : RetrofitService
    private val evalInfoService : RetrofitService
    private val locationFaclService : RetrofitService

    val categoryLiveData = MutableLiveData<Int>()

    val faclLiveData = MutableLiveData<List<ServList>>()
    val evalInfoLiveData = MutableLiveData<List<EvalInfoList>>()
    val evalInfoDetailLiveData = MutableLiveData<List<EvalInfoList>>()
    val locationFaclLiveData = MutableLiveData<List<ServList>>()
    val locationFaclListLiveData = MutableLiveData<List<ServList>>()
    val bottomNavLiveData = MutableLiveData<Boolean>()
    val mainStatus = MutableLiveData<Int>()

    val movePin = MutableLiveData<ServList>()

    val detailLiveData = MutableLiveData<ServList>()

    var clickImgStatus = false
    var selectMarkerStatus = false


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

    @SuppressLint("MissingPermission")
    fun getLocationFacInfo2(context: Context) : String {
        val locationManager = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        var latitude = ""
        var longitude = ""

        if(location != null) {
            latitude = location.latitude.toString()
            longitude = location.longitude.toString()
            Log.d("ttest", "latitude : " + latitude)
            Log.d("ttest", "longitude : " + longitude)
        } else {
            Log.d("ttest", "현재 위치 : null")
        }

        return  latitude + longitude

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

    fun getEvalInfo(wfcltId : String, type : String) {
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

                    if(type == "1") {
                        evalInfoLiveData.postValue(evalinfoList)
                    } else if (type == "2") {
                        evalInfoDetailLiveData.postValue(evalinfoList)
                    }


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


    // 지도에 정보표시
    fun getLocationFacl(cggNm : String, roadNm : String) {
        val facinfoCall : Call<FacInfoList> = locationFaclService.getLocationFaclList(cggNm, "", "1000")
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

    // 리스트 프레그먼트 - 지도에 정보표시
    fun getLocationListFacl(cggNm : String) {
        val facinfoCall : Call<FacInfoList> = locationFaclService.getLocationFaclList(cggNm, "", "1000")
        facinfoCall.enqueue(object : Callback<FacInfoList> {
            override fun onResponse(call: Call<FacInfoList>, response: Response<FacInfoList>) {
                if(response.isSuccessful()) {
                    val items = response.body()?.servList!!
                    locationFaclListLiveData.postValue(items)

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