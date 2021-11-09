package com.ilsamil.conveniencemap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.ilsamil.conveniencemap.databinding.ActivityMainBinding
import com.ilsamil.conveniencemap.model.FacInfoList
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(this@MainActivity, "성공", Toast.LENGTH_SHORT ).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getAppKeyHash()

        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        val mapView = MapView(this)
        binding.clKakaoMapView.addView(mapView)

        var instance: Retrofit? = null

//        instance = Retrofit.Builder()
//            .baseUrl("http://api.visitkorea.or.kr/openapi/service/rest/KorService/")
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(TikXmlConverterFactory.create(TikXml.Builder().exceptionOnUnreadXml(false).build()))
//            .build()
//
//        val aapi = instance.create(RetrofitService::class.java)
//        val ttest : Call<FacInfoList> =aapi.getList(2)
//
//        ttest.enqueue(object : Callback<FacInfoList> {
//            override fun onResponse(call: Call<FacInfoList>, response: Response<FacInfoList>) {
//                if(response.isSuccessful()) { // <--> response.code == 200
//                    Log.d("tttest" , "dd = " + response.body()!!.totalCount)
//                    Log.d("tttest" , "dd = " + response.body()!!.servList[0].faclNm)
//                    Log.d("tttest" , "dd = " + response.body()!!.servList[1].faclNm)
//
//                } else { // code == 400
//                    // 실패 처리
//                    Log.d("tttest" , "dd = 실패")
//                }
//            }
//
//            override fun onFailure(call: Call<FacInfoList>, t: Throwable) {
//                Log.d("tttest" , "dd = 인터넷 실패")
//                t.printStackTrace()
//            }
//
//        })

    }

    private fun getAppKeyHash() {
        try {
            val info =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                var md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something = String(Base64.encode(md.digest(), 0))
                Log.e("Hash key", "#################################################" + something + "#################################################")
            }
        } catch (e: Exception) {

            Log.e("name not found", e.toString())
        }
    }



}