package com.ilsamil.conveniencemap

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilsamil.conveniencemap.model.EvalInfoList
import com.ilsamil.conveniencemap.model.FacInfoList
import com.ilsamil.conveniencemap.model.ServList
import com.ilsamil.conveniencemap.repository.RetrofitService
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException


class MainViewModel : ViewModel() {
    private val faclService : RetrofitService
    private val evalInfoService : RetrofitService
    private val locationFaclService : RetrofitService

    val categoryLiveData = MutableLiveData<Int>()

    val locationLiveData = MutableLiveData<Location?>()

    val searchFaclLiveData = MutableLiveData<List<ServList>?>()
    val evalInfoLiveData = MutableLiveData<List<EvalInfoList>>()
    val evalInfoDetailLiveData = MutableLiveData<List<EvalInfoList>>()
    val locationFaclLiveData = MutableLiveData<List<ServList>>()
    val locationFaclListLiveData = MutableLiveData<List<ServList>>()
    val mainStatus = MutableLiveData<Int>()

    val movePin = MutableLiveData<ServList>()

    val detailLiveData = MutableLiveData<ServList>()

    var isClickImg = false
    var isSelectMarker = false

    var shopServList = arrayListOf<ServList>()
    var livingServList = arrayListOf<ServList>()
    var educationServList = arrayListOf<ServList>()
    var publicServList = arrayListOf<ServList>()

    var mapServList = arrayListOf<ServList>()
    var mapCggNm = "지역"

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
    fun getLocationInfo(context: Context) : String {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // GPS_PROVIDER가 Null일 경우 NETWORK_PROVIDER를 가져온다.
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location != null) {
            locationLiveData.value = location
            val geocoder = Geocoder(context)
            try {
                val gList = geocoder.getFromLocation(location.latitude, location.longitude, 5)
                val cggNm = if (gList[0].subLocality != null) {
                    gList[0].subLocality
                } else {
                    gList[0].locality
                }
                mapCggNm = cggNm

                return cggNm

            } catch (e : IOException) {
                Log.d("debug_viewModel", "지오코드 오류 : " + e.printStackTrace())
            }
        } else {
            Log.d("debug_viewModel", "현재 위치 : null")
        }
        return "null"
    }


    fun getSearchFacl(searchText : String) {
        val facinfoCall : Call<FacInfoList> = faclService.getFaclList(1000, searchText)
        facinfoCall.enqueue(object : Callback<FacInfoList> {
            override fun onResponse(call: Call<FacInfoList>, response: Response<FacInfoList>) {
                if(response.isSuccessful()) {
                    if(response.body()?.totalCount == 0) {
                        Log.d("debug_viewModel" , "결과값 0개")
                        searchFaclLiveData.postValue(null)
                    } else {
                        val items = response.body()?.servList!!
                        searchFaclLiveData.postValue(items)
                    }

                } else { // code == 400
                    // 실패 처리
                    Log.d("debug_viewModel" , "dd = 실패")
                }
            }

            override fun onFailure(call: Call<FacInfoList>, t: Throwable) {
                Log.d("debug_viewModel" , "onFailure = " + t)
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
                    Log.d("debug_viewModel", evalinfo)

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
                    Log.d("debug_viewModel" , "dd = 실패")
                }
            }

            override fun onFailure(call: Call<FacInfoList>, t: Throwable) {
                Log.d("debug_viewModel" , "실패코드 : " + t)
                t.printStackTrace()
            }
        })
    }


    // 지도에 정보표시
    fun getLocationFacl(cggNm : String) {
        val facinfoCall : Call<FacInfoList> = locationFaclService.getLocationFaclList(cggNm, "", "1000")
        facinfoCall.enqueue(object : Callback<FacInfoList> {
            override fun onResponse(call: Call<FacInfoList>, response: Response<FacInfoList>) {
                if(response.isSuccessful()) {
                    val items = response.body()?.servList!!
                    locationFaclLiveData.postValue(items)

                } else { // code == 400
                    // 실패 처리
                    Log.d("debug_viewModel" , "dd = 실패")
                }
            }

            override fun onFailure(call: Call<FacInfoList>, t: Throwable) {
                Log.d("debug_viewModel" , "onFailure = " + t)
                t.printStackTrace()
            }

        })

//        viewModelScope.launch {
//            val facinfoCall = locationFaclService.getLocationFaclList2(cggNm, "1000")
//            val dd1 = facinfoCall.resultMessage
//            val dd2 = facinfoCall.totalCount
//            Log.d("debug_viewModel","resultMessage " + dd1)
//            Log.d("debug_viewModel","totalCount " + dd2)
//
//            val  items = facinfoCall.servList
//            locationFaclLiveData.postValue(items!!)
//
//
//
////            val dd1 = facinfoCall.cmmMsgHeader
////            val dd2 = dd1?.errMsg
////            val dd3 = dd1?.returnAuthMsg
////            val dd4 = dd1?.returnReasonCode
//
////            Log.d("debug_viewModel","errMsg " + dd2)
////            Log.d("debug_viewModel","returnAuthMsg " + dd3)
////            Log.d("debug_viewModel","returnReasonCode " + dd4)
//
//        }




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
                    Log.d("debug_viewModel" , "dd = 실패")
                }
            }

            override fun onFailure(call: Call<FacInfoList>, t: Throwable) {
                Log.d("debug_viewModel" , "onFailure = " + t)
                t.printStackTrace()
            }

        })
    }

}